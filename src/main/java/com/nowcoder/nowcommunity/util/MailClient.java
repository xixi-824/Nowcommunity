package com.nowcoder.nowcommunity.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

    // 日志记录
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     *
     * @param to 收件人
     * @param subject 标题
     * @param content 正文
     */
    public void sendMail(String to,String subject,String content){
        // 构建MimeMessage，写入邮件内容
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            // 邮件设置
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 设置为true，支持html文本
            helper.setText(content,true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            // 发送邮件出现错误，打印日志
            logger.error("发送邮件失败"+e.getMessage());
        }
    }
}
