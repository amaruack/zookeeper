package com.eseict.zoo;

import com.eseict.zoo.proc.MasterSlaveNodeWatcher;
import com.eseict.zoo.proc.ZooKeeperConnection;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ZooApplicationTests {

    static Logger logger = LoggerFactory.getLogger(ZooApplicationTests.class);
    static String host = "localhost:2181";
    static String iotwebPath = "/iot/append/iotweb";
    static String masterNodePath = iotwebPath + "/master";

    @Test
    void contextLoads() {

        Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                ZooKeeperConnection connection = new ZooKeeperConnection();
                try {
                    ZooKeeper zk =  connection.connect(host);

                    Stat stat =  zk.exists(iotwebPath, false);
                    if (stat != null) {
                        logger.info(stat.toString());
                        byte[] dataByte = zk.getData(iotwebPath, false, null);
                        String pathData = new String(dataByte, "UTF-8");
                        logger.info("get datat [{}]",pathData);

                        if (zk.exists(masterNodePath, false) == null) {
                            logger.info("create master node");
                            zk.create(masterNodePath, "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                        } else {
                            logger.info("Exist master node");
                        }


                        Thread.sleep(5000);
//                        String path = "/iot/append/iotweb/slave-01";
//                        zk.delete(path, zk.exists(path, false).getVersion());

                    } else {
                        System.out.println("Node does not exists");
                    }

                    zk.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
            }
        });
        th1.start();

        Thread th2 = new Thread(new Runnable() {
            @Override
            public void run() {

                ZooKeeperConnection connection = new ZooKeeperConnection();
                try {
                    Thread.sleep(1000);
                    ZooKeeper zk =  connection.connect(host);

                    Stat stat =  zk.exists(iotwebPath, false);
                    if (stat != null) {
                        logger.info(stat.toString());
                        byte[] dataByte = zk.getData(iotwebPath, false, null);
                        String pathData = new String(dataByte, "UTF-8");
                        logger.info("get datat [{}]",pathData);

                        if (zk.exists(masterNodePath, false) == null) {
                            zk.create(masterNodePath, "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                        } else {
                            logger.info("Exist master node");
                            MasterSlaveNodeWatcher testWatcher = new MasterSlaveNodeWatcher();
                            zk.exists(masterNodePath, testWatcher);
//                            Stat masterStat = zk.exists(masterNodePath, true);
                        }

                        Thread.sleep(20000);
                    } else {
                        System.out.println("Node does not exists");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
            }
        });
        th2.start();
        try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
