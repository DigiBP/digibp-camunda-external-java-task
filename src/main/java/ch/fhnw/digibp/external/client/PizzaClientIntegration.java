/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.external.client;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class PizzaClientIntegration {

    private Logger logger = LoggerFactory.getLogger(PizzaClientIntegration.class);

    @Autowired
    ExternalTaskClient client;

    @PostConstruct
    private void subscribeTopics() {
        client.subscribe("GetMenuInt")
                .tenantIdIn("showcase")
                .handler((ExternalTask externalTask, ExternalTaskService externalTaskService) -> {
                    try {
                        HttpResponse<JsonNode> response = Unirest.get("https://hook.integromat.com/vhk7p513e4fmz7dvgxs65okwq6lx7243")
                                .asJson();

                        Map<String, Object> variables = new HashMap<>();
                        variables.put("pizzaList", response.getBody().getObject().get("PizzaMenu"));

                        externalTaskService.complete(externalTask, variables);
                    } catch (Exception e) {
                        externalTaskService.handleBpmnError(externalTask, "failed");
                    }
                })
                .open();


        client.subscribe("AddPizzaInt")
                .tenantIdIn("showcase")
                .handler((ExternalTask externalTask, ExternalTaskService externalTaskService) -> {
                    try {
                        String pizzaName = externalTask.getAllVariablesTyped().getValueTyped("name").getValue().toString();
                        Unirest.post("https://hook.integromat.com/bqfke9sdtrixnplwuyf99fnaken4rkc3")
                                .header("Content-Type", "application/json")
                                .body("{\n\t\"name\": \""+pizzaName+"\"\n}")
                                .asJson();

                        externalTaskService.complete(externalTask);
                    } catch (Exception e) {
                        externalTaskService.handleBpmnError(externalTask, "failed");
                    }
                })
                .open();
    }
}
