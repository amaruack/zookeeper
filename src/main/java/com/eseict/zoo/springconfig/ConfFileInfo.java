package com.eseict.zoo.springconfig;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;
import java.util.Properties;

public class ConfFileInfo extends PropertySourcesPlaceholderConfigurer {
    private static ConfFileInfo confFileInfo = null;
    private Properties mergedProperties = new Properties();

    public ConfFileInfo() {
    }

    public static ConfFileInfo getInstance() {
        return confFileInfo;
    }

    protected void loadProperties(Properties props) {
        try {
            super.loadProperties(props);
            Object[] keySet = props.keySet().toArray();
            Object[] var3 = keySet;
            int var4 = keySet.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Object key = var3[var5];
                String val = props.getProperty(key.toString());
                this.mergedProperties.setProperty(key.toString(), val);
            }

            confFileInfo = this;
        } catch (IOException var8) {
            this.logger.error(var8.getMessage(), var8);
        }

    }

    public void reloadProperties() throws IOException {
        this.loadProperties(this.mergedProperties);
    }

    public static String getString(String key) {
        return confFileInfo != null ? confFileInfo.mergedProperties.getProperty(key) : null;
    }

    public static String get(String key) {
        return getString(key);
    }

    public static void clearProperties() {
        confFileInfo.mergedProperties = new Properties();
    }
}
