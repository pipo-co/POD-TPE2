package ar.edu.itba.pod.client.queries;

import static ar.edu.itba.pod.client.QueryUtils.*;

import java.io.IOException;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;

import ar.edu.itba.pod.CollectionContainsKeyPredicate;
import ar.edu.itba.pod.HazelcastCollectionExtractor;
import ar.edu.itba.pod.SortedListCollator;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query2.Q2Answer;
import ar.edu.itba.pod.query2.Q2CombinerFactory;
import ar.edu.itba.pod.query2.Q2Mapper;
import ar.edu.itba.pod.query2.Q2ReducerFactory;

public final class Query2 {
    private Query2() {
        // static
    }

    private static final Comparator<Q2Answer> ANSWER_ORDER = Comparator.comparing(Q2Answer::getHoodName);

    private static final String CSV_HEADER = csvHeaderJoiner()
        .add("NEIGHBOURHOOD")
        .add("COMMON_NAME")
        .add("TREES_PER_PEOPLE")
        .toString()
        ;

    // Exactamente 2 decimales truncando. El primero es un 0 y no # para que nunca arranque con . (ej, .27)
    // Cableamos Locale para que no sea system dependant
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(OUTPUT_LOCALE));
    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.DOWN);
    }

    private static void writeAnswerToCsv(final Writer writer, final Q2Answer answer) throws IOException {
        writer.write(answer.getHoodName());
        writer.write(OUT_DELIM);
        writer.write(answer.getTreeName());
        writer.write(OUT_DELIM);
        writer.write(DECIMAL_FORMAT.format(answer.getTreesPerInhabitant()));
        writer.write(NEW_LINE);
    }

    public static void execute(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Writer queryOut, final Writer timeOut) throws IOException, ExecutionException, InterruptedException {

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(hazelcastNamespace("q2-tree-map"));
        treeMap.clear();

        final String hoodMapName = hazelcastNamespace("q2-hood-map");
        final Map<String, Neighbourhood> hoodMap = hazelcast.getMap(hoodMapName);
        hoodMap.clear();

        logInputProcessingStart(timeOut);

        trees.forEach(tree -> treeMap.put(tree.getHoodName(), tree));
        hoods.forEach(hood -> hoodMap.put(hood.getName(), hood));

        logInputProcessingEnd(timeOut);

        final Job<String, Tree> job = hazelcast
            .getJobTracker(hazelcastNamespace("q2-job-tracker"))
            .newJob(KeyValueSource.fromMultiMap(treeMap))
            ;

        logMapReduceJobStart(timeOut);

        final ICompletableFuture<List<Q2Answer>> future = job
            .keyPredicate   (new CollectionContainsKeyPredicate<>(hoodMapName, HazelcastCollectionExtractor.MAP_KEYS))
            .mapper         (new Q2Mapper(hoodMapName))
            .combiner       (new Q2CombinerFactory())
            .reducer        (new Q2ReducerFactory())
            .submit         (new SortedListCollator<>(Map.Entry::getValue, ANSWER_ORDER))
            ;
        
        final List<Q2Answer> answers = future.get();

        queryOut.write(CSV_HEADER);
        for(final Q2Answer answer : answers) {
            writeAnswerToCsv(queryOut, answer);
        }

        logMapReduceJobEnd(timeOut);

        // Limpiamos recursos usados
        treeMap.clear();
        hoodMap.clear();
    }
}
