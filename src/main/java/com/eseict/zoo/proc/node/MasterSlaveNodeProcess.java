package com.eseict.zoo.proc.node;

import com.eseict.zoo.proc.ZooKeeperMain;
import com.eseict.zoo.exception.ZookeeperException;
import com.eseict.zoo.proc.watch.MasterSlaveNodeWatcher;
import com.eseict.zoo.util.ZookeeperCommUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class MasterSlaveNodeProcess implements NodeProcess {

    private Logger logger = LoggerFactory.getLogger(MasterSlaveNodeProcess.class);
    private ZooKeeper zk;
    private ZooKeeperMain zooKeeperMain;
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
    public MasterSlaveNodeProcess(ZooKeeperMain zooKeeperMain){
        setZookeeperConnection(zooKeeperMain);
    }

    public boolean isMaster(){
        return masterYn;
    }

    public ZooKeeperMain getZookeeperConnection() {
        return zooKeeperMain;
    }

    public void setZookeeperConnection(ZooKeeperMain zookeeperMain) {
        this.zooKeeperMain = zookeeperMain;
        // 여기서 handler add 처리 해야됨
        this.zooKeeperMain.addWatcherHandler(Watcher.Event.KeeperState.Expired, this);
//        this.zooKeeperMain.addWatcherHandler(Watcher.Event.KeeperState.Disconnected, this);
    }

    public MasterSlaveNodeWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(MasterSlaveNodeWatcher watcher) {
        this.watcher = watcher;
        watcher.setProcess(this);
    }

    public void init() throws ZookeeperException {
        init(this.config);
    }

    @Override
    public void init(NodeConfig config) throws ZookeeperException {

        try {
            this.config = config;
            this.zk = zooKeeperMain.getConnect();

            String id = this.config.get(NodeConfig.PARAM_KEY.SERVER_ID) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.SERVER_ID);
            String mac = this.config.get(NodeConfig.PARAM_KEY.SERVER_MAC) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.SERVER_MAC);
            String host = this.config.get(NodeConfig.PARAM_KEY.SERVER_HOST) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.SERVER_HOST);
            String port = this.config.get(NodeConfig.PARAM_KEY.SERVER_PORT) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.SERVER_PORT);

            String os = this.config.get(NodeConfig.PARAM_KEY.OS_NAME) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.OS_NAME);
            String homePath = this.config.get(NodeConfig.PARAM_KEY.HOME_PATH) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.HOME_PATH);

            if (Strings.isNullOrEmpty(id)) {
                throw new ZookeeperException("id not set");
            }
            // path generate
            pathGen();
            // parent node check
            parentNodeCheck();

            if (zk.exists(MASTER_ZNODE_PATH, false) == null) {
                String result = zk.create(MASTER_ZNODE_PATH,
                        gson.toJson(ZookeeperCommUtil.getServerInfo(id, mac, host, port, os, homePath)).getBytes(StandardCharsets.UTF_8),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL);
                logger.debug("create master node [{}]", result);
                masterYn = true;
            } else {
                logger.debug("Exist master node");
            }
            // master 나 slave 중 하나라도 생성시에 watcher 생성
            zk.exists(MASTER_ZNODE_PATH, watcher);
            logger.info("MasterSlaveNodeProcess success.");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            logger.debug("Create group node [{}]", returnPath);
        } else {
            logger.debug("Exist group node [{}]", GROUP_ZNODE_PATH);
        }

        //  /iot/append
        if (zk.exists(SUB_GROUP_ZNODE_PATH, false) == null) {
            String returnPath = zk.create(SUB_GROUP_ZNODE_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.debug("Create sub group node [{}]", returnPath);
        } else {
            logger.debug("Exist sub group node [{}]", SUB_GROUP_ZNODE_PATH);
        }

        //  /iot/append/iotweb
        if (zk.exists(SYSTEM_ZNODE_PATH, false) == null) {
            String returnPath = zk.create(SYSTEM_ZNODE_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.debug("Create system node [{}]", returnPath);
        } else {
            logger.debug("Exist system node [{}]", SYSTEM_ZNODE_PATH);
        }
    }

}
