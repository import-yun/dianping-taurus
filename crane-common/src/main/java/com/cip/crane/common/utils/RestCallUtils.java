package com.cip.crane.common.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * @author chenchongze
 */
public class RestCallUtils {

    public static <T> T postRestCall(String targetUrl, FormDataMultiPart formDataMultiPart, Class<T> responseType, Integer connectTimeout, Integer readTimeout) {
        ClientConfig configuration = new ClientConfig();
        configuration.property(ClientProperties.CONNECT_TIMEOUT, connectTimeout);
        configuration.property(ClientProperties.READ_TIMEOUT, readTimeout);
        Client client = ClientBuilder.newClient(configuration).register(MultiPartFeature.class);//.register(JacksonFeature.class)
        WebTarget target = client.target(targetUrl);

        if (responseType == String.class) {
            return target.request().post(Entity.entity(formDataMultiPart, MediaType.MULTIPART_FORM_DATA), responseType);
        }

        String resStr = target.request().post(Entity.entity(formDataMultiPart, MediaType.MULTIPART_FORM_DATA), String.class);
        T result = getBeanFromJson(resStr, responseType);

        return result;
    }

    public static <T> T getRestCall(String targetUrl, Class<T> responseType) {
        return getRestCall(targetUrl, responseType, 1000, 1000);
    }

    public static <T> T getRestCall(String targetUrl, Class<T> responseType, Integer connectTimeout, Integer readTimeout) {
        ClientConfig configuration = new ClientConfig();
        configuration.property(ClientProperties.CONNECT_TIMEOUT, connectTimeout);
        configuration.property(ClientProperties.READ_TIMEOUT, readTimeout);
        Client client = ClientBuilder.newClient(configuration);
        WebTarget target = client.target(targetUrl);

        if (responseType == String.class) {
            return target.request().get(responseType);
        }

        String resStr = target.request().get(String.class);
        T result = getBeanFromJson(resStr, responseType);

        return result;
    }

    public static <T> T getBeanFromJson(String json, Class<T> responseType) {
        ObjectMapper objectMapper = new ObjectMapper();
        T result = null;
        try {
            result = objectMapper.readValue(json, responseType);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            String res = RestCallUtils.getRestCall("http://10.66.59.48:8080/api/runningtask/task_520", String.class);
            System.out.println(res);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
