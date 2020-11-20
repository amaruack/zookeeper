package com.eseict.zoo.proc;

import com.eseict.zoo.exception.ZookeeperException;
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

   private Integer CONNECTION_TIMEOUT = 10000;
   private Integer COUNT_DOWN_NUMBER = 1;

   /**
    * zookeeper connection timeout setting
    * default time is 10000 milliseconds
    * @param connectionTimeout milliseconds
    */
   public void setConnectionTimeout(Integer connectionTimeout){
      this.CONNECTION_TIMEOUT = connectionTimeout;
   }

   /**
    * if zookeeper receive SyncConnected that count number is -1
    * @param count - count down number
    */
   public void setCountDownNumber(Integer count) {
      this.COUNT_DOWN_NUMBER = count;
   }


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
         connectedSignal = new CountDownLatch(COUNT_DOWN_NUMBER);
         zk = new ZooKeeper(this.host, CONNECTION_TIMEOUT, new ZookeeperConnectionWatcher(this));
         connectedSignal.await();
      }
      return zk;
   }

   public ZooKeeper getConnect(String host) throws IOException, InterruptedException {
      this.host = host;
      return getConnect();
   }

   public ZooKeeper reConnect(Watcher.Event.KeeperState state) throws IOException, InterruptedException {
      close();
      getConnect();
      try {
         List<NodeProcess> lists = getWatcherHandler().get(state.getIntValue());
         logger.info("{} handler size {}",state, lists.size());
         if (lists != null && lists.size() > 0) {
            for (NodeProcess nodeProcess : lists) {
               nodeProcess.init();
            }
         }
      } catch (ZookeeperException e) {
         e.printStackTrace();
      }

//      List<NodeProcess> lists = zooKeeperMain.getWatcherHandler().get(Event.KeeperState.Expired.getIntValue());
//      logger.info("expired event node handler size {}",lists.size());
//      if (lists != null && lists.size() > 0) {
//         for (NodeProcess nodeProcess : lists) {
//            nodeProcess.init();
//         }
//      }

      return this.zk;
   }
 
   public void close() throws InterruptedException {
      if (zk != null && zk.getState().isAlive()){
         zk.close();
      }
   }
}