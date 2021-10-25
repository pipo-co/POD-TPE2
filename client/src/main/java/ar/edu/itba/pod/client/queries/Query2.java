package ar.edu.itba.pod.client.queries;

import static ar.edu.itba.pod.client.QueryUtils.*;

import java.io.IOException;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.KeyValueSource;

import ar.edu.itba.pod.client.QueryMetrics;
import ar.edu.itba.pod.utils.collators.SortCollator;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query2.Q2Answer;
import ar.edu.itba.pod.query2.Q2CombinerFactory;
import ar.edu.itba.pod.query2.Q2Mapper;
import ar.edu.itba.pod.query2.Q2ReducerFactory;
import ar.edu.itba.pod.utils.keyPredicates.MapContainsKeyPredicate;

public final class Query2 {
    private Query2() {
        // static
    }

    private static final String JOB_TRACKER_NAME    = hazelcastNamespace("q2-job-tracker");
    private static final String TREE_MAP_NAME       = hazelcastNamespace("q2-tree-map");
    private static final String HOOD_MAP_NAME       = hazelcastNamespace("q2-hood-map");

    private static final Comparator<Q2Answer> ANSWER_ORDER = Comparator.comparing(Q2Answer::getHoodName);

    public static final String CSV_HEADER = csvHeaderJoiner()
        .add("NEIGHBOURHOOD")
        .add("COMMON_NAME")
        .add("TREES_PER_PEOPLE")
        .toString()
        ;

    // Exactamente 2 decimales truncando. El primero es un 0 y no # para que nunca arranque con . (ej, .27)
    // Cableamos Locale para que no sea system dependant
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(DEFAULT_LOCALE));
    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.DOWN);
    }

    public static void writeAnswerToCsv(final Writer writer, final Q2Answer answer) throws IOException {
        writer.write(answer.getHoodName());
        writer.write(OUT_DELIM);
        writer.write(answer.getTreeName());
        writer.write(OUT_DELIM);
        writer.write(DECIMAL_FORMAT.format(answer.getTreesPerInhabitant()));
        writer.write(NEW_LINE);
    }

    public static QueryMetrics executeToCSV(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Writer csvOut) throws IOException, ExecutionException, InterruptedException {

        csvOut.write(CSV_HEADER);
        return queryAnswersToCSV(Query2::execute, hazelcast, trees, hoods, answer -> writeAnswerToCsv(csvOut, answer));
    }

    public static QueryMetrics execute(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Consumer<Q2Answer> callback) throws ExecutionException, InterruptedException {

        final QueryMetrics.Builder metrics = QueryMetrics.build();

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(TREE_MAP_NAME);
        treeMap.clear();

        final Map<String, Neighbourhood> hoodMap = hazelcast.getMap(HOOD_MAP_NAME);
        hoodMap.clear();

        metrics.recordInputProcessingStart();

        trees.forEach(tree -> treeMap.put(tree.getHoodName(), tree));
        hoods.forEach(hood -> hoodMap.put(hood.getName(), hood));

        metrics.recordInputProcessingEnd();
        metrics.recordMapReduceJobStart();

        hazelcast
            .getJobTracker  (JOB_TRACKER_NAME)
            .newJob         (KeyValueSource.fromMultiMap(treeMap))
            .keyPredicate   (new MapContainsKeyPredicate<>(HOOD_MAP_NAME, MapContainsKeyPredicate.MapCollection.KEYS))
            .mapper         (new Q2Mapper(HOOD_MAP_NAME))
            .combiner       (new Q2CombinerFactory())
            .reducer        (new Q2ReducerFactory())
            .submit         (new SortCollator<>(Map.Entry::getValue, ANSWER_ORDER, callback))
            .get            ()
            ;

        metrics.recordMapReduceJobEnd();

        // Limpiamos recursos usados
        treeMap.clear();
        hoodMap.clear();

        return metrics.build();
    }
}
