package com.eseict.zoo.proc;

import org.apache.jute.Record;
import org.apache.zookeeper.*;
import org.apache.zookeeper.client.ConnectStringParser;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.StaticHostProvider;
import org.apache.zookeeper.client.ZooKeeperSaslClient;
import org.apache.zookeeper.common.PathUtils;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.*;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.AsyncCallback.ACLCallback;
import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.MultiCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperWrap {

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

    public ZooKeeperSaslClient getSaslClient() {
        return zk.getSaslClient();
    }

    public ZooKeeper getZooKeeper() {
        return zk;
    }

    public long getSessionId() {
        return zk.getSessionId();
    }

    public byte[] getSessionPasswd() {
        return zk.getSessionPasswd();
    }

    public int getSessionTimeout() {
        return zk.getSessionTimeout();
    }

    public void addAuthInfo(String scheme, byte[] auth) {
        zk.addAuthInfo(scheme, auth);
    }

//    private boolean

    public synchronized void register(Watcher watcher) {
        zk.register(watcher);
    }

    public String create(String path, byte[] data, List<ACL> acl, CreateMode createMode) throws KeeperException, InterruptedException {

        return zk.create(path, data, acl, createMode);

    }

    public void create(String path, byte[] data, List<ACL> acl, CreateMode createMode, StringCallback cb, Object ctx) {
        zk.create(path, data, acl, createMode, cb, ctx);
    }

    public void delete(String path, int version) throws InterruptedException, KeeperException {
        zk.delete(path, version);
    }

    public List<OpResult> multi(Iterable<Op> ops) throws InterruptedException, KeeperException {
        return zk.multi(ops);
    }

    public void multi(Iterable<Op> ops, MultiCallback cb, Object ctx) {
        zk.multi(ops, cb, ctx);
    }

    public Transaction transaction() {
        return zk.transaction();
    }

    public void delete(String path, int version, VoidCallback cb, Object ctx) {
        zk.delete(path, version, cb, ctx);
    }

    public Stat exists(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zk.exists(path, watcher);
    }

    public Stat exists(String path, boolean watch) throws KeeperException, InterruptedException {
        return zk.exists(path, watch);
    }

    public void exists(String path, Watcher watcher, StatCallback cb, Object ctx) {
        zk.exists(path, watcher, cb, ctx);
    }

    public void exists(String path, boolean watch, StatCallback cb, Object ctx) {
        zk.exists(path, watch, cb, ctx);
    }

    public byte[] getData(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
        return zk.getData(path, watcher, stat);
    }

    public byte[] getData(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
        return zk.getData(path, watch, stat);
    }

    public void getData(String path, Watcher watcher, DataCallback cb, Object ctx) {
        zk.getData(path, watcher, cb, ctx);
    }

    public void getData(String path, boolean watch, DataCallback cb, Object ctx) {
        zk.getData(path, watch, cb, ctx);
    }

    public Stat setData(String path, byte[] data, int version) throws KeeperException, InterruptedException {
        return zk.setData(path, data, version);
    }

    public void setData(String path, byte[] data, int version, StatCallback cb, Object ctx) {
        zk.setData(path, data, version, cb, ctx);
    }

    public List<ACL> getACL(String path, Stat stat) throws KeeperException, InterruptedException {
        return zk.getACL(path, stat);
    }

    public void getACL(String path, Stat stat, ACLCallback cb, Object ctx) {
        zk.getACL(path, stat, cb, ctx);
    }

    public Stat setACL(String path, List<ACL> acl, int aclVersion) throws KeeperException, InterruptedException {
        return zk.setACL(path, acl, aclVersion);
    }

    public void setACL(String path, List<ACL> acl, int version, StatCallback cb, Object ctx) {
        zk.setACL(path, acl, version, cb, ctx);
    }

    public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zk.getChildren(path, watcher);
    }

    public List<String> getChildren(String path, boolean watch) throws KeeperException, InterruptedException {
        return zk.getChildren(path, watch);
    }

    public void getChildren(String path, Watcher watcher, ChildrenCallback cb, Object ctx) {
        zk.getChildren(path, watcher, cb, ctx);
    }

    public void getChildren(String path, boolean watch, ChildrenCallback cb, Object ctx) {
        zk.getChildren(path, watch, cb, ctx);
    }

    public List<String> getChildren(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
        return zk.getChildren(path, watcher, stat);
    }

    public List<String> getChildren(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
        return zk.getChildren(path, watch, stat);
    }

    public void getChildren(String path, Watcher watcher, Children2Callback cb, Object ctx) {
        zk.getChildren(path, watcher, cb, ctx);
    }

    public void getChildren(String path, boolean watch, Children2Callback cb, Object ctx) {
        zk.getChildren(path, watch, cb, ctx);
    }

    public void sync(String path, VoidCallback cb, Object ctx) {
        zk.sync(path, cb, ctx);
    }

    public ZooKeeper.States getState() {
        return zk.getState();
    }

    public String toString() {
        return zk.toString();
    }

}
