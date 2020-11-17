package com.eseict.zoo;

import com.eseict.zoo.proc.MasterSlaveNodeWatcher;
import com.eseict.zoo.proc.NodeConfig;
import com.eseict.zoo.proc.RunningNodeProcess;
import com.eseict.zoo.proc.ServerInfo;
import com.eseict.zoo.util.CommUtil;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;


@Component
public class ServletInitializer extends SpringBootServletInitializer {
	Logger logger = LoggerFactory.getLogger(ServletInitializer.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ZooApplication.class);
	}

	@PostConstruct
	public void initialization() throws Exception {
		logger.info("Init success.");
	}
	
}
