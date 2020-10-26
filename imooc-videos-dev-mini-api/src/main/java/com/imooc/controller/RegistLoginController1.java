package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.Vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@Api(value="用户注册登录的接口", tags= {"注册和登录的controller"})
public class RegistLoginController1 extends BasicController{
	
	@Autowired
	private UserService userService;
	
	@ApiOperation(value="用户注册", notes="用户注册的接口")
	@PostMapping("/regist")
	public IMoocJSONResult regist(@RequestBody Users user) throws Exception {
		System.out.println(user.getPassword());
		System.out.println(user.getUsername());
		
		// 1. 判断用户名和密码必须不为空
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			return IMoocJSONResult.errorMsg("用户名和密码不能为空");
		}
		
		// 2. 判断用户名是否存在
		boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
		System.out.println(usernameIsExist);
		
		// 3. 保存用户，注册信息
		if (!usernameIsExist) {
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setFansCounts(0);
			user.setReceiveLikeCounts(0);
			user.setFollowCounts(0);
			userService.saverUser(user);
		} else {
			return IMoocJSONResult.errorMsg("用户名已经存在，请换一个再试");
		}
		user.setPassword("");
		UsersVO usersVO = setUserRedisSessionToken(user);
		return IMoocJSONResult.ok(usersVO);
	}

	@ApiOperation(value = "用户登录",notes = "用户登录接口")
	@PostMapping(value = "/login")
	public IMoocJSONResult login(@RequestBody Users user) throws Exception {
		String username = user.getUsername();
		String password = user.getPassword();

		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
			return IMoocJSONResult.ok("用户名或密码不能为空~~");
		}

		Users usersResult =userService.queryUserForLogin(username,MD5Utils.getMD5Str(password));

		if(usersResult != null){
			usersResult.setPassword("");
			UsersVO usersVO = setUserRedisSessionToken(usersResult);
			return IMoocJSONResult.ok(usersVO);
		}else{
			return IMoocJSONResult.errorMsg("用户名或密码不正确");
		}
	}

	@ApiOperation(value = "用户注销",notes = "用户注销接口")
	@ApiImplicitParam(name="userId",value = "用户Id" ,required = true,dataType = "String",paramType = "query")
	@PostMapping(value = "/logout")
	public IMoocJSONResult logout(String userId) throws Exception {
		redis.del(USER_REDIS_SESSION + ":" + userId);
		return IMoocJSONResult.ok("");
	}


	public UsersVO setUserRedisSessionToken(Users userModel){
		String uniqueToken = UUID.randomUUID().toString();
		redis.set(USER_REDIS_SESSION + ":" + userModel.getId(),uniqueToken,60 * 30 * 10);

		UsersVO usersVO = new UsersVO();
		BeanUtils.copyProperties(userModel,usersVO);
		usersVO.setUserToken(uniqueToken);
		return usersVO;
	}
}
