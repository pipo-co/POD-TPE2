package queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ar.edu.itba.pod.client.queries.Query3;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query3.Q3Answer;

public class Query3Test extends AbstractQueryTest {

    @Test
    public void test_query_3() throws ExecutionException, InterruptedException {

        System.setProperty(Query3.PROPERTY_ANSWER_COUNT, "4");

        final Stream<Neighbourhood> hoods = Stream.of(
            new Neighbourhood("1", 30),
            new Neighbourhood("2", 40),
            new Neighbourhood("3", 50),
            new Neighbourhood("4", 60),
            new Neighbourhood("5", 70),
            new Neighbourhood("6", 70)
        ).unordered();

        final Stream<Tree> trees = Stream.of(
            new Tree("1", "1-street", "t1"),
            new Tree("1", "2-street", "t2"),
            new Tree("1", "3-street", "t3"),
            new Tree("1", "4-street", "t1"),
            new Tree("2", "5-street", "t2"),
            new Tree("2", "6-street", "t3"),
            new Tree("2", "7-street", "t1"),
            new Tree("2", "8-street", "t2"),
            new Tree("2", "9-street", "t5"),
            new Tree("2", "10-street", "t1"),
            new Tree("2", "11-street", "t2"),
            new Tree("3", "12-street", "t3"),
            new Tree("4", "12-street", "t6"),
            new Tree("not_found", "13-street", "t1")
        ).unordered();

        final List<Q3Answer> answers = new LinkedList<>();

        Query3.execute(client, trees, hoods, answers::add);

        final List<Q3Answer> expectedAnswers = List.of(
            new Q3Answer("2", 4),
            new Q3Answer("1", 3),
            new Q3Answer("3", 1),
            new Q3Answer("4", 1)
        );
        assertEquals(expectedAnswers, answers);
    }
}
