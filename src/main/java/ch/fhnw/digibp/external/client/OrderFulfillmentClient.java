/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.external.client;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class OrderFulfillmentClient {

    private Logger logger = LoggerFactory.getLogger(OrderFulfillmentClient.class);

    @Autowired
    ExternalTaskClient client;

    @PostConstruct
    private void subscribeTopics() {

        client.subscribe("Payment")
                .tenantIdIn("showcase")
                .handler((ExternalTask externalTask, ExternalTaskService externalTaskService) -> {
                    try {
                        if(new Random().nextInt(2)==0) // 1/3 fail
                            throw new Exception("Credit card expired");
                        String orderAmount = externalTask.getAllVariablesTyped().getValueTyped("amount").getValue().toString();
                        Map<String, Object> variables = new HashMap<>();
                        variables.put("transactionId", UUID.randomUUID().toString());
                        logger.info("Credit card charged with the amount of " + orderAmount + ". Transaction ID: " + variables.get("transactionId"));
                        externalTaskService.complete(externalTask, variables);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        externalTaskService.handleBpmnError(externalTask, "PaymentFailed", e.getMessage());
                    }
                })
                .open();

        client.subscribe("RefundPayment")
                .tenantIdIn("showcase")
                .handler((ExternalTask externalTask, ExternalTaskService externalTaskService) -> {
                    try {
                        String orderAmount = externalTask.getAllVariablesTyped().getValueTyped("amount").getValue().toString();
                        String transactionId = externalTask.getAllVariablesTyped().getValueTyped("transactionId").getValue().toString();
                        logger.info("Transaction with ID "+ transactionId +" rolled back. Amount of " + orderAmount + " refunded.");
                        externalTaskService.complete(externalTask);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        externalTaskService.handleBpmnError(externalTask, "RefundPaymentFailed");
                    }
                })
                .open();

        client.subscribe("Inventory")
                .tenantIdIn("showcase")
                .handler((ExternalTask externalTask, ExternalTaskService externalTaskService) -> {
                    try {
                        if(new Random().nextInt(2)==0) // 1/3 fail
                            throw new Exception("Out of stock");
                        logger.info("Goods fetched");
                        externalTaskService.complete(externalTask);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        externalTaskService.handleBpmnError(externalTask, "OutOfStock");
                    }
                })
                .open();

        client.subscribe("Shipping")
                .tenantIdIn("showcase")
                .handler((ExternalTask externalTask, ExternalTaskService externalTaskService) -> {
                    try {
                        logger.info("Goods delivered");
                        externalTaskService.complete(externalTask);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        externalTaskService.handleBpmnError(externalTask, "ShippingFailed");
                    }
                })
                .open();
    }

}
