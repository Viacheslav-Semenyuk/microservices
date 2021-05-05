package com.smartfoxpro.gateway.filter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.smartfoxpro.gateway.activemq.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class UserServicePreFilter extends ZuulFilter {

    @Value("${jms.queue.gateway-user}")
    private String gatewayUserQueue;

    @Autowired
    private SendMessage sendMessage;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 999;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        return context.get("proxy").equals("user-service")
                && !context.getRequest().getMethod().equals("GET");
    }


    @Override
    public Object run() {
        try {
            RequestContext context = RequestContext.getCurrentContext();
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonRequest = (JsonObject) jsonParser.parse(
                    new InputStreamReader(context.getRequest().getInputStream(), StandardCharsets.UTF_8));
            jsonObject.addProperty("tx_id", context.getRequest().getParameter("tx_id"));
            jsonObject.addProperty("service", context.get("proxy").toString());
            jsonObject.addProperty("method", context.getRequest().getMethod());
            jsonObject.addProperty("endpoint", context.get("requestURI").toString());
            jsonObject.add("request", gson.toJsonTree(jsonRequest));
            sendMessage.send(gatewayUserQueue, jsonObject.toString());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
