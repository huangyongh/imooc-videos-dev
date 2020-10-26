package com.imooc.controller.intercepto;

import com.imooc.utils.JsonUtils;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MiniInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION = "user-redis-session";
    //拦截请求判断在Contorller之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

         /*
        返回false,请求被拦截
        返回true，请求被执行
       */
        String userId = request.getHeader("userId");
        String userToken = request.getHeader("userToken");

        System.out.println("userId:" + userId);

        System.out.println("userToken:" + userToken);

        if(StringUtils.isNotBlank(userId) && StringUtils.isNoneBlank(userToken) ){

            String uniqueToken = redisOperator.get(USER_REDIS_SESSION + ":" + userId);

            System.out.println("uniqueToken" + uniqueToken);

            if(StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)){
                returnErrorResponse(response,new IMoocJSONResult().errorTokenMsg("请登录1··"));
                System.out.println("请登录1····");
                return false;
            }else {
                if (!uniqueToken.equals(userToken)) {
                    System.out.println("账号被挤出");
                    returnErrorResponse(response,new IMoocJSONResult().errorTokenMsg("账号被挤出"));
                    return false;
                }
            }
        }else{
            System.out.println("请登录2····");
            returnErrorResponse(response,new IMoocJSONResult().errorTokenMsg("请登录2··"));
            return false;
        }
       return true;
    }


    public void returnErrorResponse(HttpServletResponse response , IMoocJSONResult result)
    throws IOException ,UnsupportedEncodingException {

        OutputStream outputStream = null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            outputStream = response.getOutputStream();
            outputStream.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            outputStream.flush();
        }finally {
            if(outputStream != null){
                outputStream.close();
            }
        }

    }
    //请求Contorller之后渲染之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
    //请求contorller之后，渲染之后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
