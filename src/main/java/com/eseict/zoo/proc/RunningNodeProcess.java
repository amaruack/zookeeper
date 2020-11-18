package com.eseict.zoo.proc;

import com.eseict.zoo.util.ZookeeperCommUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledFuture;

public class RunningNodeProcess {

    Logger logger = LoggerFactory.getLogger(RunningNodeProcess.class);

    private NodeConfig config;
    ZooKeeper zk;
    private ScheduledFuture<?> future; // schdeuler future

    String ZNODE_PATH_SEPERATER = "/";

    String GROUP_ZNODE_PATH = "";
    String SUB_GROUP_ZNODE_PATH = "";
    String SYSTEM_ZNODE_PATH = "";
    String RUNNING_ZNODE_PATH = "";
    String SERVER_INFO_ZNODE_PRE_PATH = "";
    String SERVER_INFO_ZNODE_PATH = "";

    Gson gson = new GsonBuilder().create();

    public RunningNodeProcess(){}
    public RunningNodeProcess(ZooKeeper zk){this.zk = zk;}

    public ZooKeeper getZookeeperConnection() {
        return zk;
    }

    public void setZookeeperConnection(ZooKeeper zk) {
        this.zk = zk;
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
            // sequence ephemeral node 로 unique 함
            SERVER_INFO_ZNODE_PATH = zk.create(SERVER_INFO_ZNODE_PRE_PATH,
                    gson.toJson(ZookeeperCommUtil.getServerInfo(id, mac)).getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL
            );
            logger.debug("Create server info node [{}]", SERVER_INFO_ZNODE_PATH);

            // scheduler 등록
            startScheduler();
            logger.info("RunningNodeProcess success.");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

        RUNNING_ZNODE_PATH = new StringBuilder()
                .append(SYSTEM_ZNODE_PATH)
                .append(ZNODE_PATH_SEPERATER)
                .append("runnings")
                .toString();

        SERVER_INFO_ZNODE_PRE_PATH = new StringBuilder()
                .append(RUNNING_ZNODE_PATH)
                .append(ZNODE_PATH_SEPERATER)
                .append("server-")
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

        //  /iot/append/iotweb/runnings
        if (zk.exists(RUNNING_ZNODE_PATH, false) == null) {
            String returnPath = zk.create(RUNNING_ZNODE_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.debug("Create system node [{}]", returnPath);
        } else {
            logger.debug("Exist system node [{}]", RUNNING_ZNODE_PATH);
        }
    }

    public boolean setServerMonitoringInfo() throws UnknownHostException, KeeperException, InterruptedException {

        String id = this.config.get(NodeConfig.PARAM_KEY.SERVER_ID) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.SERVER_ID);
        String mac = this.config.get(NodeConfig.PARAM_KEY.SERVER_MAC) == null ? "" : (String)this.config.get(NodeConfig.PARAM_KEY.SERVER_MAC);

        ServerInfo serverInfo = ZookeeperCommUtil.getServerInfo();
  		serverInfo.setId(id);
		serverInfo.setMac(mac);

        Stat serverMonitoringStat = zk.exists(SERVER_INFO_ZNODE_PATH, false);

        if (serverMonitoringStat != null) {
            Stat returnStat = zk.setData(SERVER_INFO_ZNODE_PATH, gson.toJson(serverInfo).getBytes(StandardCharsets.UTF_8), serverMonitoringStat.getVersion());
            logger.debug("Set Server monitoring Info [{}]", returnStat.toString());
        } else {
            logger.debug("Not Exist server monitoring info node [{}]", SERVER_INFO_ZNODE_PATH);
        }

        return false;
    }

    public void startScheduler() throws ZookeeperException {

        String cronExpression = "0 * * * * *";
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        ScheduledFuture<?> future = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                logger.debug("current id [{}][{}]", Thread.currentThread().getName(),  Thread.currentThread().getId());

                try {
                    String id = config.get(NodeConfig.PARAM_KEY.SERVER_ID) == null ? "" : (String)config.get(NodeConfig.PARAM_KEY.SERVER_ID);
                    String mac = config.get(NodeConfig.PARAM_KEY.SERVER_MAC) == null ? "" : (String)config.get(NodeConfig.PARAM_KEY.SERVER_MAC);

                    ServerInfo serverInfo = ZookeeperCommUtil.getServerInfo();
                    serverInfo.setId(id);
                    serverInfo.setMac(mac);

                    Stat serverMonitoringStat = zk.exists(SERVER_INFO_ZNODE_PATH, false);

                    if (serverMonitoringStat != null) {
                        Stat returnStat = zk.setData(SERVER_INFO_ZNODE_PATH, gson.toJson(serverInfo).getBytes(StandardCharsets.UTF_8), serverMonitoringStat.getVersion());
                        logger.debug("Set Server monitoring Info [{}]", returnStat.toString());
                    } else {
                        logger.debug("Not Exist server monitoring info node [{}]", SERVER_INFO_ZNODE_PATH);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }


            }
        }, new CronTrigger(cronExpression));
        this.future = future;
    }


}
