package com.eseict.zoo;

import com.eseict.zoo.exception.ZookeeperException;
import com.eseict.zoo.proc.*;
import com.eseict.zoo.proc.node.MasterSlaveNodeProcess;
import com.eseict.zoo.proc.node.NodeConfig;
import com.eseict.zoo.proc.watch.MasterSlaveNodeWatcher;
import com.eseict.zoo.proc.node.RunningNodeProcess;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ZooApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooApplication.class);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException, ZookeeperException {
        String zookeeperHost="210.97.42.250:2181";

        ZooKeeperMain zooKeeperMain = new ZooKeeperMain();
        zooKeeperMain.setHost(zookeeperHost);

        //각 시스템에서 사용 하는 id 값 으로 처리 // secret 으로 처리
        String id = "idsfdfdsf";
        String host = "localhost";
        String port = "9999";
        String group = "rino";
        String subGroup = "test";
        String system = "test";

        // config 파일 생성
        NodeConfig config = new NodeConfig(id);
        config.setProperty(NodeConfig.PARAM_KEY.SERVER_HOST, host);
        config.setProperty(NodeConfig.PARAM_KEY.SERVER_PORT, port);
        config.setProperty(NodeConfig.PARAM_KEY.GROUP_PATH, group);
        config.setProperty(NodeConfig.PARAM_KEY.SUB_GROUP_PATH, subGroup);
        config.setProperty(NodeConfig.PARAM_KEY.SYSTEM_PATH, system);

        // process 파일 생성
        MasterSlaveNodeProcess masterSlaveNodeProcess = new MasterSlaveNodeProcess(zooKeeperMain);
        // watcher 생성 및 등록
        masterSlaveNodeProcess.setWatcher(new MasterSlaveNodeWatcher(zooKeeperMain));
        // master slave node 초기화
        masterSlaveNodeProcess.init(config);


        // running process 생성
        RunningNodeProcess runningNodeProcess = new RunningNodeProcess(zooKeeperMain);
        // running process 초기화
        runningNodeProcess.init(config);

        Thread.sleep(10000);


        Thread.sleep(50000);

//        ZooKeeperConnectionFactory zkf = new ZooKeeperConnectionFactory();
//        zkf.setHost(host);
//        ZooKeeper zk = zkf.getConnection();

//        ZooApplication aa = new ZooApplication();
//        ZooKeeper zk = aa.getZooKeeper();
//        String path = "/rino/iot_append/receive_server/runnings";
//        List<String> list =  zk.getChildren(path , false);
//
//        for (String chi : list){
//            System.out.println(chi);
//            LOGGER.info("list {}", chi);
//        }
//        try {
//            List<String> list2 =  zk.getChildren(path , false);
//        } catch (KeeperException.SessionExpiredException e) {
//            zkf.reConnect();
//        }
//
//        List<String> list3 =  zk.getChildren(path , false);

    }



//    public ZooKeeper getZooKeeper() throws IOException, InterruptedException {
//        String host="210.97.42.250:2181";
//        ZooKeeperConnection connection = new ZooKeeperConnection();
//        return connection.connect(host);
//    }

//    @Bean
//    public RunningNodeProcess runningNodeProcess(
//            @Qualifier("zookeeperConnection") ZooKeeper zk
//    ) throws ZookeeperException {
//
//        //TODO 각 시스템에서 사용 하는 id 값 으로 처리
//        String id = "asdfasdfasdf";
//        String group = "iot";
//        String subGroup = "append";
//        String system = "iotweb";
//
//
//        //config 생성
//        NodeConfig config = new NodeConfig(id);
////		config.setProperty(NodeConfig.PARAM_KEY.SERVER_ID, serverId); // server id를 셋팅으로 처리해도됨
//        config.setProperty(NodeConfig.PARAM_KEY.GROUP_PATH, group);
//        config.setProperty(NodeConfig.PARAM_KEY.SUB_GROUP_PATH, subGroup);
//        config.setProperty(NodeConfig.PARAM_KEY.SYSTEM_PATH, system);
//
//        // running process 생성
//        RunningNodeProcess runningNodeProcess = new RunningNodeProcess(zk);
//        // running process 초기화
//        runningNodeProcess.init(config);
//
//        return runningNodeProcess;
//    }
//
//    @Bean
//    public MasterSlaveNodeProcess masterSlaveNodeProcess(
//            @Qualifier("zookeeperConnection") ZooKeeper zk
//    ) throws ZookeeperException {
//
//        //TODO 각 시스템에서 사용 하는 id 값 으로 처리
//        String id = "asdfasdfasdf";
//        String group = "iot";
//        String subGroup = "append";
//        String system = "iotweb";
//
//        // config 파일 생성
//        NodeConfig config = new NodeConfig(id);
//        config.setProperty(NodeConfig.PARAM_KEY.GROUP_PATH, group);
//        config.setProperty(NodeConfig.PARAM_KEY.SUB_GROUP_PATH, subGroup);
//        config.setProperty(NodeConfig.PARAM_KEY.SYSTEM_PATH, system);
//
//        // process 파일 생성
//        MasterSlaveNodeProcess masterSlaveNodeProcess = new MasterSlaveNodeProcess(zk);
//        // watcher 생성 및 등록
//        masterSlaveNodeProcess.setWatcher(new MasterSlaveNodeWatcher(zk));
//        // master slave node 초기화
//        masterSlaveNodeProcess.init(config);
//
//        return masterSlaveNodeProcess;
//    }


}
