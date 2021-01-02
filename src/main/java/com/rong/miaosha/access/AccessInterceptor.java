package com.rong.miaosha.access;

import com.rong.miaosha.model.MiaoshaUser;
import com.rong.miaosha.redis.AccessKey;
import com.rong.miaosha.redis.RedisService;
import com.rong.miaosha.result.CodeMessage;
import com.rong.miaosha.result.Result;
import com.rong.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import com.alibaba.fastjson.JSON;


@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    //处理拦截器输出
    private void render(HttpServletResponse response, CodeMessage cm) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }


    //从参数或cookies中获取cookieToken
    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return userService.getByToken(response, token);
    }


    //获取HTTPrequest中的token值
    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    //预处理请求，cookies校验
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if(handler instanceof HandlerMethod) {
            MiaoshaUser user = getUser(request, response);
            //设置多线程请求user
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod)handler;
            //访问控制
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin) {
                if(user == null){
                    render(response, CodeMessage.SESSION_ERROR);
                    return false;
                }
                //访问控制key，限制一个用户的访问次数
                key += "_" + user.getId();
            }else {
                //do nothing
            }
            //这里统计次数并不是线程安全的
            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if(count  == null) {
                redisService.set(ak, key, 1);
            }else if(count < maxCount) {
                redisService.incr(ak, key);
            }else {
                render(response, CodeMessage.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }
}