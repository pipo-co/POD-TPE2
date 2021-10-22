package ar.edu.itba.pod.client;

import static ar.edu.itba.pod.client.QueryUtils.*;
import static java.util.Objects.requireNonNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.HazelcastInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.pod.client.queries.Query1;
import ar.edu.itba.pod.client.queries.Query2;
import ar.edu.itba.pod.client.queries.Query3;
import ar.edu.itba.pod.client.queries.Query4;
import ar.edu.itba.pod.models.Neighbourhood;
import ar.edu.itba.pod.models.Tree;

public final class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private Client() {
        // static
    }

    public static final String PROPERTY_QUERY       = "query";
    public static final String PROPERTY_CITY        = "city";
    public static final String PROPERTY_IN_PATH     = "inPath";
    public static final String PROPERTY_OUT_PATH    = "outPath";
    public static final String PROPERTY_CHARSET     = "charset";
    public static final String PROPERTY_GROUP_NAME  = "name";
    public static final String PROPERTY_GROUP_PASS  = "pass";
    public static final String PROPERTY_ADDRESSES   = "addresses";  

    public static final String DEFAULT_IN_PATH    = ".";
    public static final String DEFAULT_OUT_PATH   = ".";
    public static final String DEFAULT_CHARSET    = "ISO-8859-1";
    public static final String DEFAULT_GROUP_NAME = "g16";
    public static final String DEFAULT_GROUP_PASS = "g16-pass";
    public static final String DEFAULT_ADDRESS    = "127.0.0.1:" + NetworkConfig.DEFAULT_PORT;

    private static QueryToCSV getQuery(final int queryCount) {
        if(queryCount < 1 || queryCount > QueryEnum.SIZE) {
            throw new IllegalArgumentException("Invalid query count " + queryCount + ". Values go from 1 to " + QueryEnum.SIZE);
        }
        return QueryEnum.VALUES.get(queryCount - 1);
    }

    private static Charset parseCharset(final String charsetName) {
        return Charset.forName(charsetName == null ? DEFAULT_CHARSET : charsetName);
    }

    private static final String PROPERTY_LIST_DELIM = ";";
    private static List<String> parseAddresses(final String addressList) {
        if(addressList == null) {
            return List.of(DEFAULT_ADDRESS);
        }
        return Arrays.asList(addressList.split(PROPERTY_LIST_DELIM));
    }

    private static Path requireReadable(final Path path) {
        if(!Files.isReadable(path)) {
            throw new IllegalArgumentException("Path " + path + " must be a regular file");
        }
        return path;
    }
    private static Path createWritableFile(final Path path) throws IOException {
        Files.newByteChannel(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING).close();
        return path;
    }

    public static final String TREE_IN_FILE_PREFIX = "arboles";
    public static final String TREE_IN_FILE_SUFFIX = ".csv";
    public static Path treeFilePath(final String inPath, final String cityCode) {
        return requireReadable(Path.of(inPath, TREE_IN_FILE_PREFIX + cityCode + TREE_IN_FILE_SUFFIX));
    }

    public static final String HOOD_IN_FILE_PREFIX = "barrios";
    public static final String HOOD_IN_FILE_SUFFIX = ".csv";
    public static Path hoodFilePath(final String inPath, final String cityCode) {
        return requireReadable(Path.of(inPath, HOOD_IN_FILE_PREFIX + cityCode + HOOD_IN_FILE_SUFFIX));
    }

    public static final String QUERY_OUT_FILE_PREFIX = "query";
    public static final String QUERY_OUT_FILE_SUFFIX = ".csv";
    public static Path queryOutPath(final String outPath, final int queryCount) throws IOException {
        return createWritableFile(Path.of(outPath, QUERY_OUT_FILE_PREFIX + queryCount + QUERY_OUT_FILE_SUFFIX));
    }

    public static final String TIME_OUT_FILE_PREFIX = "time";
    public static final String TIME_OUT_FILE_SUFFIX = ".txt";
    public static Path timeOutPath(final String outPath, final int queryCount) throws IOException {
        return createWritableFile(Path.of(outPath, TIME_OUT_FILE_PREFIX + queryCount + TIME_OUT_FILE_SUFFIX));
    }

    public static final String            TIMESTAMP_PATTERN     = "dd/MM/yyyy HH:mm:ss:SSSS";
    public static final DateTimeFormatter TIMESTAMP_FORMATTER   = DateTimeFormatter
        .ofPattern  (TIMESTAMP_PATTERN)
        .withLocale (DEFAULT_LOCALE)
        .withZone   (DEFAULT_ZONE_ID)
        ;
    private static void writeTimestampMessage(final Writer writer, final Instant timestamp, final String message) throws IOException {
        writer.write(TIMESTAMP_FORMATTER.format(timestamp) + " - " + message);
        writer.write(NEW_LINE);
    }

    public static void writeQueryMetrics(final Writer writer, final QueryMetrics metrics) throws IOException {
        writeTimestampMessage(writer, metrics.inputProcessingStart, "Inicio de la lectura del archivo");
        writeTimestampMessage(writer, metrics.inputProcessingEnd,   "Fin de lectura del archivo");
        writeTimestampMessage(writer, metrics.mapReduceJobStart,    "Inicio del trabajo map/reduce");
        writeTimestampMessage(writer, metrics.mapReduceJobEnd,      "Fin del trabajo map/reduce");
    }

    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException {
        logger.info("Client Starting ...");

        // Required
        final int           queryCount  = Integer.parseInt(System.getProperty(PROPERTY_QUERY));
        final String        city        = requireNonNull(System.getProperty(PROPERTY_CITY));

        // Optional
        final Charset       charset     = parseCharset(System.getProperty(PROPERTY_CHARSET, DEFAULT_CHARSET));
        final String        groupName   = System.getProperty(PROPERTY_GROUP_NAME, DEFAULT_GROUP_NAME);
        final String        groupPass   = System.getProperty(PROPERTY_GROUP_PASS, DEFAULT_GROUP_PASS);
        final List<String>  addresses   = parseAddresses(System.getProperty(PROPERTY_ADDRESSES));
        final String        inPath      = System.getProperty(PROPERTY_IN_PATH, DEFAULT_IN_PATH);
        final String        outPath     = System.getProperty(PROPERTY_OUT_PATH, DEFAULT_OUT_PATH);   

        final Path treeCsv  = treeFilePath  (inPath,  city);
        final Path hoodCsv  = hoodFilePath  (inPath,  city);
        final Path queryOut = queryOutPath  (outPath, queryCount);
        final Path timeOut  = timeOutPath   (outPath, queryCount);

        final CityCSVDatasource datasource = CityCSVDatasource.valueOf(city);

        final HazelcastInstance hazelcast =  HazelcastClient.newHazelcastClient(new ClientConfig()
            .setGroupConfig(new GroupConfig()
                .setName    (groupName)
                .setPassword(groupPass)
            )
            .setNetworkConfig(new ClientNetworkConfig()
                .setAddresses(addresses)
            )
        );

        logger.info("Executing Query " + queryCount);

        try(final Stream<String> treeLines  = Files.lines(treeCsv, charset);
            final Stream<String> hoodLines  = Files.lines(hoodCsv, charset);
            final var queryOutWriter        = new BufferedWriter(new FileWriter(queryOut.toFile(), charset));
            final var metricsWriter         = new BufferedWriter(new FileWriter(timeOut.toFile(), charset))) {

            final QueryMetrics metrics = getQuery(queryCount).executeToCSV(
                hazelcast,
                treeLines
                    .skip(1)
                    .map(line -> line.split(IN_DELIM))
                    .map(datasource::treeFromCSV),
                hoodLines
                    .skip(1)
                    .map(line -> line.split(IN_DELIM))
                    .map(datasource::hoodFromCSV),
                queryOutWriter
            );

            writeQueryMetrics(metricsWriter, metrics);
        }

        HazelcastClient.shutdownAll();

        logger.info("Query " + queryCount + " Finished");
    }

    public enum QueryEnum implements QueryToCSV {
        Q1(Query1::executeToCSV),
        Q2(Query2::executeToCSV),
        Q3(Query3::executeToCSV),
        Q4(Query4::executeToCSV),
        ;

        public static final List<QueryEnum> VALUES  = Arrays.asList(values());
        public static final int             SIZE    = VALUES.size();

        private final QueryToCSV query;

        QueryEnum(final QueryToCSV query) {
            this.query = query;
        }

        public QueryMetrics executeToCSV(
            final HazelcastInstance hazelcast,
            final Stream<Tree> trees, final Stream<Neighbourhood> hoods,
            final Writer queryOut) throws IOException, ExecutionException, InterruptedException {
            return query.executeToCSV(hazelcast, trees, hoods, queryOut);
        }
    }
}
