package com.eseict.zoo.proc;

import com.eseict.zoo.util.CommUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class MasterSlaveNodeWatcher implements Watcher {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private ZooKeeper zk;
    private MasterSlaveNodeProcess process;
    private Gson gson = new GsonBuilder().create();

    public MasterSlaveNodeWatcher(){
        super();
    }
    public MasterSlaveNodeWatcher(ZooKeeper zk){
        super();
        this.zk = zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public MasterSlaveNodeProcess getProcess() {
        return process;
    }

    public void setProcess(MasterSlaveNodeProcess process) {
        this.process = process;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

        String id = process.config.get(NodeConfig.PARAM_KEY.SERVER_ID) == null ? "" : (String)process.config.get(NodeConfig.PARAM_KEY.SERVER_ID);
        String mac = process.config.get(NodeConfig.PARAM_KEY.SERVER_MAC) == null ? "" : (String)process.config.get(NodeConfig.PARAM_KEY.SERVER_MAC);

        try {

            logger.info("path = {} , event = [{}]",watchedEvent.getPath(),watchedEvent.toString());
            if (zk.exists(process.MASTER_ZNODE_PATH, false) == null) {
                String path = zk.create(process.MASTER_ZNODE_PATH,
                        gson.toJson(CommUtil.getServerInfo(id, mac)).getBytes(StandardCharsets.UTF_8),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL);
                logger.info("create master node [{}]", path);
            } else {
                logger.info("Exist master node");
            }
            zk.exists(process.MASTER_ZNODE_PATH, this);

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}

