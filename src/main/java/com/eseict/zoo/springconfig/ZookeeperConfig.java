package com.eseict.zoo.springconfig;

import com.eseict.zoo.proc.*;
import com.google.common.base.Strings;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * root-context.xml 대치용 java config 파일
 * @author eseict
 *
 */
@Configuration
public class ZookeeperConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperConfig.class);

	@Bean("zookeeperConnection")
	public ZooKeeper getZooKeeper() throws IOException, InterruptedException {
		String host="localhost:2181";
		ZooKeeperConnection connection = new ZooKeeperConnection();
		return connection.connect(host);
	}

	@Bean
	public RunningNodeProcess runningNodeProcess(
			@Qualifier("zookeeperConnection") ZooKeeper zk
	) throws ZookeeperException {

		//TODO 각 시스템에서 사용 하는 id 값 으로 처리
		String id = "asdfasdfasdf";
		String group = "iot";
		String subGroup = "append";
		String system = "iotweb";


		//config 생성
		NodeConfig config = new NodeConfig(id);
//		config.setProperty(NodeConfig.PARAM_KEY.SERVER_ID, serverId); // server id를 셋팅으로 처리해도됨
		config.setProperty(NodeConfig.PARAM_KEY.GROUP_PATH, group);
		config.setProperty(NodeConfig.PARAM_KEY.SUB_GROUP_PATH, subGroup);
		config.setProperty(NodeConfig.PARAM_KEY.SYSTEM_PATH, system);

		// running process 생성
		RunningNodeProcess runningNodeProcess = new RunningNodeProcess(zk);
		// running process 초기화
		runningNodeProcess.init(config);

		return runningNodeProcess;
	}

	@Bean
	public MasterSlaveNodeProcess masterSlaveNodeProcess(
			@Qualifier("zookeeperConnection") ZooKeeper zk
	) throws ZookeeperException {

		//TODO 각 시스템에서 사용 하는 id 값 으로 처리
		String id = "asdfasdfasdf";
		String group = "iot";
		String subGroup = "append";
		String system = "iotweb";

		// config 파일 생성
		NodeConfig config = new NodeConfig(id);
		config.setProperty(NodeConfig.PARAM_KEY.GROUP_PATH, group);
		config.setProperty(NodeConfig.PARAM_KEY.SUB_GROUP_PATH, subGroup);
		config.setProperty(NodeConfig.PARAM_KEY.SYSTEM_PATH, system);

		// process 파일 생성
		MasterSlaveNodeProcess masterSlaveNodeProcess = new MasterSlaveNodeProcess(zk);
		// watcher 생성 및 등록
		masterSlaveNodeProcess.setWatcher(new MasterSlaveNodeWatcher(zk));
		// master slave node 초기화
		masterSlaveNodeProcess.init(config);

		return masterSlaveNodeProcess;
	}


}
