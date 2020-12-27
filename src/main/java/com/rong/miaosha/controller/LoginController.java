package com.rong.miaosha.controller;

import com.rong.miaosha.result.Result;
import com.rong.miaosha.service.MiaoshaUserService;
import com.rong.miaosha.vo.LoginVo;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("/user")
public class LoginController {

    @Autowired
    MiaoshaUserService userService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }


    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> login_easy(HttpServletResponse response, @Valid LoginVo loginVo){
        Boolean result = userService.login(response, loginVo);
        return Result.success("hello");
    }

}
