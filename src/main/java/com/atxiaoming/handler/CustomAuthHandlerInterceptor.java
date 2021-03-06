package com.atxiaoming.handler;

import com.atxiaoming.entity.User;
import com.atxiaoming.mapper.UserMapper;
import com.atxiaoming.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class CustomAuthHandlerInterceptor  implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //获取请求头信息
        String cusToken = request.getHeader("token");

        //如果U-TOKEN为空则证明没有登录
        if (StringUtils.isEmpty(cusToken)) {
            writeJosn(response, "2用户未登录",52000);
            //拦截
            return false;
        }

        //token（id）不为0，如果redis的velue为空，则账号密码过期
        Object loginUser = redisTemplate.opsForValue().get(cusToken);
        if (StringUtils.isEmpty(loginUser)) {
            //过期
            writeJosn(response, "2登录已过期",52001);
            return false;
        }
        //第一次或者第n次请求 刷新保存时间
        redisTemplate.opsForValue().set(cusToken, loginUser, 60 * 60 * 24 * 5, TimeUnit.SECONDS);
        //false 拦截
        return true;
    }

    /**
     * 响应给前台的json对象
     * @param response
     * @param msg
     */
    private void writeJosn(HttpServletResponse response, String msg,Integer code) {
        PrintWriter writer = null;
        //如果token为空则证明没有登录
        try {
            //没有登录 就告诉前台 应该跳到登录页面 （前台用后置拦截器接受：在每次请求后响应之前拦截，就是有res以后执行成功函数之前）
            //告诉前台我要传的数据格式 和字符集
            response.setContentType("text/json;charset=utf-8");
            //要一个流
            writer = response.getWriter();
            //要把什么以json格式传给前台
            writer.write("{\"success\":false,\"result\":\" " + msg + "\",\"code\":\""+ code + "\" }");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }
}
