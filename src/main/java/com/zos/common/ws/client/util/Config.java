package com.zos.common.ws.client.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

@Slf4j
public class Config {

    private static String CONFIG_PROPERTIES = "config.properties";
    private static Properties properties = null;

    static {

        try {
            if (properties == null) {
                log.info("=============开始加载" + CONFIG_PROPERTIES + "===========");
                properties = new Properties();
                //String filePath = System.getProperty("user.dir") + "/conf/" + CONFIG_PROPERTIES;
                //log.info("filePath:" + filePath);
//                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
                //InputStream is = new BufferedInputStream(new FileInputStream(filePath));
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( CONFIG_PROPERTIES );
                properties.load(is);
                log.info(properties.getProperty("wsUrl"));
                log.info(properties.getProperty("httpUrl"));
                log.info("=============加载完成" + CONFIG_PROPERTIES + "===========");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(1);
    }

    public static String getConfigProperties(String key) {
        return properties.getProperty(key);
    }

}
