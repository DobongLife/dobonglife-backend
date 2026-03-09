package com.umust.dobonglife.domain.user.application.port;

public interface MailSender {

    void send(String to, String subject, String htmlContent);

    String renderTemplate(String templateName, String variableName, String variableValue);
}
