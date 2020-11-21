package com.nowcoder.nowcommunity.Controlller;

import com.nowcoder.nowcommunity.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping(path="/usernameCheck",method = RequestMethod.POST)
    public String usernameCheck(String username){
        return userInfoService.usernameCheck(username);
    }

    @RequestMapping(path="/passwordCheck",method = RequestMethod.POST)
    public String passwordCheck(String password){
        return userInfoService.passwordCheck(password);
    }

    @RequestMapping(path="/confirmPasswordCheck",method = RequestMethod.POST)
    public String confirmPasswordCheck(String password,String confirmPassword){
        return userInfoService.confirmPasswordCheck(password,confirmPassword);
    }

    @RequestMapping(path="/emailCheck",method = RequestMethod.POST)
    public String emailCheck(String email){
        return userInfoService.emailCheck(email);
    }

}
