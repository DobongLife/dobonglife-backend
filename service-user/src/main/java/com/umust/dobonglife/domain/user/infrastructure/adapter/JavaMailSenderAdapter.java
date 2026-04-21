package com.umust.dobonglife.domain.user.infrastructure.adapter;

import com.umust.dobonglife.domain.user.application.port.out.MailSender;
import com.umust.dobonglife.infra.error.InfraErrorCode;
import com.umust.dobonglife.infra.error.InfraException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@RequiredArgsConstructor
public class JavaMailSenderAdapter implements MailSender {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void send(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            throw new InfraException(InfraErrorCode.MAIL_SEND_FAILED);
        }
    }

    @Override
    public String renderTemplate(String templateName, String variableName, String variableValue) {
        Context context = new Context();
        context.setVariable(variableName, variableValue);
        return templateEngine.process(templateName, context);
    }
}
