package com.eseict.zoo.springconfig;

import com.eseict.zoo.proc.ZooKeeperConnection;
import com.google.common.base.Strings;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
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
@EnableAspectJAutoProxy
public class ApplicationContextConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextConfig.class);
	private static final String CONFIG_FILE_NAME = "system.properties";

	/**
	 * Propoerty 로딩 클래스 빈 생성 설정<br>
	 * @return
	 */
	@Bean
	public PropertySourcesPlaceholderConfigurer confFileInfo(){
		PropertySourcesPlaceholderConfigurer config = new ConfFileInfo();
		Resource location = null;
        String homePath = System.getenv("TEST_HOME");
        if(Strings.isNullOrEmpty(homePath)){
            LOGGER.error("Can not find TEST_HOME");
        }else{
            StringBuilder sb = new StringBuilder(homePath);
            sb.append(File.separatorChar);
            sb.append(ApplicationContextConfig.CONFIG_FILE_NAME);
            location = new FileSystemResource(sb.toString());	//디플로이시 HOME 패스에서 컨피그 로딩
        }
		config.setLocation(location);
		return config;
	}

}
