package com.nowcoder.nowcommunity.Controlller;

import com.nowcoder.nowcommunity.annotation.LoginRequired;
import com.nowcoder.nowcommunity.entity.User;
import com.nowcoder.nowcommunity.service.UserService;
import com.nowcoder.nowcommunity.util.CommunityUtil;
import com.nowcoder.nowcommunity.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    /**
     * 上传图片
     * @param headerImage
     * @param request
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, HttpServletRequest request, Model model){
        if(headerImage == null || headerImage.isEmpty()){
            model.addAttribute("error","您没有选择图片");
            return "/site/setting";
        }
        // 判断异常类型

        if(request.getAttribute("error") != null){
            model.addAttribute("error","文件上传大小不超过1MB");
            return "/site/setting";
        }

        // 检查图片的后缀
        String fileName = headerImage.getOriginalFilename();
        // 检查文件格式是否合法
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 只能上传jpg、jpeg、bmp、png格式的图片
        // 文件大小不能超过1M
        if(!CommunityUtil.checkFile(fileName)){
            model.addAttribute("error","仅支持jpg、jpeg、bmp、png格式的图片!");
            return "/site/setting";
        }
        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath);
        if(dest.isDirectory()){
            dest.mkdirs();
        }
        dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！",e);
        }

        // 更新当前用户的头像的路径
        // http:localhost:8088/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    /**
     * 从服务端的存储空间读取图片，通过response获取的输出流显示在页面上
     * @param fileName
     * @param response
     */
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){

        // 图片格式合法性检查
        if(!CommunityUtil.checkFile(fileName)){
            throw new RuntimeException("文件后缀名非法");
        }

        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 1、获取服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 2、判断文件后缀是否符合要求
        // 图片格式必须为png、jpg、jpeg
        // 3、获取输入流，将服务端存储位置文件通过输出流传至页面显示
        response.setContentType("image/" + suffix);
        // 设置响应的编码方式
        response.setCharacterEncoding("utf-8");

        try(
                // 字节输出流
                ServletOutputStream outputStream = response.getOutputStream();
                InputStream inputStream = new FileInputStream(fileName);
        ){
            byte[] b = new byte[1024];
            while(inputStream.read(b) != -1){
                outputStream.write(b);
            }
        } catch (IOException e) {
            // 记录日志
            logger.error("读取头像失败："+e.getMessage());
        }
    }

    @RequestMapping(value = "/updatePassword",method = RequestMethod.POST)
    public String updatePassword(String oldPassword,String newPassword,String confirmPassword,Model model){
        Map<String, String> map = userService.updatePassword(oldPassword, newPassword, confirmPassword);
        if(map.isEmpty()){
            // 密码修改成功
            // 请求重定向至首页
            return "redirect:/index";
        }else{
            model.addAttribute("oldPasswordError",map.get("oldPasswordError"));
            model.addAttribute("newPasswordError",map.get("newPasswordError"));
            model.addAttribute("confirmPasswordError",map.get("confirmPasswordError"));
            // 请求转发至用户设置界面
            return "/site/setting";
        }
    }
}
