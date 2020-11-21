package com.nowcoder.nowcommunity.service;

import com.nowcoder.nowcommunity.dao.UserMapper;
import com.nowcoder.nowcommunity.entity.User;
import com.nowcoder.nowcommunity.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户名合法性检查
     * @param username
     * @return
     */
    public String usernameCheck(String username){
        // 1、当前用户名表达式合法性
        // 限8个字符，支持中英文、数字、减号或下划线
        String usernamePatt = "^[\\\u4e00-\\\u9fa5_a-zA-Z0-9-]{1,8}$";
        if(StringUtils.isBlank(username) || !username.matches(usernamePatt)){
            return CommunityUtil.getRegisterCheck(400,"usernameMsg","用户限8个字符，仅支持中英文、数字、减号或下划线");
        }

        // 2、当前用户名是否已被注册
        User user = userMapper.selectByName(username);
        if(user != null){
            return CommunityUtil.getRegisterCheck(400,"usernameMsg","当前用户名已被注册，请重新设置用户名");
        }

        return CommunityUtil.getRegisterCheck(200,"usernameSuccessMsg","当前用户名可用");
    }

    /**
     * 用户密码合法性及强度检查
     * @param password
     * @return
     */
    public String passwordCheck(String password){
        // 1、创建正则表达式对象
        // 密码包含 数字,英文,字符中的两种以上，长度8-20
        String passwordPatt = "^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?!([^(0-9a-zA-Z)])+$).{8,20}$";
        if(StringUtils.isBlank(password) || !password.matches(passwordPatt)){
            return CommunityUtil.getRegisterCheck(400,"passwordMsg","密码包含 数字,英文,字符中的两种以上，长度8-20");
        }

        // 2、密码强度检查
        String passwordSuccessMsg = CommunityUtil.checkPassword(password);
        return CommunityUtil.getRegisterCheck(200,"passwordSuccessMsg",passwordSuccessMsg);
    }

    /**
     * 密码与确认密码的一致性检查
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return
     */
    public String confirmPasswordCheck(String password,String confirmPassword){
        if(StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword) || !password.equals(confirmPassword)){
            return CommunityUtil.getRegisterCheck(400,"confirmPasswordMsg","两次输入密码不一致");
        }

        // 2、密码强度检查
        return CommunityUtil.getRegisterCheck(200,"confirmPasswordSuccessMsg","两次输入密码一致");
    }

    /**
     * 邮箱合法性检查
     * @param email
     * @return
     */
    public String emailCheck(String email){
        // 1、创建正则表达式对象
        // 限8个字符，支持中英文、数字、减号或下划线
        String emailPatt = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if(StringUtils.isBlank(email) || !email.matches(emailPatt)){
            return CommunityUtil.getRegisterCheck(400,"emailMsg","邮箱名称允许汉字、字母、数字，邮箱域名只允许英文域名");
        }

        // 2、当前用户名是否已被注册
        User user = userMapper.selectByEmail(email);
        if(user != null){
            return CommunityUtil.getRegisterCheck(400,"emailMsg","当前邮箱已被注册");
        }

        return CommunityUtil.getRegisterCheck(200,"emailSuccessMsg","当前邮箱可用");
    }

}
