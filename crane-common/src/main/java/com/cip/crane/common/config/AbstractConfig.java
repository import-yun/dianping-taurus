package com.cip.crane.common.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Properties;

/**
 * Author   mingdongli
 * 16/3/8  下午2:21.
 */
public abstract class AbstractConfig {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private String localFileName;

    public AbstractConfig(){

    }

    public AbstractConfig(String localFileName) {

        this.localFileName = localFileName;
    }

    protected void loadConfig() {

        if(localFileName == null){
            if(logger.isInfoEnabled()){
                logger.info("[loadConfig][localFileName null]");
            }
            return ;
        }

        InputStream ins = null;

        File file = new File(localFileName);

        if(!file.exists()){

            URL url = getClass().getClassLoader().getResource(localFileName);
            if(url != null){
                try {
                    ins = url.openStream();
                    if(logger.isInfoEnabled()){
                        logger.info("[loadConfig]" + url);
                    }
                } catch (IOException e) {
                    logger.error("[loadLocalConfig]" + url, e);
                }
            }

        }else{
            try {
                ins = new FileInputStream(file);
                if(logger.isInfoEnabled()){
                    logger.info("[loadConfig]" + file.getAbsolutePath());
                }
            } catch (FileNotFoundException e) {
                logger.error("[loadConfig]" + localFileName, e);
            }
        }

        if(ins == null){
            logger.warn("[loadLocalConfig][file not found]" + localFileName);
            return;
        }

        loadLocalConfig(ins);

    }

    private void loadLocalConfig(InputStream in) {
        Properties props = new Properties();
        try {
            props.load(in);
            in.close();
        } catch (IOException e1) {
            throw new RuntimeException(e1.getMessage(), e1);
        }

        for (String key : props.stringPropertyNames()) {

            String value = getValue(key, props).trim();
            setFieldValue(key, value);
        }

        if (logger.isInfoEnabled()) {
            Field[] fields = getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                if (!Modifier.isStatic(f.getModifiers())) {
                    try {
                        logger.info(f.getName() + "=" + f.get(this));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    protected void setFieldValue(final String rawKey, final String rawValue) {

        if(logger.isInfoEnabled()){
            logger.info("[setFieldValue]" + rawKey + ":" + rawValue);
        }

        String key = StringUtils.trimToNull(rawKey);
        if(key == null){
            throw new IllegalArgumentException("key empty:" + rawKey);
        }

        String value = StringUtils.trimToNull(rawValue);
        if(value == null){
            if(logger.isInfoEnabled()){
                logger.info("[setFieldValue][value empty, return!]" );
            }
            return;
        }

        Class<?> clazz = this.getClass();
        Field field = null;
        try {
            field = clazz.getDeclaredField(key);
        } catch (Exception e) {
            logger.error("unknown property found: " + key);
            return;
        }

        field.setAccessible(true);
        try {

            if (field.getType().equals(Integer.TYPE)) {
                field.set(this, Integer.parseInt(value));
            } else if (field.getType().equals(Long.TYPE)) {
                field.set(this, Long.parseLong(value));
            } else if (field.getType().equals(String.class)) {
                field.set(this, value);
            } else {
                field.set(this, Boolean.parseBoolean(value));
            }
        } catch (Exception e) {
            logger.error("can not parse property " + key +"," + value, e);
        }

    }

    protected String getValue(String key, Properties props) {

        return props.getProperty(key);
    }
}
