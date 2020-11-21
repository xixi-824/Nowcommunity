package com.nowcoder.nowcommunity;

import com.nowcoder.nowcommunity.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NowcommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private  TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("2389588874@qq.com","TEST","华南理工大学自动化科学与工程学院");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        // 存入参数
        context.setVariable("username","SCUT");

        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);

        mailClient.sendMail("2389588874@qq.com","HTML",process);
    }
}
