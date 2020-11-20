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

        // Connected -> connection loss (called close) - > session expired
        // Connected -> connecting lost -> Disconnected Event -> session expired event ->

        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            zooKeeperMain.getConnectedSignal().countDown();
        }
        if (watchedEvent.getState() == Event.KeeperState.Expired) {
            try {
                zooKeeperMain.reConnect(watchedEvent.getState());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (watchedEvent.getState() == Event.KeeperState.Disconnected) {
            // connection loss 일 경우 해당 로직을 재시작 하는 로직은 중대한 시스템 오류를 발생할 가능성이 있음
        }
    }
}

