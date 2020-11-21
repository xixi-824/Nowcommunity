package com.nowcoder.nowcommunity.Controlller;

import com.nowcoder.nowcommunity.service.AlphaService;
import com.nowcoder.nowcommunity.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author lei
 * @date 2020/7/21 20:58
 */

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping(path = "/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot...";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        // 获取请求数据
        System.out.println(request.getMethod());  //获取请求类型
        System.out.println(request.getServletPath());  //获取请求路径
        Enumeration<String> headerNames = request.getHeaderNames();  // 获取请求头数据
        while (headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+"："+value);
        }
        System.out.println(request.getParameter("code"));

        // 返回相应数据
        response.setContentType("text/html;charset=utf-8");
        try (PrintWriter writer = response.getWriter())
        {
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // /students?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current",required = false,defaultValue = "1")int current,
            @RequestParam(name = "limit",required = false,defaultValue = "10")int limit
    ){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /students/123
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    // POST请求
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 响应HTML数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",30);
        // templates模板引擎默认就是html文件格式
        // view.html
        mav.setViewName("/demo/view");

        return mav;
    }

    @RequestMapping(path = "/teacher1",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","华南理工大学");
        model.addAttribute("age",86);
        return "/demo/view";
    }

    // 响应JSON数据(异步请求)
    // Java对象 -> JSON字符串 -> JS对象
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);
        return emp;
    }

    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);

        Map<String,Object> emp1 = new HashMap<>();
        emp1.put("name","李四");
        emp1.put("age",24);
        emp1.put("salary",9000.00);

        Map<String,Object> emp2 = new HashMap<>();
        emp2.put("name","王五");
        emp2.put("age",25);
        emp2.put("salary",10000.00);

        list.add(emp);
        list.add(emp1);
        list.add(emp2);
        return list;
    }

    // cookie示例
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        // 创建cookie对象
        // cookie对象仅有有参构造器(String name,String value)
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie生效的范围
        // cookie只有在访问http://localhost:8088/community/aplha及其子路径时
        // 才会将该cookie通过请求头的方式传送至服务端
        cookie.setPath("/community/alpha");

        // 默认Cookie生命周期：浏览器关闭之后它会被自动删除，也就是说它仅在会话期内有效，此时cookie对象保存在内存中。
        // 通过设置cookie的声明周期，当前cookie就会被保存至硬盘当中，并不随浏览器会话对象销毁而自动消失
        cookie.setMaxAge(60 * 10);  //单位：s
        // 将cookie存入响应头，发送至客户端
        response.addCookie(cookie);

        return "set cookie";
    }

    // cookie示例
    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(@CookieValue(value = "code")String code){
        System.out.println(code);
        return "get cookie" + code;
    }

    // session示例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        // session存储键值对的形式  (String name,Object value)
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }
}
