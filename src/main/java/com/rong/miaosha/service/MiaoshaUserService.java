package com.rong.miaosha.service;

import com.rong.miaosha.dao.MiaoshaUserDao;
import com.rong.miaosha.model.MiaoshaUser;
import com.rong.miaosha.redis.MiaoshaUserKey;
import com.rong.miaosha.redis.RedisService;
import com.rong.miaosha.util.MD5Util;
import com.rong.miaosha.util.UuidUtil;
import com.rong.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Service
public class MiaoshaUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    public Boolean login(HttpServletResponse response, LoginVo loginVo){
        if(loginVo==null){
            return false;
        }
        Long mobile = Long.parseLong(loginVo.getMobile());
        MiaoshaUser user = getById(mobile);
        if(user == null){
            return false;
        }
        String formPassWord = loginVo.getPassword();
        System.out.println(loginVo.getPassword()+"        "+loginVo.getMobile());
        String calPassWord = MD5Util.formPassWordToDbPassWord(formPassWord);
        System.out.println(user.getPassword()+"   "+calPassWord);
        if (!user.getPassword().equals(calPassWord)){
            return false;
        }
        String uuid = UuidUtil.uuid();
        addCookies(response, uuid, user);
        return true;
    }

    public void addCookies(HttpServletResponse response, String token, MiaoshaUser user){
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.TOKEN_EXPIRE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public MiaoshaUser getById(Long mobile){
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, ""+mobile, MiaoshaUser.class);
        if (user!=null) {
            return user;
        }
        user = miaoshaUserDao.getById(mobile);
        if (user!=null){
            redisService.set(MiaoshaUserKey.getById, ""+mobile, user);
        }
        return user;
    }

    // http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        MiaoshaUser user = getById(id);
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoshaUserKey.getById, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }
}
