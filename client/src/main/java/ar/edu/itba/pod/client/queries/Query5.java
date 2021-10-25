package ar.edu.itba.pod.client.queries;

import ar.edu.itba.pod.client.QueryMetrics;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query5.*;
import ar.edu.itba.pod.utils.collators.MapCollator;
import ar.edu.itba.pod.utils.collators.SortPreSortedValuesCollator;
import ar.edu.itba.pod.utils.combiners.CountCombinerFactory;
import ar.edu.itba.pod.utils.combiners.ValueSetCombinerFactory;
import ar.edu.itba.pod.utils.reducers.CountReducerFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.KeyValueSource;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static ar.edu.itba.pod.client.QueryUtils.*;
import static ar.edu.itba.pod.client.QueryUtils.queryAnswersToCSV;

public final class Query5 {
    private Query5() {
        //Static
    }

    public  static final String PROPERTY_NEIGHBOURHOOD  = "neighbourhood";
    public  static final String PROPERTY_SPECIES        = "commonName";

    private static final String JOB_TRACKER_1_NAME      = hazelcastNamespace("q5-job-1-tracker");
    private static final String JOB_TRACKER_2_NAME      = hazelcastNamespace("q5-job-2-tracker");
    private static final String TREE_MAP_NAME           = hazelcastNamespace("q5-tree-map");
    private static final String STREET_COUNT_LIST_NAME  = hazelcastNamespace("q5-street-count-list");

    private static final Comparator<Integer> GROUPS_ORDER = Comparator.reverseOrder();

    public static final String CSV_HEADER = csvHeaderJoiner()
        .add("GROUP")
        .add("STREET A")
        .add("STREET B")
        .toString()
        ;

    public static void writeAnswerToCsv(final Writer writer, final Q5Answer answer) throws IOException {
        writer.write(Integer.toString(answer.getGroup()));
        writer.write(OUT_DELIM);
        writer.write(answer.getStreetA());
        writer.write(OUT_DELIM);
        writer.write(answer.getStreetB());
        writer.write(NEW_LINE);
    }

    public static QueryMetrics executeToCSV(
            final HazelcastInstance hazelcast,
            final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
            final Writer csvOut) throws IOException, ExecutionException, InterruptedException {

        csvOut.write(CSV_HEADER);
        return queryAnswersToCSV(Query5::execute, hazelcast, trees, hoods, answer -> writeAnswerToCsv(csvOut, answer));
    }

    public static QueryMetrics execute(
            final HazelcastInstance hazelcast,
            final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
            final Consumer<Q5Answer> callback) throws ExecutionException, InterruptedException {

        final String hood       = getRequiredProperty(PROPERTY_NEIGHBOURHOOD);
        final String species    = getRequiredProperty(PROPERTY_SPECIES);

        final QueryMetrics.Builder metrics = QueryMetrics.build();

        final MultiMap<Tree, Integer> treeMap = hazelcast.getMultiMap(TREE_MAP_NAME);
        treeMap.clear();

        final IList<Q5TransitionalAnswer> streetCount = hazelcast.getList(STREET_COUNT_LIST_NAME);
        streetCount.clear();

        metrics.recordInputProcessingStart();

        trees.forEach(tree -> treeMap.put(tree, 1));

        metrics.recordInputProcessingEnd();
        metrics.recordMapReduceJobStart();

        hazelcast
            .getJobTracker  (JOB_TRACKER_1_NAME)
            .newJob         (KeyValueSource.fromMultiMap(treeMap))
            .keyPredicate   (new Q5KeyPredicate(hood, species))
            .mapper         (new Q5FirstMapper())
            .combiner       (new CountCombinerFactory())
            .reducer        (new CountReducerFactory())
            .submit         (new MapCollator<>(Q5TransitionalAnswer::fromEntry, streetCount::add))
            .get            ()
            ;

        hazelcast
            .getJobTracker  (JOB_TRACKER_2_NAME)
            .newJob         (KeyValueSource.fromList(streetCount))
            .mapper         (new Q5SecondMapper())
            .combiner       (new ValueSetCombinerFactory<>())
            .reducer        (new Q5ReducerFactory())
            .submit         (new SortPreSortedValuesCollator<>(GROUPS_ORDER, callback))
            .get            ()
            ;

        metrics.recordMapReduceJobEnd();

        // Limpiamos recursos usados
        treeMap     .clear();
        streetCount .clear();

        return metrics.build();
    }
}
