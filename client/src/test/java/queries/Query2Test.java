package queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ar.edu.itba.pod.client.queries.Query2;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query2.Q2Answer;

public class Query2Test extends AbstractQueryTest {

    @Test
    public void test_query_2() throws ExecutionException, InterruptedException {
        final Neighbourhood hood1 = new Neighbourhood("1", 30);
        final Neighbourhood hood11 = new Neighbourhood("11", 30);
        final Neighbourhood hood2 = new Neighbourhood("2", 40);
        final Neighbourhood hood3 = new Neighbourhood("3", 50);

        final Stream<Neighbourhood> hoods = Stream
            .of(hood1, hood11, hood2, hood3, new Neighbourhood("4", 60))
            .unordered()
            ;

        final Stream<Tree> trees = Stream.of(
            new Tree("1", "1-street", "t1"),
            new Tree("1", "2-street", "t2"),
            new Tree("1", "3-street", "t3"),
            new Tree("1", "4-street", "t1"),
            new Tree("11", "4-street", "t11"),
            new Tree("11", "1-street", "t11"),
            new Tree("11", "3-street", "t11"),
            new Tree("11", "4-street", "t2"),
            new Tree("11", "5-street", "t2"),
            new Tree("11", "8-street", "t2"),
            new Tree("2", "5-street", "t2"),
            new Tree("2", "6-street", "t3"),
            new Tree("2", "7-street", "t1"),
            new Tree("2", "8-street", "t2"),
            new Tree("2", "9-street", "t3"),
            new Tree("2", "10-street", "t1"),
            new Tree("2", "11-street", "t2"),
            new Tree("3", "12-street", "t3"),
            new Tree("not_found", "13-street", "t1")
        ).unordered();

        final List<Q2Answer> answers = new LinkedList<>();

        Query2.execute(client, trees, hoods, answers::add);

        final List<Q2Answer> expectedAnswers = List.of(
            new Q2Answer(hood1, "t1", 2),
            new Q2Answer(hood11, "t11", 3),
            new Q2Answer(hood2, "t2", 3),
            new Q2Answer(hood3, "t3", 1)
        );
        assertEquals(expectedAnswers, answers);
    }
}
