package com.rong.miaosha.service;

import com.rong.miaosha.dao.MiaoshaUserDao;
import com.rong.miaosha.model.MiaoshaUser;
import com.rong.miaosha.redis.MiaoshaUserKey;
import com.rong.miaosha.redis.RedisService;
import com.rong.miaosha.result.CodeMessage;
import com.rong.miaosha.util.MD5Util;
import com.rong.miaosha.util.UuidUtil;
import com.rong.miaosha.vo.LoginVo;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import com.rong.miaosha.exception.GlobalException;


@Service
public class MiaoshaUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    public Boolean login(HttpServletResponse response, LoginVo loginVo){
        if(loginVo==null){
            throw new GlobalException(CodeMessage.REQUEST_ILLEGAL);
        }
        Long mobile = Long.parseLong(loginVo.getMobile());
        MiaoshaUser user = getById(mobile);
        if(user == null){
            throw new GlobalException(CodeMessage.MOBILE_ERROR);
        }
        String formPassWord = loginVo.getPassword();
        System.out.println(loginVo.getPassword()+"        "+loginVo.getMobile());
        String calPassWord = MD5Util.formPassWordToDbPassWord(formPassWord);
        System.out.println(user.getPassword()+"   "+calPassWord);
        if (!user.getPassword().equals(calPassWord)){
            throw new GlobalException(CodeMessage.PASSWORD_ERROR);
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
            throw new GlobalException(CodeMessage.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassWordToDbPassWord(formPass));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoshaUserKey.getById, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if(user != null) {
            addCookies(response, token, user);
        }
        return user;
    }
}
