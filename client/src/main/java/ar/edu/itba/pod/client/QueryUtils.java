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

    /* ------------------------------ System Constants ------------------------------------ */

    /** Constantes para centralizar el locale y que no sea system dependant */
    public static final Locale DEFAULT_LOCALE   = Locale.ROOT;
    public static final ZoneId DEFAULT_ZONE_ID  = ZoneId.of("UTC");

    public static final String NEW_LINE         = System.lineSeparator();

    /* ----------------------------- Parameter Validation --------------------------------- */

    private static final String MISSING_PARAM_MSG_TEMPLATE = "'%s' parameter is required";
    public static String getRequiredProperty(final String propertyName) {
        final String ret = System.getProperty(propertyName);
        if(ret == null || ret.isEmpty()) {
            throw new IllegalArgumentException(String.format(DEFAULT_LOCALE, MISSING_PARAM_MSG_TEMPLATE, propertyName));
        }
        return ret;
    }

    private static final String INVALID_POSITIVE_INT_MSG_TEMPLATE = "'%s' must be a positive integer";
    private static int parsePositiveIntProperty(final String intValue, final String propertyName) {
        final int ret;
        try {
            ret = Integer.parseInt(intValue);
        } catch(final NumberFormatException e) {
            throw new IllegalArgumentException(String.format(DEFAULT_LOCALE, INVALID_POSITIVE_INT_MSG_TEMPLATE, propertyName));
        }

        if(ret <= 0) {
            throw new IllegalArgumentException(String.format(DEFAULT_LOCALE, INVALID_POSITIVE_INT_MSG_TEMPLATE, propertyName));
        }

        return ret;
    }
    public static int getPositiveIntProperty(final String propertyName) {
        return parsePositiveIntProperty(getRequiredProperty(propertyName), propertyName);
    }
    public static int getPositiveIntProperty(final String propertyName, final int defaultValue) {
        final String intValue = System.getProperty(propertyName);
        if(intValue == null) {
            return defaultValue;
        }
        return parsePositiveIntProperty(intValue, propertyName);
    }

    /* ---------------------------------- Hazelcast ------------------------------------- */

    public static final String COLLECTIONS_PREFIX = "g16-";
    public static String hazelcastNamespace(final String name) {
        return COLLECTIONS_PREFIX + name;
    }

    /* -------------------------------- IO/CSV Handling ---------------------------------- */

    public static final String IN_DELIM     = ";";
    public static final String OUT_DELIM    = ";";

    public static StringJoiner csvHeaderJoiner() {
        return new StringJoiner(OUT_DELIM, "", NEW_LINE);
    }

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
