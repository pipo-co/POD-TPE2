package ar.edu.itba.pod.server;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static final String PROPERTY_GROUP_NAME          = "name";
    public static final String PROPERTY_GROUP_PASS          = "pass";
    public static final String PROPERTY_INTERFACES          = "interfaces";
    public static final String PROPERTY_MEMBERS             = "members";
    public static final String PROPERTY_PORT                = "port";
    public static final String PROPERTY_MGMT_CENTER_URL     = "mgmtCenterUrl";
    public static final String PROPERTY_ENABLE_MGMT_CENTER  = "enable";

    public static final String DEFAULT_GROUP_NAME           = "g16";
    public static final String DEFAULT_GROUP_PASS           = "g16-pass";
    public static final String DEFAULT_INTERFACE            = "127.0.0.1";
    public static final String DEFAULT_PORT                 = Integer.toString(NetworkConfig.DEFAULT_PORT);
    public static final String DEFAULT_MGMT_CENTER_URL      = "http://localhost:32768/mancenter/";
    public static final String DEFAULT_ENABLE_MGMT_CENTER   = Boolean.FALSE.toString();

    private static final String PROPERTY_LIST_DELIM = ";";
    private static List<String> parseInterfaces(final String interfaceList) {
        if(interfaceList == null) {
            return List.of(DEFAULT_INTERFACE);
        }
        return Arrays.asList(interfaceList.split(PROPERTY_LIST_DELIM));
    }
    private static List<String> parseMembers(final String memberList) {
        if(memberList == null) {
            return List.of();
        }
        return Arrays.asList(memberList.split(PROPERTY_LIST_DELIM));
    }

    public static void main(final String[] args) {
        logger.info("Hazelcast Server Starting ...");

        final String        groupName           = System.getProperty(PROPERTY_GROUP_NAME, DEFAULT_GROUP_NAME);
        final String        groupPass           = System.getProperty(PROPERTY_GROUP_PASS, DEFAULT_GROUP_PASS);
        final List<String>  interfaces          = parseInterfaces(System.getProperty(PROPERTY_INTERFACES));
        final List<String>  members             = parseMembers(System.getProperty(PROPERTY_MEMBERS));
        final int           port                = Integer.parseInt(System.getProperty(PROPERTY_PORT, DEFAULT_PORT));
        final String        mgmtCenterUrl       = System.getProperty(PROPERTY_MGMT_CENTER_URL, DEFAULT_MGMT_CENTER_URL);
        final boolean       enableMgmtCenter    = Boolean.parseBoolean(System.getProperty(PROPERTY_ENABLE_MGMT_CENTER, DEFAULT_ENABLE_MGMT_CENTER));

        Hazelcast.newHazelcastInstance(new Config()
            .setGroupConfig(new GroupConfig()
                .setName    (groupName)
                .setPassword(groupPass)
            )
            .setNetworkConfig(new NetworkConfig()
                .setInterfaces(new InterfacesConfig()
                    .setInterfaces  (interfaces)
                    .setEnabled     (true)
                )
                .setJoin(new JoinConfig()
                    .setMulticastConfig(new MulticastConfig()
                        .setEnabled(false)
                    )
                    .setTcpIpConfig(new TcpIpConfig()
                        .setEnabled(true)
                        .setMembers(members)
                    )
                )
                .setPort(port)
                .setPortAutoIncrement(false)
            )
            .setManagementCenterConfig(new ManagementCenterConfig()
                .setUrl     (mgmtCenterUrl)
                .setEnabled (enableMgmtCenter)
            )
        );
    }
}
