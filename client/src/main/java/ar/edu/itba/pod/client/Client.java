package ar.edu.itba.pod.client;

import java.nio.file.Files;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

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

    public void query1(Path csvPath) {
        final Map<String, List<String>> map;
        try(final Stream<String> neighbourhoodLines = Files.lines(csvPath)) {
            neighbourhoodLines.map()
            final String[] fields = csvLine.split(';');
        }



    }
}

