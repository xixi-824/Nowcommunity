package com.nowcoder.nowcommunity.service;

import com.nowcoder.nowcommunity.dao.IAlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author lei
 * @date 2020/7/21 22:53
 */

@Service
//@Scope("prototype")
public class AlphaService {

    @Autowired
    private IAlphaDao alphaDao;

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    /**
     * 在创建构造器之后执行
     */
    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destory(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }
}
