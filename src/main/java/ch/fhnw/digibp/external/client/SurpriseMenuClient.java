/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.external.client;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.variable.value.TypedValue;
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
                .tenantIdIn("showcase")
                .handler((ExternalTask externalTask, ExternalTaskService externalTaskService) -> {
                    try {
                        Boolean vegetarianGuests = false;
                        TypedValue vegetarianGuestsValue = externalTask.getVariableTyped("vegetarianGuests");
                        if(vegetarianGuestsValue!=null)
                            vegetarianGuests = (Boolean) externalTask.getVariableTyped("vegetarianGuests").getValue();
                        String menu;
                        if(vegetarianGuests){
                            menu = Arrays.asList("pizza", "pasta", "verdura").get(new Random().nextInt(3));
                        } else {
                            menu = Arrays.asList("pizza", "pasta", "carne", "verdura").get(new Random().nextInt(4));
                        }
                        Map<String, Object> variables = new HashMap<>();
                        variables.put("menu", menu);

                        externalTaskService.complete(externalTask, variables);
                    } catch (Exception e) {
                        externalTaskService.handleBpmnError(externalTask, "failed");
                    }
                })
                .open();
    }

}
