package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value="用户注册登录的接口", tags= {"注册和登录的controller"})
public class Hello {
    @ApiOperation(value="用户注册", notes="用户注册的接口")
    @RequestMapping(value = "/hello",method = RequestMethod.POST)
    public IMoocJSONResult hello(@RequestBody Users user){
        System.out.println(user.getPassword());
        System.out.println(user.getUsername());

        // 1. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }

        return IMoocJSONResult.ok("OK");
    }
}
