package com.rong.miaosha.vo;

import com.rong.miaosha.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwod) {
        this.password = password;
    }

    @Length(min=2, max=35, message="length is not right")
    private String password;

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
