package com.umust.dobonglife.global.port.user.out;

public interface MailSender {
    void send(String to, String subject, String htmlContent);
    String renderTemplate(String templateName, String variableName, String variableValue);
}
