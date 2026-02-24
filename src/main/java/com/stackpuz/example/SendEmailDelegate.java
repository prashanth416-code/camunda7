package com.stackpuz.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

@Qualifier("SendEmailDelegate")
public class SendEmailDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Get process variables
        String recipient = (String) execution.getVariable("recipient");
        String subject = (String) execution.getVariable("subject");
        String body = (String) execution.getVariable("body");

        // Simulate sending email (replace with actual email sending logic)
        System.out.println("Sending email to: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        //execution.getProcessEngine().getRuntimeService().

        execution.setVariables(Map.of("recipient", recipient, "subject", subject));

    }
}
