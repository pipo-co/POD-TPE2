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
import com.hazelcast.mapreduce.KeyValueSource;

import ar.edu.itba.pod.client.QueryMetrics;
import ar.edu.itba.pod.utils.keyPredicates.CollectionContainsKeyPredicate;
import ar.edu.itba.pod.utils.combiners.ValueSetCombinerFactory;
import ar.edu.itba.pod.utils.reducers.DistinctValuesCountReducerFactory;
import ar.edu.itba.pod.utils.keyPredicates.HazelcastCollectionExtractor;
import ar.edu.itba.pod.query3.Q3Mapper;
import ar.edu.itba.pod.utils.collators.SortCollator;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query3.Q3Answer;

public final class Query3 {
    private Query3() {
        // static
    }

    public  static final String PROPERTY_ANSWER_COUNT   = "n";
    public  static final int    DEFAULT_ANSWER_COUNT    = Integer.MAX_VALUE;

    private static final String JOB_TRACKER_NAME        = hazelcastNamespace("q3-job-tracker");
    private static final String TREE_MAP_NAME           = hazelcastNamespace("q3-tree-map");
    private static final String HOODS_NAME_SET_NAME     = hazelcastNamespace("q3-hoods-name-set");

    private static final Comparator<Q3Answer> ANSWER_ORDER = Comparator.comparing(Q3Answer::getDistinctSpecies).reversed().thenComparing(Q3Answer::getHoodName);

    public static final String CSV_HEADER = csvHeaderJoiner()
        .add("NEIGHBOURHOOD")
        .add("COMMON_NAME_COUNT")
        .toString()
        ;

    public static void writeAnswerToCsv(final Writer writer, final Q3Answer answer) throws IOException {
        writer.write(answer.getHoodName());
        writer.write(OUT_DELIM);
        writer.write(Integer.toString(answer.getDistinctSpecies()));
        writer.write(NEW_LINE);
    }

    public static QueryMetrics executeToCSV(
        final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final Writer csvOut) throws IOException, ExecutionException, InterruptedException {

        csvOut.write(CSV_HEADER);
        return queryAnswersToCSV(Query3::execute, hazelcast, trees, hoods, answer -> writeAnswerToCsv(csvOut, answer));
    }

    public static QueryMetrics execute(
            final HazelcastInstance hazelcast,
            final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
            final Consumer<Q3Answer> callback) throws ExecutionException, InterruptedException {

        final int answerCount = getPositiveIntProperty(PROPERTY_ANSWER_COUNT, DEFAULT_ANSWER_COUNT);

        final QueryMetrics.Builder metrics = QueryMetrics.build();

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(TREE_MAP_NAME);
        treeMap.clear();

        final Set<String> hoodsName = hazelcast.getSet(HOODS_NAME_SET_NAME);
        hoodsName.clear();

        metrics.recordInputProcessingStart();

        trees.forEach(tree -> treeMap.put(tree.getHoodName(), tree));
        hoods.map(Neighbourhood::getName).forEach(hoodsName::add);

        metrics.recordInputProcessingEnd();
        metrics.recordMapReduceJobStart();

        hazelcast
            .getJobTracker  (JOB_TRACKER_NAME)
            .newJob         (KeyValueSource.fromMultiMap(treeMap))
            .keyPredicate   (new CollectionContainsKeyPredicate<>(HOODS_NAME_SET_NAME, HazelcastCollectionExtractor.SET))
            .mapper         (new Q3Mapper())
            .combiner       (new ValueSetCombinerFactory<>())
            .reducer        (new DistinctValuesCountReducerFactory<>())
            .submit         (new SortCollator<>(Q3Answer::fromEntry, ANSWER_ORDER, 0, answerCount, callback))
            .get            ()
            ;

        metrics.recordMapReduceJobEnd();

        // Limpiamos recursos usados
        treeMap     .clear();
        hoodsName   .clear();

        return metrics.build();
    }
}
