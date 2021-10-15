package ar.edu.itba.pod.client;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.HazelcastInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private Client() {
        // static
    }

    public static final String PROPERTY_QUERY       = "query";
    public static final String PROPERTY_CITY        = "city";
    public static final String PROPERTY_IN_PATH     = "inPath";
    public static final String PROPERTY_OUT_PATH    = "outPath";
    public static final String PROPERTY_GROUP_NAME  = "name";
    public static final String PROPERTY_GROUP_PASS  = "pass";
    public static final String PROPERTY_ADDRESSES   = "addresses";

    public static final String DEFAULT_IN_PATH    = ".";
    public static final String DEFAULT_OUT_PATH   = ".";
    public static final String DEFAULT_GROUP_NAME = "g16";
    public static final String DEFAULT_GROUP_PASS = "g16-pass";
    public static final String DEFAULT_ADDRESS    = "127.0.0.1:" + NetworkConfig.DEFAULT_PORT;

    private static Query getQuery(final int queryCount) {
        if(queryCount < 1 || queryCount > QueryEnum.SIZE) {
            throw new IllegalArgumentException("Invalid query count " + queryCount + ". Values go from 1 to " + QueryEnum.SIZE);
        }
        return QueryEnum.VALUES.get(queryCount - 1);
    }

    private static final String PROPERTY_LIST_DELIM = ";";
    private static List<String> parseAddresses(final String addressList) {
        if(addressList == null) {
            return List.of(DEFAULT_ADDRESS);
        }
        return Arrays.asList(addressList.split(PROPERTY_LIST_DELIM));
    }

    public static final String COLLECTIONS_PREFIX = "g16-";
    public static String collectionName(final String name) {
        return COLLECTIONS_PREFIX + name;
    }

    private static Path requireRegularFile(final Path path) {
        if(!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Path " + path + " must be a regular file");
        }
        return path;
    }

    public static final String TREE_IN_FILE_PREFIX = "arboles";
    public static final String TREE_IN_FILE_SUFFIX = ".csv";
    public static Path treeFilePath(final String inPath, final String cityCode) {
        return requireRegularFile(Path.of(inPath + TREE_IN_FILE_PREFIX + cityCode + TREE_IN_FILE_SUFFIX));
    }

    public static final String HOOD_IN_FILE_PREFIX = "barrios";
    public static final String HOOD_IN_FILE_SUFFIX = ".csv";
    public static Path hoodFilePath(final String inPath, final String cityCode) {
        return requireRegularFile(Path.of(inPath + HOOD_IN_FILE_PREFIX + cityCode + HOOD_IN_FILE_SUFFIX));
    }

    public static final String QUERY_OUT_FILE_PREFIX = "query";
    public static final String QUERY_OUT_FILE_SUFFIX = ".csv";
    public static Path queryOutPath(final String outPath, final int queryCount) {
        return requireRegularFile(Path.of(outPath + QUERY_OUT_FILE_PREFIX + queryCount + QUERY_OUT_FILE_SUFFIX));
    }

    public static final String TIME_OUT_FILE_PREFIX = "time";
    public static final String TIME_OUT_FILE_SUFFIX = ".txt";
    public static Path timeOutPath(final String inPath, final int queryCount) {
        return requireRegularFile(Path.of(inPath + TIME_OUT_FILE_PREFIX + queryCount + TIME_OUT_FILE_SUFFIX));
    }

    public static void main(final String[] args) throws IOException {
        logger.info("Client Starting ...");

        // Required
        final int           queryCount  = Integer.parseInt(System.getProperty(PROPERTY_QUERY));
        final String        city        = requireNonNull(System.getProperty(PROPERTY_CITY));

        // Optional
        final String        groupName   = System.getProperty(PROPERTY_GROUP_NAME, DEFAULT_GROUP_NAME);
        final String        groupPass   = System.getProperty(PROPERTY_GROUP_PASS, DEFAULT_GROUP_PASS);
        final List<String>  addresses   = parseAddresses(System.getProperty(PROPERTY_ADDRESSES));
        final String        inPath      = System.getProperty(PROPERTY_IN_PATH, DEFAULT_IN_PATH);
        final String        outPath     = System.getProperty(PROPERTY_OUT_PATH, DEFAULT_OUT_PATH);

        final Path treeCsv  = treeFilePath  (inPath,  city);
        final Path hoodCsv  = hoodFilePath  (inPath,  city);
        final Path queryOut = queryOutPath  (outPath, queryCount);
        final Path timeOut  = timeOutPath   (outPath, queryCount);

        final HazelcastInstance hazelcast =  HazelcastClient.newHazelcastClient(new ClientConfig()
            .setGroupConfig(new GroupConfig()
                .setName    (groupName)
                .setPassword(groupPass)
            )
            .setNetworkConfig(new ClientNetworkConfig()
                .setAddresses(addresses)
            )
        );

        logger.info("Executing query " + queryCount);
        getQuery(queryCount).execute(hazelcast, treeCsv, hoodCsv, queryOut, timeOut);

        HazelcastClient.shutdownAll();

        logger.info("Client Finished");
    }

    public enum QueryEnum implements Query {
        Q1(Query1::execute),
        ;

        public static final List<QueryEnum> VALUES  = Arrays.asList(values());
        public static final int             SIZE    = VALUES.size();

        private final Query query;

        QueryEnum(final Query query) {
            this.query = query;
        }

        public void execute(
            final HazelcastInstance hazelcast,
            final Path treeCsv, final Path hoodCsv,
            final Path queryOut, final Path timeOut) throws IOException {
            query.execute(hazelcast, treeCsv, hoodCsv, queryOut, timeOut);
        }
    }
}
