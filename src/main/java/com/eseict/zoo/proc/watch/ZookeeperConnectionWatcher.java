package com.eseict.zoo.proc.watch;

import com.eseict.zoo.proc.ZooKeeperMain;
import com.eseict.zoo.exception.ZookeeperException;
import com.eseict.zoo.proc.node.NodeProcess;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ZookeeperConnectionWatcher implements Watcher {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    ZooKeeperMain zooKeeperMain;

    public ZookeeperConnectionWatcher(ZooKeeperMain zooKeeperMain) {
        super();
        this.zooKeeperMain = zooKeeperMain;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

        logger.debug("zookeeper watchedEvent state {}", watchedEvent.getState());
        logger.debug("zookeeper watchedEvent name {}", watchedEvent.getType().name());
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            zooKeeperMain.getConnectedSignal().countDown();
        }
        if (watchedEvent.getState() == Event.KeeperState.Expired) {
//               connectedSignal.
        }
        if (watchedEvent.getState() == Event.KeeperState.Disconnected) {
            try {
                zooKeeperMain.reConnect();
                List<NodeProcess> lists = zooKeeperMain.getWatcherHandler().get(Event.KeeperState.Disconnected.getIntValue());
                logger.info("Disconnected state node handler size {}",lists.size());
                if (lists != null && lists.size() > 0) {
                    for (NodeProcess nodeProcess : lists) {
                        nodeProcess.init();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ZookeeperException e) {
                e.printStackTrace();
            }

        }
    }
}

