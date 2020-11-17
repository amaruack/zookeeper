package com.eseict.zoo.proc;

import com.eseict.zoo.util.CommUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class MasterSlaveNodeProcess {

    private Logger logger = LoggerFactory.getLogger(MasterSlaveNodeProcess.class);
    private ZooKeeper zk;
    private MasterSlaveNodeWatcher watcher;
    private boolean masterYn = false;
    private Gson gson = new GsonBuilder().create();

    public NodeConfig config;

    public String ZNODE_PATH_SEPERATER = "/";

    public String GROUP_ZNODE_PATH = "";
    public String SUB_GROUP_ZNODE_PATH = "";
    public String SYSTEM_ZNODE_PATH = "";
    public String MASTER_ZNODE_PATH = "";

    public MasterSlaveNodeProcess(){}
    public MasterSlaveNodeProcess(ZooKeeper zk){this.zk = zk;}

    public ZooKeeper getZookeeperConnection() {
        return zk;
    }

    public void setZookeeperConnection(ZooKeeper zk) {
        this.zk = zk;
    }

    public MasterSlaveNodeWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(MasterSlaveNodeWatcher watcher) {
        this.watcher = watcher;
        watcher.setProcess(this);
//        watcher.setConfig(this.config);
    }

    public void init(NodeConfig config) throws ZookeeperException {
        this.config = config;

        String id = this.config.get(NodeConfig.PARAM_KEY.SERVER_ID) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.SERVER_ID);
        String mac = this.config.get(NodeConfig.PARAM_KEY.SERVER_MAC) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.SERVER_MAC);

        if (Strings.isNullOrEmpty(id)) {
            throw new ZookeeperException("id not set");
        }

        try {
            // path generate
            pathGen();
            // parent node check
            parentNodeCheck();

            if (zk.exists(MASTER_ZNODE_PATH, false) == null) {
                String result = zk.create(MASTER_ZNODE_PATH,
                        gson.toJson(CommUtil.getServerInfo(id, mac)).getBytes(StandardCharsets.UTF_8),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL);
                logger.info("create master node [{}]", result);
                masterYn = true;
            } else {
                logger.info("Exist master node");
            }
            // master 나 slave 중 하나라도 생성시에 watcher 생성
            zk.exists(MASTER_ZNODE_PATH, watcher);

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        logger.info("MasterSlaveNodeProcess success.");
    }

    public void pathGen(){

        String group = this.config.get(NodeConfig.PARAM_KEY.GROUP_PATH) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.GROUP_PATH);
        String subGroup = this.config.get(NodeConfig.PARAM_KEY.SUB_GROUP_PATH) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.SUB_GROUP_PATH);
        String system = this.config.get(NodeConfig.PARAM_KEY.SYSTEM_PATH) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.SYSTEM_PATH);

        if (!Strings.isNullOrEmpty(group)){
            GROUP_ZNODE_PATH = new StringBuilder()
                    .append(ZNODE_PATH_SEPERATER)
                    .append(group)
                    .toString();
        } else {
            GROUP_ZNODE_PATH = "/group";
        }

        if (!Strings.isNullOrEmpty(subGroup)){
            SUB_GROUP_ZNODE_PATH = new StringBuilder()
                    .append(GROUP_ZNODE_PATH)
                    .append(ZNODE_PATH_SEPERATER)
                    .append(subGroup)
                    .toString();
        } else {
            SUB_GROUP_ZNODE_PATH = GROUP_ZNODE_PATH + "/subGroup";
        }

        if (!Strings.isNullOrEmpty(system)){
            SYSTEM_ZNODE_PATH = new StringBuilder()
                    .append(SUB_GROUP_ZNODE_PATH)
                    .append(ZNODE_PATH_SEPERATER)
                    .append(system)
                    .toString();
        } else {
            SYSTEM_ZNODE_PATH = SUB_GROUP_ZNODE_PATH + "/system";
        }

        MASTER_ZNODE_PATH = new StringBuilder()
                .append(SYSTEM_ZNODE_PATH)
                .append(ZNODE_PATH_SEPERATER)
                .append("master")
                .toString();
    }

    public void parentNodeCheck() throws KeeperException, InterruptedException {
        //  /iot node
        if (zk.exists(GROUP_ZNODE_PATH, false) == null) {
            String returnPath = zk.create(GROUP_ZNODE_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.info("Create group node [{}]", returnPath);
        } else {
            logger.info("Exist group node [{}]", GROUP_ZNODE_PATH);
        }

        //  /iot/append
        if (zk.exists(SUB_GROUP_ZNODE_PATH, false) == null) {
            String returnPath = zk.create(SUB_GROUP_ZNODE_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.info("Create sub group node [{}]", returnPath);
        } else {
            logger.info("Exist sub group node [{}]", SUB_GROUP_ZNODE_PATH);
        }

        //  /iot/append/iotweb
        if (zk.exists(SYSTEM_ZNODE_PATH, false) == null) {
            String returnPath = zk.create(SYSTEM_ZNODE_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.info("Create system node [{}]", returnPath);
        } else {
            logger.info("Exist system node [{}]", SYSTEM_ZNODE_PATH);
        }
    }

}
