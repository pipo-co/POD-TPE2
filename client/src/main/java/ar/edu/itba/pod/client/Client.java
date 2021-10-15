package ar.edu.itba.pod.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.pod.models.DataSources;
import ar.edu.itba.pod.models.Tree;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private static final String GROUP_NAME = "g16";
    private static final String GROUP_PASS = "g16-pass";


    public static void main(String[] args) {
        logger.info("hz-config Client Starting ...");
        // Client Config
        ClientConfig clientConfig = new ClientConfig();
        // Group Config
        GroupConfig groupConfig = new
            GroupConfig().setName(GROUP_NAME).setPassword(GROUP_PASS);
        clientConfig.setGroupConfig(groupConfig);

        // Client Network Config
        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();

        String[] addresses = {"localhost:5701"};
        clientNetworkConfig.addAddress(addresses);
        clientConfig.setNetworkConfig(clientNetworkConfig);
        HazelcastInstance hazelcastInstance =
            HazelcastClient.newHazelcastClient(clientConfig);

        String mapName = "testMap";
        IMap<Integer, String> testMapFromMember = hazelcastInstance.getMap(mapName);
        testMapFromMember.set(1, "test1");
        IMap<Integer, String> testMap = hazelcastInstance.getMap(mapName);
        System.out.println(testMap.get(1));
        // Shutdown
        HazelcastClient.shutdownAll();
    }

    public void query1(Path csvPath, String city) throws IOException {
        final Map<String, List<String>> map;
        
        try(final Stream<String> neighbourhoodLines = Files.lines(csvPath)) {
            neighbourhoodLines
                .map(line -> line.split(";"))
                .map(values -> DataSources.valueOf(city).treeFromCSV(values))
                .collect(Collectors.toMap(Tree::getNeighbourhoodName, Function.identity()));
        }
    }
}

