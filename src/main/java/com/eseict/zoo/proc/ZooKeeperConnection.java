package com.eseict.zoo.proc;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZooKeeperConnection {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   private ZooKeeper zk;
   final CountDownLatch connectedSignal = new CountDownLatch(1);
 
   public ZooKeeper connect(String host) throws IOException, InterruptedException {
      zk = new ZooKeeper(host, 5000, new Watcher() {
         @Override
         public void process(WatchedEvent event) {
            logger.info("get watch event");
            logger.info("watchedEvent state {}",event.getState());
            logger.info("watchedEvent name {}",event.getType().name());
            if (event.getState() == Event.KeeperState.SyncConnected) {
               connectedSignal.countDown();
            }
         }
      });
      connectedSignal.await();
      return zk;
   }
 
   public void close() throws InterruptedException {
      zk.close();
   }
}