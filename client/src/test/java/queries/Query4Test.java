package queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ar.edu.itba.pod.client.queries.Query4;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;
import ar.edu.itba.pod.query4.Q4Answer;

public class Query4Test extends AbstractQueryTest {

    @Test
    public void test_query_4() throws ExecutionException, InterruptedException {

        final Stream<Neighbourhood> hoods = Stream.of(
            new Neighbourhood("1", 30),
            new Neighbourhood("2", 40),
            new Neighbourhood("3", 50),
            new Neighbourhood("4", 60),
            new Neighbourhood("5", 70),
            new Neighbourhood("6", 80),
            new Neighbourhood("7", 90)
        ).unordered();

        final Stream<Tree> trees = Stream.of(
            IntStream.range(0, 150) .mapToObj(i -> new Tree("1", "1-street", "t" + i)),
            IntStream.range(0, 130) .mapToObj(i -> new Tree("2", "2-street", "t" + i)),
            IntStream.range(0, 199) .mapToObj(i -> new Tree("3", "3-street", "t" + i)),
            IntStream.range(0, 300) .mapToObj(i -> new Tree("4", "4-street", "t" + i)),
            IntStream.range(0, 322) .mapToObj(i -> new Tree("5", "5-street", "t" + i)),
            IntStream.range(0, 5)   .mapToObj(i -> new Tree("6", "6-street", "t" + i)),
            IntStream.range(0, 10)  .mapToObj(i -> new Tree("7", "7-street", "t" + i)),
            IntStream.range(0, 101) .mapToObj(i -> new Tree("8", "8-street", "t" + i)),
            IntStream.range(0, 256) .mapToObj(i -> new Tree("9", "9-street", "t" + i))
        )
            .flatMap(i -> i)
            .unordered()
            ;

        final List<Q4Answer> answers = new LinkedList<>();

        Query4.execute(client, trees, hoods, answers::add);

        final List<Q4Answer> expectedAnswers = List.of(
            new Q4Answer(300, "4", "5"),
            new Q4Answer(100, "1", "2"),
            new Q4Answer(100, "1", "3"),
            new Q4Answer(100, "2", "3")

        );
        assertEquals(expectedAnswers, answers);
    }
}
