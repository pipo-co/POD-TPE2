package ar.edu.itba.pod.client.queries;

import static ar.edu.itba.pod.client.QueryUtils.*;

import static ar.edu.itba.pod.client.QueryUtils.hazelcastNamespace;
import static ar.edu.itba.pod.client.QueryUtils.logInputProcessingEnd;
import static ar.edu.itba.pod.client.QueryUtils.logInputProcessingStart;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;

import ar.edu.itba.pod.CollectionContainsKeyPredicate;
import ar.edu.itba.pod.ValueSetCombinerFactory;
import ar.edu.itba.pod.DistinctValuesCountReducerFactory;
import ar.edu.itba.pod.HazelcastCollectionExtractor;
import ar.edu.itba.pod.query3.Q3Mapper;
import ar.edu.itba.pod.SortedListCollator;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query3.Q3Answer;

public final class Query3 {
    private Query3() {
        // static
    }

    public static final String PROPERTY_ANSWER_COUNT = "n";
    public static final String INVALID_ANSWER_COUNT_MSG = "'" + PROPERTY_ANSWER_COUNT + "' parameter must be a positive integer";

    private static final Comparator<Q3Answer> ANSWER_ORDER = Comparator.comparing(Q3Answer::getDistinctSpecies).reversed();

    private static final String CSV_HEADER = csvHeaderJoiner()
        .add("NEIGHBOURHOOD")
        .add("COMMON_NAME_COUNT")
        .toString()
        ;

    private static void writeAnswerToCsv(final Writer writer, final Q3Answer answer) throws IOException {
        writer.write(answer.getHoodName());
        writer.write(OUT_DELIM);
        writer.write(Integer.toString(answer.getDistinctSpecies()));
        writer.write(NEW_LINE);
    }

    private static int parseAnswerCount(final String answerCount) {
        final int ret;
        if(answerCount == null) {
            throw new IllegalArgumentException("'" + PROPERTY_ANSWER_COUNT + "' parameter is required");
        }

        try {
            ret = Integer.parseInt(answerCount);
        } catch(final NumberFormatException e) {
            throw new IllegalArgumentException(INVALID_ANSWER_COUNT_MSG);
        }

        if(ret <= 0) {
            throw new IllegalArgumentException(INVALID_ANSWER_COUNT_MSG);
        }

        return ret;
    }

    public static void execute(
            final HazelcastInstance hazelcast,
            final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
            final Writer queryOut, final Writer timeOut) throws IOException, ExecutionException, InterruptedException {

        final int answerCount = parseAnswerCount(System.getProperty(PROPERTY_ANSWER_COUNT));

        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(hazelcastNamespace("q3-tree-map"));
        treeMap.clear();

        final String hoodsNameSetName = hazelcastNamespace("q3-hoods-name-set");
        final Set<String> hoodsName = hazelcast.getSet(hoodsNameSetName);
        hoodsName.clear();

        logInputProcessingStart(timeOut);

        trees.forEach(tree -> treeMap.put(tree.getHoodName(), tree));
        hoods.map(Neighbourhood::getName).forEach(hoodsName::add);

        logInputProcessingEnd(timeOut);

        final Job<String, Tree> job = hazelcast
            .getJobTracker(hazelcastNamespace("q3-job-tracker"))
            .newJob(KeyValueSource.fromMultiMap(treeMap))
            ;
        
        logMapReduceJobStart(timeOut);

        final ICompletableFuture<List<Q3Answer>> future = job
            .keyPredicate   (new CollectionContainsKeyPredicate<>(hoodsNameSetName, HazelcastCollectionExtractor.SET))
            .mapper         (new Q3Mapper())
            .combiner       (new ValueSetCombinerFactory<>())
            .reducer        (new DistinctValuesCountReducerFactory<>())
            .submit         (new SortedListCollator<>(Q3Answer::fromEntry, ANSWER_ORDER))
            ;
    
        final List<Q3Answer> answers = future.get();

        queryOut.write(CSV_HEADER);
        for(final Q3Answer answer : answers.subList(0, Math.min(answers.size(), answerCount))) {
            writeAnswerToCsv(queryOut, answer);
        }
        
        logMapReduceJobEnd(timeOut);

        // Limpiamos recursos usados
        treeMap.clear();
        hoodsName.clear();
    }
}
