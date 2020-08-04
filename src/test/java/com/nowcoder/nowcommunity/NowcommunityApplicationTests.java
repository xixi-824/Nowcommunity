package com.nowcoder.nowcommunity;

import com.nowcoder.nowcommunity.dao.IAlphaDao;
import com.nowcoder.nowcommunity.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.pattern.PathPattern;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NowcommunityApplication.class)
class NowcommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);

		IAlphaDao alphaDaoHibernate = applicationContext.getBean(IAlphaDao.class);
		System.out.println(alphaDaoHibernate.select());

		alphaDaoHibernate = applicationContext.getBean("AlphaDaoHibernate",IAlphaDao.class);
		System.out.println(alphaDaoHibernate.select());
	}

	@Test
	public void testBeanManagement(){
		// Spring管理的Bean对象是单例的，它需要获取该Bean类的实例对象时，才在spring容器中创建该对象(懒汉模式)
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired //自动注入后，跳过@Resource
	@Resource(name = "AlphaDaoHibernate")
	private IAlphaDao alphaDao;

	@Autowired
	private AlphaService alphaService;

	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test
	public void testDI(){
		System.out.println(alphaDao.select());
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
	}
}
