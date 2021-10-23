package queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ar.edu.itba.pod.client.queries.Query1;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query1.Q1Answer;

public class Query1Test extends AbstractQueryTest {

    @Test
    public void test_query_1() throws ExecutionException, InterruptedException {

        final Stream<Neighbourhood> hoods = Stream.of(
            new Neighbourhood("1", 30),
            new Neighbourhood("2", 30),
            new Neighbourhood("3", 30),
            new Neighbourhood("4", 30)
        ).unordered();

        final Stream<Tree> trees = Stream.of(
            new Tree("2", "1-street", "t1"),
            new Tree("1", "2-street", "t2"),
            new Tree("3", "3-street", "t3"),
            new Tree("3", "4-street", "t4"),
            new Tree("1", "5-street", "t5"),
            new Tree("not_found", "5-street", "t6")
        ).unordered();

        final List<Q1Answer> answers = new LinkedList<>();

        Query1.execute(client, trees, hoods, answers::add);

        final List<Q1Answer> expectedAnswers = List.of(
            new Q1Answer("1", 2),
            new Q1Answer("3", 2),
            new Q1Answer("2", 1)
        );
        assertEquals(expectedAnswers, answers);
    }
}
