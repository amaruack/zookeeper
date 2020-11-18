package com.eseict.zoo.proc;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZooKeeperConnectionFactory {

   Logger logger = LoggerFactory.getLogger(this.getClass());
   private CountDownLatch connectedSignal = new CountDownLatch(1);
   private ZooKeeper zk;
   private String host;

   public void setHost(String host){
      this.host = host;
   }

   public void connect() throws IOException, InterruptedException {

      zk = new ZooKeeper(host, 5000, new Watcher() {
         @Override
         public void process(WatchedEvent event) {
            logger.debug("watchedEvent state {}",event.getState());
            logger.debug("watchedEvent name {}",event.getType().name());
            if (event.getState() == Event.KeeperState.SyncConnected) {
               connectedSignal.countDown();
            }
            if (event.getState() == Event.KeeperState.Expired) {
               try {
                  reConnect();
               } catch (IOException e) {
                  e.printStackTrace();
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      });

      connectedSignal.await();
//      return zk;
   }

   public ZooKeeper getConnection() throws IOException, InterruptedException {
      if (zk == null) {
         connect();
      }
      return zk;
   }

   public void reConnect() throws IOException, InterruptedException {
      connectedSignal = new CountDownLatch(1);
      connect();
   }
 
   public void close() throws InterruptedException {
      zk.close();
   }
}