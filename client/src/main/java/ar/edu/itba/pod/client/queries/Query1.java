package ar.edu.itba.pod.client.queries;

import static ar.edu.itba.pod.client.QueryUtils.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;

import ar.edu.itba.pod.client.QueryMetrics;
import ar.edu.itba.pod.utils.keyPredicates.HazelcastCollectionExtractor;
import ar.edu.itba.pod.query1.Q1Answer;
import ar.edu.itba.pod.utils.combiners.CountCombinerFactory;
import ar.edu.itba.pod.utils.collators.SortCollator;
import ar.edu.itba.pod.query1.Q1Mapper;
import ar.edu.itba.pod.utils.reducers.CountReducerFactory;
import ar.edu.itba.pod.utils.keyPredicates.CollectionContainsKeyPredicate;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

public final class Query1 {
    private Query1() {
        // static
    }

    private static final String JOB_TRACKER_NAME     = hazelcastNamespace("q1-hoods-name-set");
    private static final String HOODS_NAME_SET_NAME  = hazelcastNamespace("q1-hoods-name-set");
    private static final String TREE_MAP_NAME        = hazelcastNamespace("q1-tree-map");

    private static final Comparator<Q1Answer> ANSWER_ORDER = Comparator
        .comparingInt   (Q1Answer::getTreeCount).reversed()
        .thenComparing  (Q1Answer::getHood)
        ;

    public static final String CSV_HEADER = csvHeaderJoiner()
        .add("NEIGHBOURHOOD")
        .add("TREES")
        .toString()
        ;

    public static void writeAnswerToCsv(final Writer writer, final Q1Answer answer) throws IOException {
        writer.write(answer.getHood());
        writer.write(OUT_DELIM);
        writer.write(Integer.toString(answer.getTreeCount()));
        writer.write(NEW_LINE);
    }

    public static QueryMetrics executeToCSV(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Writer queryOut) throws IOException, ExecutionException, InterruptedException {

        queryOut.write(CSV_HEADER);
        return queryAnswersToCSV(Query1::execute, hazelcast, trees, hoods, answer -> writeAnswerToCsv(queryOut, answer));
    }

    public static QueryMetrics execute(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Consumer<Q1Answer> callback) throws ExecutionException, InterruptedException {

        final QueryMetrics.Builder metrics = QueryMetrics.build();

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(TREE_MAP_NAME);
        treeMap.clear();

        final Set<String> hoodsName = hazelcast.getSet(HOODS_NAME_SET_NAME);
        hoodsName.clear();

        metrics.recordInputProcessingStart();

        trees.forEach(tree -> treeMap.put(tree.getHoodName(), tree));
        hoods.map(Neighbourhood::getName).forEach(hoodsName::add);

        metrics.recordInputProcessingEnd();

        final Job<String, Tree> job = hazelcast
            .getJobTracker(JOB_TRACKER_NAME)
            .newJob(KeyValueSource.fromMultiMap(treeMap))
            ;

        metrics.recordMapReduceJobStart();

        job
            .keyPredicate   (new CollectionContainsKeyPredicate<>(HOODS_NAME_SET_NAME, HazelcastCollectionExtractor.SET))
            .mapper         (new Q1Mapper())
            .combiner       (new CountCombinerFactory())
            .reducer        (new CountReducerFactory())
            .submit         (new SortCollator<>(Q1Answer::fromEntry, ANSWER_ORDER, callback))
            .get            ()
            ;

        metrics.recordMapReduceJobEnd();

        // Limpiamos recursos usados
        treeMap.clear();
        hoodsName.clear();

        return metrics.build();
    }
}
