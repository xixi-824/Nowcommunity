package com.nowcoder.nowcommunity;

import com.nowcoder.nowcommunity.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NowcommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "这里可以赌博，可以嫖娼，可以9吸9毒9，可以##开##票###，哈哈哈";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
        text = "这里可以☆赌☆博☆，可以嫖娼，可以9吸9毒9，可以##开##票###，哈哈哈";
        System.out.println(sensitiveFilter.filter(text));
    }
}