package com.stackpuz.example;


import lombok.NoArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionImpl;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Camundacontroller {

    @Autowired
    private final RuntimeService runtimeService;

    @Autowired
    private final TaskService taskService;

    @Autowired
    private final DecisionService decisionService;


    public Camundacontroller(RuntimeService runtimeService, TaskService taskService, DecisionService decisionService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.decisionService = decisionService;
    }

    //create a rest api to start the process instance
    @PostMapping("/start-process")
    public String startProcessInstance() {
        // Start the process instance using Camunda's RuntimeService
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_test", "businessKey123", Map.of("recipient", "", "subject", "", "body", ""));
        return "Process instance started with ID: " + processInstance.getId();
    }

    //create a rest api to complete the user task
    @PostMapping("/complete-task")
    public String completeUserTask(@RequestParam(name = "businessKey")String businessKey) {
        // Complete the user task using Camunda's TaskService
        String taskId = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult().getId();
        taskService.claim(taskId, "user1"); // Claim the task for a user (optional)
        taskService.complete(taskId);
        return "User task with ID: " + taskId + " has been completed.";
    }

    //create a rest api to publish a message to the process instance
    @PostMapping("/publish-message")
    public String publishMessage(@RequestParam(name = "businessKey")String businessKey, @RequestParam(name = "messageName")String messageName) {
        // Publish a message to the process instance using Camunda's RuntimeService
        runtimeService.createMessageCorrelation(messageName)
                .processInstanceBusinessKey(businessKey)
                .setVariables(Map.of("recipient", "", "subject", "", "body", ""))
                .correlate();
        return "Message '" + messageName + "' has been published to process instance with business key: " + businessKey;
    }

    //create a rest api to publish a signal to the process instance
    @PostMapping("/publish-signal")
    public String publishSignal(@RequestParam(name = "signalName")String signalName) {
        // Publish a signal to the process instance using Camunda's RuntimeService
        runtimeService.signalEventReceived(signalName);
        return "Signal '" + signalName + "' has been published.";
    }

    //create a rest api to  evaluate a decision table
    @PostMapping("/evaluate-decision")
    public String evaluateDecisionTable(@RequestParam(name = "decisionKey")String decisionKey, @RequestParam(name = "inputVariables")Map<String, Object> inputVariables) {
        // Evaluate a decision table using Camunda's RuntimeService
        Map<String, Object> decisionResult = decisionService.evaluateDecisionTableById(decisionKey, inputVariables).getSingleResult().getSingleEntry();

        return "Decision table '" + decisionKey + "' has been evaluated with result: " + decisionResult;
    }
}