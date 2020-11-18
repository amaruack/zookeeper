package com.eseict.zoo;

import com.eseict.zoo.proc.*;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.util.List;

public class ZooApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooApplication.class);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String host="210.97.42.250:2181";
        ZooKeeperConnectionFactory zkf = new ZooKeeperConnectionFactory();
        zkf.setHost(host);
        ZooKeeper zk = zkf.getConnection();

//        ZooApplication aa = new ZooApplication();
//        ZooKeeper zk = aa.getZooKeeper();
        String path = "/rino/iot_append/receive_server/runnings";
        List<String> list =  zk.getChildren(path , false);

        for (String chi : list){
            System.out.println(chi);
            LOGGER.info("list {}", chi);
        }
        try {
            List<String> list2 =  zk.getChildren(path , false);
        } catch (KeeperException.SessionExpiredException e) {
            zkf.reConnect();
        }

        List<String> list3 =  zk.getChildren(path , false);

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
