/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.external.client;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class SurpriseMenuClient {

    @Autowired
    ExternalTaskClient client;

    @PostConstruct
    private void subscribeTopics() {

        client.subscribe("GetSurpriseMenu")
                .handler((ExternalTask externalTask, ExternalTaskService externalTaskService) -> {
                    try {
                        Map<String, Object> variables = new HashMap<>();
                        variables.put("menu", getRandomMenuItem(Arrays.asList("pizza", "pasta", "carne", "verdura")));

                        externalTaskService.complete(externalTask, variables);
                    } catch (Exception e) {
                        externalTaskService.handleBpmnError(externalTask, "failed");
                    }
                })
                .open();
    }

    public String getRandomMenuItem(List<String> items) {
        return items.get(new Random().nextInt(items.size()));
    }

}
