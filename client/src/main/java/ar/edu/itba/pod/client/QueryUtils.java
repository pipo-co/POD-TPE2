package ar.edu.itba.pod.client;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.hazelcast.core.HazelcastInstance;

import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

public final class QueryUtils {
    private QueryUtils() {
        // static
    }

    public static final String COLLECTIONS_PREFIX = "g16-";
    public static String hazelcastNamespace(final String name) {
        return COLLECTIONS_PREFIX + name;
    }

    public static final String IN_DELIM     = ";";
    public static final String OUT_DELIM    = ";";
    public static final String NEW_LINE     = System.lineSeparator();

    public static StringJoiner csvHeaderJoiner() {
        return new StringJoiner(OUT_DELIM, "", NEW_LINE);
    }

    /** Constantes para centralizar el locale y que no sea system dependant */
    public static final Locale DEFAULT_LOCALE   = Locale.ROOT;
    public static final ZoneId DEFAULT_ZONE_ID  = ZoneId.of("UTC");

    public static <Answer> QueryMetrics queryAnswersToCSV(
        final Query<Answer> query, final HazelcastInstance hazelcast,
        final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
        final ThrowingConsumer<Answer> answerToCsv) throws IOException, ExecutionException, InterruptedException {

        final QueryMetrics metrics;
        try {
            metrics = query.execute(hazelcast, trees, hoods, answerToCsv);
        } catch(final RuntimeException e) {
            throw restoreWrappedIOException(e);
        }

        return metrics;
    }

    public static RuntimeException restoreWrappedIOException(final RuntimeException e) throws IOException {
        final Throwable cause = e.getCause();
        if(cause instanceof IOException) {
            throw (IOException) cause;
        }
        return e;
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T> extends Consumer<T> {
        void acceptWithEx(final T t) throws Exception;

        @Override
        default void accept(final T t) {
            try {
                acceptWithEx(t);
            } catch(final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
