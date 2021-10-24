package ar.edu.itba.pod.client.queries;

import ar.edu.itba.pod.client.QueryMetrics;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query5.*;
import ar.edu.itba.pod.utils.collators.MapCollator;
import ar.edu.itba.pod.utils.collators.SortPreSortedValuesCollator;
import ar.edu.itba.pod.utils.combiners.CountCombinerFactory;
import ar.edu.itba.pod.utils.combiners.ValueSetCombinerFactory;
import ar.edu.itba.pod.utils.keyPredicates.CollectionContainsKeyPredicate;
import ar.edu.itba.pod.utils.keyPredicates.HazelcastCollectionExtractor;
import ar.edu.itba.pod.utils.mappers.NopMapper;
import ar.edu.itba.pod.utils.reducers.CountReducerFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.util.StringUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static ar.edu.itba.pod.client.QueryUtils.*;
import static ar.edu.itba.pod.client.QueryUtils.queryAnswersToCSV;

public class Query5 {
    private Query5() {
        //Static
    }

    private static final String JOB_TRACKER_1_NAME      = hazelcastNamespace("q5-job-1-tracker");
    private static final String JOB_TRACKER_2_NAME      = hazelcastNamespace("q5-job-2-tracker");
    private static final String TREE_MAP_NAME           = hazelcastNamespace("q5-tree-map");
    private static final String STREET_COUNT_LIST_NAME  = hazelcastNamespace("q5-street-count-list");
    public  static final String PROPERTY_NEIGHBOURHOOD  = "neighbourhood";
    public  static final String PROPERTY_SPECIES        = "commonName";
    public  static final String INVALID_PARAM_EXCEPTION = " parameter can't be null or empty";

    private static final Comparator<Integer> GROUPS_ORDER = Comparator.reverseOrder();

    public static final String CSV_HEADER = csvHeaderJoiner()
            .add("GROUP")
            .add("STREET A")
            .add("STREET B")
            .toString()
            ;

    private static String verifyStringParameter(final String param, final String paramName) {
        if (StringUtil.isNullOrEmpty(param)){
            throw new IllegalArgumentException(paramName + INVALID_PARAM_EXCEPTION);
        }
        return param;
    }

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

        final String neighbourhood = verifyStringParameter(System.getProperty(PROPERTY_NEIGHBOURHOOD), PROPERTY_NEIGHBOURHOOD);

        final String species = verifyStringParameter(System.getProperty(PROPERTY_SPECIES), PROPERTY_SPECIES);

        final QueryMetrics.Builder metrics = QueryMetrics.build();

        final MultiMap<String, Integer> streetMap = hazelcast.getMultiMap(TREE_MAP_NAME);
        streetMap.clear();

        final IList<Q5PartialAnswer> streetCount = hazelcast.getList(STREET_COUNT_LIST_NAME);
        streetCount.clear();

        metrics.recordInputProcessingStart();

        trees.filter(tree -> tree.getHoodName().equals(neighbourhood))
             .filter(tree -> tree.getName().equals(species))
             .forEach(tree -> streetMap.put(tree.getHoodStreet(), 1));

        metrics.recordInputProcessingEnd();

        final Job<String, Integer> job1 = hazelcast
                .getJobTracker(JOB_TRACKER_1_NAME)
                .newJob(KeyValueSource.fromMultiMap(streetMap))
                ;

        metrics.recordMapReduceJobStart();

        job1
                .mapper         (new NopMapper<>())
                .combiner       (new CountCombinerFactory())
                .reducer        (new CountReducerFactory())
                .submit         (new MapCollator<>(Q5PartialAnswer::fromEntry, streetCount::add))
                .get            ()
        ;

        final Job<String, Q5PartialAnswer> job2 = hazelcast
                .getJobTracker(JOB_TRACKER_2_NAME)
                .newJob(KeyValueSource.fromList(streetCount))
                ;

        job2
                .mapper     (new Q5Mapper())
                .combiner   (new ValueSetCombinerFactory<>())
                .reducer    (new Q5ReducerFactory())
                .submit     (new SortPreSortedValuesCollator<>(GROUPS_ORDER, callback))
                .get        ()
        ;

        metrics.recordMapReduceJobEnd();

        // Limpiamos recursos usados
        streetMap.clear();
        streetCount.clear();

        return metrics.build();
    }
}
