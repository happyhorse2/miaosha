package com.rong.miaosha.access;

import com.rong.miaosha.model.MiaoshaUser;

//用户信息上下文
public class UserContext {
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();
    public static void setUser(MiaoshaUser user) {
        userHolder.set(user);
    }

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }
}