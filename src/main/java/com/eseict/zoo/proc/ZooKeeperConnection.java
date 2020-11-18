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
   private String host;

   public ZooKeeper connect(String host) throws IOException, InterruptedException {

      zk = new ZooKeeper(host, 20000, new Watcher() {
         @Override
         public void process(WatchedEvent event) {
            logger.debug("watchedEvent state {}",event.getState());
            logger.debug("watchedEvent name {}",event.getType().name());
            if (event.getState() == Event.KeeperState.SyncConnected) {
               connectedSignal.countDown();
            }
            if (event.getState() == Event.KeeperState.Expired) {
//               connectedSignal.
            }
         }
      });

      connectedSignal.await();
      return zk;
   }

   public void reConnect(){

   }
 
   public void close() throws InterruptedException {
      zk.close();
   }
}