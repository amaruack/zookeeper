package com.eseict.zoo.proc.watch;

import com.eseict.zoo.proc.ZooKeeperMain;
import com.eseict.zoo.exception.ZookeeperException;
import com.eseict.zoo.proc.node.MasterSlaveNodeProcess;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterSlaveNodeWatcher implements Watcher {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private ZooKeeperMain zooKeeperMain;
    private MasterSlaveNodeProcess process;
    private Gson gson = new GsonBuilder().create();

    public MasterSlaveNodeWatcher(ZooKeeperMain zooKeeperMain) {
        super();
        this.zooKeeperMain = zooKeeperMain;
    }

    public MasterSlaveNodeProcess getProcess() {
        return process;
    }

    public void setProcess(MasterSlaveNodeProcess process) {
        this.process = process;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            logger.debug("receive master watchedEvent state {}", watchedEvent.getState());
            logger.debug("receive master watchedEvent name {}", watchedEvent.getType().name());
//            if (watchedEvent.getState() != Event.KeeperState.SyncConnected &&
//                    watchedEvent.getType() == Event.EventType.NodeDeleted){
            if (watchedEvent.getType() == Event.EventType.NodeDeleted){
                    logger.debug("action master watchedEvent state {}", watchedEvent.getState());
                    logger.debug("action master watchedEvent name {}", watchedEvent.getType().name());
                    process.init(this.process.config);
            }
        } catch (ZookeeperException e) {
            e.printStackTrace();
        }

    }
}

