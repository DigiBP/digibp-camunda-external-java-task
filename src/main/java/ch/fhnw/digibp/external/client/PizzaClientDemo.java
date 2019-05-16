/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.external.client;

import ch.fhnw.digibp.external.data.MenuRepository;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class PizzaClientDemo {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    ExternalTaskClient client;

    @PostConstruct
    private void subscribeTopics() {

        client.subscribe("GetMenu")
                //.tenantIdIn("showcase")
                .handler((ExternalTask externalTask, ExternalTaskService externalTaskService) -> {
                    try {
                        Map<String, Object> variables = new HashMap<>();
                        externalTask.getTenantId();
                        variables.put("pizzaList", menuRepository.getMenuItems(externalTask.getTenantId()));

                        externalTaskService.complete(externalTask, variables);
                    } catch (Exception e) {
                        externalTaskService.handleBpmnError(externalTask, "failed");
                    }
                })
                .open();


        client.subscribe("AddPizza")
                //.tenantIdIn("showcase")
                .handler((ExternalTask externalTask, ExternalTaskService externalTaskService) -> {
                    try {
                        String pizzaName = externalTask.getAllVariablesTyped().getValueTyped("pizzaName").getValue().toString();
                        menuRepository.setMenuItem(externalTask.getTenantId(), pizzaName);

                        externalTaskService.complete(externalTask);
                    } catch (Exception e) {
                        externalTaskService.handleBpmnError(externalTask, "failed");
                    }
                })
                .open();
    }
}