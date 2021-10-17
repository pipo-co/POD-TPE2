package ar.edu.itba.pod.client;

import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.StringJoiner;

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

    /** Constantes para centralizar locale de la salida, y que no sea system dependant */
    public static final Locale OUTPUT_LOCALE    = Locale.ROOT;
    public static final ZoneId OUTPUT_ZONE_ID   = ZoneId.of("UTC");

    public static StringJoiner csvHeaderJoiner() {
        return new StringJoiner(OUT_DELIM, "", NEW_LINE);
    }

    public static final String              TIMESTAMP_PATTERN     = "dd/MM/yyyy HH:mm:ss:SSSS";
    public static final DateTimeFormatter   TIMESTAMP_FORMATTER   = DateTimeFormatter
        .ofPattern  (TIMESTAMP_PATTERN)
        .withLocale (OUTPUT_LOCALE)
        .withZone   (OUTPUT_ZONE_ID)
        ;

    public static String getCurrentTimestamp() {
        return TIMESTAMP_FORMATTER.format(Instant.now());
    }
    public static void writeTimestampMessage(final Writer writer, final String message) throws IOException {
        writer.write(getCurrentTimestamp() + " - " + message);
        writer.write(NEW_LINE);
    }

    public static final String INPUT_PROCESSING_START   = "Inicio de la lectura del archivo";
    public static final String INPUT_PROCESSING_END     = "fin de lectura del archivo";
    public static final String MAP_REDUCE_JOB_START     = "Inicio del trabajo map/reduce";
    public static final String MAP_REDUCE_JOB_END       = "Fin del trabajo map/reduce";

    public static void logInputProcessingStart(final Writer writer) throws IOException {
        writeTimestampMessage(writer, INPUT_PROCESSING_START);
    }
    public static void logInputProcessingEnd(final Writer writer) throws IOException {
        writeTimestampMessage(writer, INPUT_PROCESSING_END);
    }
    public static void logMapReduceJobStart(final Writer writer) throws IOException {
        writeTimestampMessage(writer, MAP_REDUCE_JOB_START);
    }
    public static void logMapReduceJobEnd(final Writer writer) throws IOException {
        writeTimestampMessage(writer, MAP_REDUCE_JOB_END);
    }
}
