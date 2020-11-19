package com.eseict.zoo.proc;

import com.eseict.zoo.proc.node.NodeProcess;
import com.eseict.zoo.proc.watch.ZookeeperConnectionWatcher;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ZooKeeperMain {

   Logger logger = LoggerFactory.getLogger(this.getClass());
   private ZooKeeper zk;
   volatile CountDownLatch connectedSignal = new CountDownLatch(1);
   private String host;
   private Map<Integer, List<NodeProcess>> watcherHandler = new HashMap<>();

   public void setHost(String host){
      this.host = host;
   }

   public String getHost() {
      return host;
   }

   public CountDownLatch getConnectedSignal() {
      return connectedSignal;
   }

   public Map<Integer, List<NodeProcess>> getWatcherHandler() {
      return watcherHandler;
   }

   public boolean addWatcherHandler(Watcher.Event.KeeperState state, NodeProcess nodeProcess){
      List<NodeProcess> processes = watcherHandler.get(state.getIntValue());
      if (processes == null) {
         processes = new ArrayList<>();
         watcherHandler.put(state.getIntValue(), processes);
      }
      return processes.add(nodeProcess);
   }

   public List<NodeProcess> setWatcherHandler(Watcher.Event.KeeperState state, List<NodeProcess> nodeProcesses){
      return watcherHandler.put(state.getIntValue(), nodeProcesses);
   }

   public synchronized ZooKeeper getConnect() throws IOException, InterruptedException {
      if (zk == null || !zk.getState().isConnected() || !zk.getState().isAlive()){
         connectedSignal = new CountDownLatch(1);
         zk = new ZooKeeper(this.host, 10000, new ZookeeperConnectionWatcher(this));
//         zk = new ZooKeeper(this.host, 20000, new Watcher() {
//            @Override
//            public void process(WatchedEvent event) {
//               logger.debug("watchedEvent state {}",event.getState());
//               logger.debug("watchedEvent name {}",event.getType().name());
//               if (event.getState() == Event.KeeperState.SyncConnected) {
//                  connectedSignal.countDown();
//               }
//               if (event.getState() == Event.KeeperState.Expired) {
////               connectedSignal.
////                  watcherHandler.clear();
//               }
//            }
//         });
         connectedSignal.await();
      }
      return zk;
   }

   public ZooKeeper getConnect(String host) throws IOException, InterruptedException {
      this.host = host;
      return getConnect();
   }

   public ZooKeeper reConnect() throws IOException, InterruptedException {
      zk.close();
      return getConnect();
   }
 
   public void close() throws InterruptedException {
      zk.close();
   }
}