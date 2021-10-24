package queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import ar.edu.itba.pod.client.queries.Query5;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query5.Q5Answer;
import org.junit.jupiter.api.Test;

public class Query5Test extends AbstractQueryTest {

    @Test
    public void test_query_5_empty() throws ExecutionException, InterruptedException {

        System.setProperty(Query5.PROPERTY_NEIGHBOURHOOD, "1");
        System.setProperty(Query5.PROPERTY_SPECIES, "t1");

        final Stream<Neighbourhood> hoods = Stream.empty();

        final Stream<Tree> lessThan10 = generateTrees(
                8,
                List.of("1", "2"),
                List.of("1-street", "2-street", "3-street"),
                List.of("t1", "t2")).unordered();

        final Stream<Tree> wrongHood = generateTrees(
                11,
                List.of("2", "3"),
                List.of("1-street", "2-street", "3-street"),
                List.of("t1", "t2")).unordered();

        final Stream<Tree> wrongSpecies = generateTrees(
                11,
                List.of("1", "2"),
                List.of("1-street", "2-street", "3-street"),
                List.of("t3", "t2")).unordered();

        final List<Q5Answer> answers = new LinkedList<>();

        final Stream<Tree> trees = Stream.concat(Stream.concat(lessThan10, wrongHood), wrongSpecies);

        Query5.execute(client, trees, hoods, answers::add);

        final List<Q5Answer> expectedAnswers = Collections.emptyList();

        assertEquals(expectedAnswers, answers);
    }

    @Test
    public void test_query_5_one_group() throws ExecutionException, InterruptedException {

        System.setProperty(Query5.PROPERTY_NEIGHBOURHOOD, "1");
        System.setProperty(Query5.PROPERTY_SPECIES, "t1");

        final Stream<Neighbourhood> hoods = Stream.empty();

        final Stream<Tree> trees = generateTrees(
                11,
                List.of("1", "2"),
                List.of("1-street", "2-street", "3-street"),
                List.of("t1", "t2")).unordered();

        final List<Q5Answer> answers = new LinkedList<>();

        Query5.execute(client, trees, hoods, answers::add);

        final List<Q5Answer> expectedAnswers = List.of(
                new Q5Answer(10, "1-street", "2-street"),
                new Q5Answer(10, "1-street", "3-street"),
                new Q5Answer(10, "2-street", "3-street")
        );
        assertEquals(expectedAnswers, answers);
    }

    @Test
    public void test_query_5_two_groups() throws ExecutionException, InterruptedException {

        System.setProperty(Query5.PROPERTY_NEIGHBOURHOOD, "1");
        System.setProperty(Query5.PROPERTY_SPECIES, "t1");

        final Stream<Neighbourhood> hoods = Stream.empty();

        final Stream<Tree> trees10 = generateTrees(
                11,
                List.of("1"),
                List.of("1-street", "2-street", "3-street"),
                List.of("t1", "t2")).unordered();

        final Stream<Tree> trees20 = generateTrees(
                20,
                List.of("1"),
                List.of("4-street", "5-street", "6-street"),
                List.of("t1", "t2")).unordered();

        final Stream<Tree> trees = Stream.concat(trees10, trees20);
        final List<Q5Answer> answers = new LinkedList<>();

        Query5.execute(client, trees, hoods, answers::add);

        final List<Q5Answer> expectedAnswers = List.of(
                new Q5Answer(20, "4-street", "5-street"),
                new Q5Answer(20, "4-street", "6-street"),
                new Q5Answer(20, "5-street", "6-street"),
                new Q5Answer(10, "1-street", "2-street"),
                new Q5Answer(10, "1-street", "3-street"),
                new Q5Answer(10, "2-street", "3-street")
        );
        assertEquals(expectedAnswers, answers);
    }

    private Stream<Tree> generateTrees(final int amount, final List<String> hoods, final List<String> streets, final List<String> treeNames) {
        List<Tree> trees = new LinkedList<>();
        for (final String h : hoods){
            for (final String s : streets){
                for (final String t : treeNames){
                    for (int i = 0; i < amount; i++) {
                        trees.add(new Tree(h, s, t));
                    }
                }
            }
        }
        return trees.stream();
    }


}
