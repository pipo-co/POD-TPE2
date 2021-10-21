package ar.edu.itba.pod.client.queries;

import static ar.edu.itba.pod.client.QueryUtils.NEW_LINE;
import static ar.edu.itba.pod.client.QueryUtils.OUT_DELIM;
import static ar.edu.itba.pod.client.QueryUtils.csvHeaderJoiner;
import static ar.edu.itba.pod.client.QueryUtils.hazelcastNamespace;
import static ar.edu.itba.pod.client.QueryUtils.logInputProcessingEnd;
import static ar.edu.itba.pod.client.QueryUtils.logInputProcessingStart;
import static ar.edu.itba.pod.client.QueryUtils.logMapReduceJobEnd;
import static ar.edu.itba.pod.client.QueryUtils.logMapReduceJobStart;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;

import ar.edu.itba.pod.utils.keyPredicates.CollectionContainsKeyPredicate;
import ar.edu.itba.pod.utils.collators.SortPreSortedValuesCollator;
import ar.edu.itba.pod.utils.collators.MapCollator;
import ar.edu.itba.pod.utils.combiners.ValueSetCombinerFactory;
import ar.edu.itba.pod.utils.reducers.DistinctValuesCountReducerFactory;
import ar.edu.itba.pod.utils.keyPredicates.HazelcastCollectionExtractor;
import ar.edu.itba.pod.query3.Q3Mapper;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query3.Q3Answer;
import ar.edu.itba.pod.query4.Q4Answer;
import ar.edu.itba.pod.query4.Q4Mapper;
import ar.edu.itba.pod.query4.Q4ReducerFactory;

public final class Query4 {
    private Query4() {
        //Static
    }

    private static final String JOB_TRACKER_1_NAME      = hazelcastNamespace("q4-job-1-tracker");
    private static final String JOB_TRACKER_2_NAME      = hazelcastNamespace("q4-job-2-tracker");
    private static final String TREE_MAP_NAME           = hazelcastNamespace("q4-tree-map");
    private static final String HOODS_NAME_SET_NAME     = hazelcastNamespace("q4-hoods-name-set");
    private static final String HOODS_SPECIES_LIST_NAME = hazelcastNamespace("q4-hoods-species-list");

    private static final Comparator<Integer> GROUPS_ORDER = Comparator.reverseOrder();

    private static final String CSV_HEADER = csvHeaderJoiner()
        .add("GROUP")
        .add("NEIGHBOURHOOD A")
        .add("NEIGHBOURHOOD B")
        .toString()
        ;

    private static void writeAnswerToCsv(final Writer writer, final Q4Answer answer) throws IOException {
        writer.write(Integer.toString(answer.getGroup()));
        writer.write(OUT_DELIM);
        writer.write(answer.getHoodA());
        writer.write(OUT_DELIM);
        writer.write(answer.getHoodB());
        writer.write(NEW_LINE);
    }

    public static void execute(
            final HazelcastInstance hazelcast,
            final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
            final Writer queryOut, final Writer timeOut) throws IOException, ExecutionException, InterruptedException {
            
        final MultiMap<String, Tree> treeMap = hazelcast.getMultiMap(TREE_MAP_NAME);
        treeMap.clear();

        final Set<String> hoodsName = hazelcast.getSet(HOODS_NAME_SET_NAME);
        hoodsName.clear();

        final IList<Q3Answer> hoodSpecies = hazelcast.getList(HOODS_SPECIES_LIST_NAME);
        hoodSpecies.clear();

        logInputProcessingStart(timeOut);

        trees.forEach(tree -> treeMap.put(tree.getHoodName(), tree));
        hoods.map(Neighbourhood::getName).forEach(hoodsName::add);

        logInputProcessingEnd(timeOut);

        final Job<String, Tree> job1 = hazelcast
            .getJobTracker(JOB_TRACKER_1_NAME)
            .newJob(KeyValueSource.fromMultiMap(treeMap))
            ;
    
        logMapReduceJobStart(timeOut);

        job1
            .keyPredicate   (new CollectionContainsKeyPredicate<>(HOODS_NAME_SET_NAME, HazelcastCollectionExtractor.SET))
            .mapper         (new Q3Mapper())
            .combiner       (new ValueSetCombinerFactory<>())
            .reducer        (new DistinctValuesCountReducerFactory<>())
            .submit         (new MapCollator<>(Q3Answer::fromEntry, hoodSpecies::add))
            .get            ()
            ;

        final Job<String, Q3Answer> job2 = hazelcast
            .getJobTracker(JOB_TRACKER_2_NAME)
            .newJob(KeyValueSource.fromList(hoodSpecies))
            ;

        final List<Q4Answer> answers = new LinkedList<>();

        job2
            .mapper     (new Q4Mapper())
            .combiner   (new ValueSetCombinerFactory<>())
            .reducer    (new Q4ReducerFactory())
            .submit     (new SortPreSortedValuesCollator<>(GROUPS_ORDER, answers::add))
            .get        ()
            ;

        queryOut.write(CSV_HEADER);
        for(final Q4Answer answer : answers) {
            writeAnswerToCsv(queryOut, answer);
        }
        
        logMapReduceJobEnd(timeOut);

        // Limpiamos recursos usados
        treeMap     .clear();
        hoodsName   .clear();
        hoodSpecies .clear();
    }
}
