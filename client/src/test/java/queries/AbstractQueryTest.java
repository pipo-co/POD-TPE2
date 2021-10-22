package queries;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.HazelcastInstance;

public abstract class AbstractQueryTest {

    public static final String TEST_GROUP_NAME = "g16-test";
    public static final String TEST_GROUP_PASS = "g16-test-pass";

    private     TestHazelcastFactory    hazelcastFactory;
    private   HazelcastInstance[]       servers;
    protected   HazelcastInstance       client;

    @BeforeEach
    public void setUp() {
        hazelcastFactory = new TestHazelcastFactory();

        // Group Config
        final GroupConfig groupConfig = new GroupConfig()
            .setName    (TEST_GROUP_NAME)
            .setPassword(TEST_GROUP_PASS)
            ;

        // Create Servers
        servers = hazelcastFactory.newInstances(new Config()
            .setGroupConfig     (groupConfig)
            .addMultiMapConfig  (new MultiMapConfig("default")
                .setValueCollectionType(MultiMapConfig.ValueCollectionType.LIST)
            ),
            2
        );

        // Create Client
        client = hazelcastFactory.newHazelcastClient(new ClientConfig().setGroupConfig(groupConfig));
    }

    @AfterEach
    public void tearDown() {
        hazelcastFactory.shutdownAll();
    }
}
