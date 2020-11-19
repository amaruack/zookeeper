package com.eseict.zoo.proc.node;

import com.eseict.zoo.exception.ZookeeperException;
import com.eseict.zoo.util.ZookeeperCommUtil;

import java.util.Properties;

public class NodeConfig extends Properties {

    public static class PARAM_KEY {

        public static String SERVER_ID = "id";
        public static String SERVER_MAC = "mac";
        public static String SERVER_KEY = "key";
        public static String SERVER_SECRET = "secret";
        public static String SERVER_HOST = "host";
        public static String SERVER_PORT = "port";

        public static String GROUP_PATH = "groupPath";
        public static String SUB_GROUP_PATH = "subGroupPath";
        public static String SYSTEM_PATH = "systemPath";

    }

//    String GROUP_ZNODE_PATH = "/iot";
//    String SUB_GROUP_ZNODE_PATH = GROUP_ZNODE_PATH + "/append";
//    String SYSTEM_ZNODE_PATH = SUB_GROUP_ZNODE_PATH + "/iotweb";
//    String RUNNING_ZNODE_PATH = SYSTEM_ZNODE_PATH + "/runnings";
//    String SERVER_INFO_ZNODE_PRE_PATH =  RUNNING_ZNODE_PATH + "/server-";
//    String SERVER_INFO_ZNODE_PATH = "";
//
//
//    String GROUP_ZNODE_PATH = "/iot";
//    String SUB_GROUP_ZNODE_PATH = GROUP_ZNODE_PATH + "/append";
//    String SYSTEM_ZNODE_PATH = SUB_GROUP_ZNODE_PATH + "/iotweb";
//    String MASTER_ZNODE_PATH = SYSTEM_ZNODE_PATH + "/master";


    public NodeConfig(Properties prop) throws ZookeeperException {
        super(prop);
        init();
    }
    public NodeConfig() throws ZookeeperException {
        super();
        init();
    }
    public NodeConfig(String id) throws ZookeeperException {
        super();
        super.setProperty(PARAM_KEY.SERVER_ID, id);
        init();
    }
    public NodeConfig(String group, String subGroup, String system, String id) throws ZookeeperException {
        super();
        super.setProperty(PARAM_KEY.SERVER_ID, id);
        super.setProperty(PARAM_KEY.GROUP_PATH, group);
        super.setProperty(PARAM_KEY.SUB_GROUP_PATH, subGroup);
        super.setProperty(PARAM_KEY.SYSTEM_PATH, system);
        init();
    }

    public void init() throws ZookeeperException {

//        String id = super.get(SERVER_ID) == null ? "" : (String) super.get(SERVER_ID);
//        if (Strings.isNullOrEmpty(id)) {
//            throw new ZookeeperException();
//        }

        String mac =  ZookeeperCommUtil.getLocalMacAddress();
        super.setProperty(PARAM_KEY.SERVER_MAC, mac);

    }



//    @Override
//    public synchronized Object get(Object key) {
//        super.setProperty()
//
//        return super.get(key);
////        (return super.get(key);)
//    }
}
