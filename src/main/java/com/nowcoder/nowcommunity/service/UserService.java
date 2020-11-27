package com.nowcoder.nowcommunity.service;

import com.nowcoder.nowcommunity.dao.LoginTicketMapper;
import com.nowcoder.nowcommunity.dao.UserMapper;
import com.nowcoder.nowcommunity.entity.LoginTicket;
import com.nowcoder.nowcommunity.entity.User;
import com.nowcoder.nowcommunity.util.CommunityConstant;
import com.nowcoder.nowcommunity.util.CommunityUtil;
import com.nowcoder.nowcommunity.util.HostHolder;
import com.nowcoder.nowcommunity.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author lei
 * @date 2020/8/2 9:40
 */
@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Value(value = "${community.path.domain}")
    private String domain;

    @Value(value = "${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    /**
     * 登录检查及登录凭证生成
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map = new HashMap<>();

        // 1、判断用户名与密码是否为空
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","用户密码不能为空");
            return map;
        }

        // 2、用户名合法性验证
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","当前用户不存在，请注册后登陆");
            return map;
        }

        // 3、密码有效性验证
        password = CommunityUtil.md5(getString(password,user.getSalt()));
        if(!password.equals(user.getPassword())){
            map.put("passwordMsg","用户名或密码输入不正确");
            return map;
        }

        // 用户名与密码匹配，生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus(0);
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        // new Date()方法设置的是毫秒
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        // 登录凭证存入数据库
        loginTicketMapper.insertLoginTicket(loginTicket);

        // 登录凭证发送至客户端(以cookie形式，下次自动登录时，检查凭证是否匹配即可免登录)
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录，修改登录用户的登录凭证状态为1
     * @param ticket
     * @return
     */
    public int logout(String ticket){
        return loginTicketMapper.updateStatus(ticket,1);
    }

    /**
     * 验证码核对
     * @param code 验证码客户端输入结果
     * @param kaptcha 验证码正确答案
     * @return
     */
    public boolean kaptchaCheck(String code,String kaptcha){
        if(StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !kaptcha.equalsIgnoreCase(code)){
            return false;
        }
        return true;
    }

    /**
     * 账号注册功能
     * @param user 用户信息
     * @param confirmPassword 用户确认密码
     * @return
     */
    public Map<String,Object> regist(User user,String confirmPassword){
        Map<String,Object> map = new HashMap<>();
        if(user == null){
            throw new IllegalArgumentException("参数不能为空!");
        }

        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }

        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        if(!user.getPassword().equals(confirmPassword)){
            map.put("confirmPasswordMsg","密码与确认密码不一致");
            return map;
        }

        // 验证用户名是否已被注册
        User checkUser = userMapper.selectByName(user.getUsername());
        if(checkUser != null){
            map.put("usernameMsg","该用户名已被注册");
            return map;
        }

        // 验证邮箱是否已被注册
        User byEmail = userMapper.selectByEmail(user.getEmail());
        if(byEmail != null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }

        // 注册用户操作
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        String result = getString(user.getPassword(),user.getSalt());
        if(result == null){
            map.put("passwordMsg","密码加密异常，请联系管理员");
        }
        user.setPassword(CommunityUtil.md5(result));

        // 普通用户
        user.setType(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        // 0:表示未激活
        user.setStatus(0);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // 拼接激活连接
        // http://localhost:8088/community/activatopn/101/code
        // String引用类型变量的字符串拼接，JVM 只有在 String 对象直接拼接的时候才会进行优化
        // 因此此处采用StringBuilder进行字符串拼接比较好
        String url = getString(domain,contextPath,"/activation/",String.valueOf(user.getId()),"/",user.getActivationCode());
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    /**
     * 使用StringBuilder进行字符串拼接
     * @param args
     * @return
     */
    private String getString(String...args){
        if(args == null || args.length < 1){
            return null;
        }

        // 新建一个StringBuilder对象
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i < args.length;i++){
            sb.append(args[i]);
        }
        return sb.toString();
    }

    /**
     * 根据激活邮件激活注册用户
     * @param userId 注册用户的id
     * @param code 激活码
     * @return
     */
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user != null && user.getStatus() == 1){
            // 当前用户已经激活成功或当前用户不存在
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            // 激活用户
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            // 当前用户为空或者激活码不匹配
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 查询当前登录用户的登录凭证
     * @param ticket
     * @return
     */
    public LoginTicket getLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }


    public Map<String,String> updatePassword(String oldPassword,String newPassword,String confirmPassword){
        Map<String,String> map = new HashMap<>();
        // 检查当前用户是否登录
        User user = hostHolder.getUser();
        if(user == null){
            map.put("LoginError","当前用户未登录");
            return map;
        }

        // 2、密码输入是否为空
        if(StringUtils.isBlank(oldPassword)){
            map.put("oldPasswordError","旧密码输入不能为空");
            return map;
        }

        // 3、新密码与确认密码是否一致
        // 1、创建正则表达式对象
        // 密码包含 数字,英文,字符中的两种以上，长度8-20
        String passwordPatt = "^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?!([^(0-9a-zA-Z)])+$).{8,20}$";

        if(StringUtils.isBlank(newPassword) || !newPassword.matches(passwordPatt)){
            map.put("newPasswordError","密码包含 数字,英文,字符中的两种以上，长度8-20");
            return map;
        }

        if(!newPassword.equals(confirmPassword)){
            map.put("confirmPasswordError","新密码与确认密码输入不一致");
            return map;
        }

        // 4、旧密码确认
        oldPassword = CommunityUtil.md5(getString(oldPassword,user.getSalt()));
        if(!oldPassword.equals(user.getPassword())){
            map.put("oldPasswordError","旧密码输入错误");
            return map;
        }
        // 5、密码修改

        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        newPassword = getString(newPassword,user.getSalt());
        newPassword = CommunityUtil.md5(newPassword);
        // 修改salt及password
        userMapper.updateSalt(user.getId(),user.getSalt());
        userMapper.updatePassword(user.getId(),newPassword);
        return map;
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
}
