package com.nowcoder.nowcommunity.Controlller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.nowcommunity.entity.User;
import com.nowcoder.nowcommunity.service.UserService;
import com.nowcoder.nowcommunity.util.CommunityConstant;
import com.nowcoder.nowcommunity.util.CommunityUtil;
import com.nowcoder.nowcommunity.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path="/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    /**
     * 登录验证
     * service层验证用户名以验证码是否有效(验证码凭证在session中保存)
     * 登录成功跳转至首页，并将登陆凭证ticket以cookie形式发送至客户端本地用于免登录操作
     * 登录失败，请求转发至登录界面，显示错误信息
     * @param username
     * @param password
     * @param code
     * @param rememberMe
     * @param model
     * @param session
     * @param response
     * @return
     */
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String loginCheck(String username,String password,String code,boolean rememberMe,
                             Model model,HttpSession session,HttpServletResponse response,
                             @CookieValue("kaptchaOwner") String kaptchaOwner){
        // 1、session中获取验证码凭证
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;

        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String)redisTemplate.opsForValue().get(redisKey);
        }

        if(!userService.kaptchaCheck(code,kaptcha)){
            model.addAttribute("codeMsg","验证码输入不正确");
            return "/site/login";
        }

        // 2、账号密码的合法性
        // 用户登录凭证保存时间
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> login = userService.login(username, password,expiredSeconds);
        if(login.get("ticket") != null){
            // 登录验证成功
            // 登录凭证存至cookie中
            String ticket = (String) login.get("ticket");
            // cookie有效周期及范围设置
            Cookie cookie = new Cookie("ticket", ticket);
            // cookie的有效路径设置
            cookie.setPath(contextPath);
            // cookie的最大生命周期设置
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            // 登录成功，请求重定向至首页
            // 将第一次登录请求的用户，密码等数据随request对象一同销毁，防止敏感信息泄露
            return "redirect:/index";
        }else{
            // 登录失败
            // 请求转发至登录页面，利用request域对象保存第一次请求数据的特性
            // 登录页面显示之前输入的登录信息，登录错误项提示信息
            model.addAttribute("usernameMsg",login.get("usernameMsg"));
            model.addAttribute("passwordMsg",login.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket){
        // 修改用户登录凭证状态为1
        userService.logout(ticket);
        // 重定向至登录页面，销毁第一次请求携带的参数
        return "redirect:/login";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user,String confirmPassword){
        Map<String, Object> registMap = userService.regist(user,confirmPassword);
        if(registMap == null || registMap.isEmpty()){
            // 注册成功
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一份激活邮件");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            // 注册失败，请求转发至注册页面
            model.addAttribute("usernameMsg", registMap.get("usernameMsg"));
            model.addAttribute("passwordMsg", registMap.get("passwordMsg"));
            model.addAttribute("emailMsg", registMap.get("emailMsg"));
            model.addAttribute("confirmPasswordMsg", registMap.get("confirmPasswordMsg"));
            // 请求转发至注册页面
            // 因为此时用户需要复用之前的注册信息，方便只需修改异常项
            // 请求重定向会导致第一次请求的信息全部丢失
            return "/site/register";
        }
    }

    /**
     * 激活账号操作
     * @param model
     * @param userId 激活的用户id
     * @param code 激活码
     * @return
     */
    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        int activation = userService.activation(userId, code);
        if(activation == ACTIVATION_SUCCESS){
            // 激活成功
            model.addAttribute("msg","激活成功，您的账号已经可以正常使用");
            model.addAttribute("target","/login");
        }else if(activation == ACTIVATION_REPEAT){
            // 重复激活
            model.addAttribute("msg","无效操作，您的账号已经激活过了");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激活失败，激活码不匹配");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    /**
     * 访问验证码信息
     * 1、实现当前登录客户端与验证码的一一匹配  2、验证码等敏感信息存储于服务端
     * 版本1：验证码信息属于敏感数据，不能存在cookie当中，应该存放在session当中
     * 版本2：验证码信息存储于服务端的redis数据中，通过向客户端发送包含随机字符串的cookie
     *        用户提交时，验证当前cookie与提交验证码是否匹配来核对当前客户端登录请求
     * @param response
     */
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session对象中，方便下次登录验证时，校验验证码使用
        // 每次刷新验证码就会覆盖之前session中保存的验证码信息
//        session.setAttribute("kaptcha",text);

        // 匹配当前登录用户的凭证
        // 1、点击登录时，该cookie会连同表单数据提交至后端
        // 检查该随机字符串与验证码两者均匹配就满足验证码输入正确
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        // 2、将验证码信息存入redis数据库中，设置string类型数据过期时间
        String redisKey = RedisUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            // 将图片写入当前客户端页面
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            // 当前异常无法处理，进行日志记录
            logger.error("相应验证码失败："+e.getMessage());
        }
    }
}
