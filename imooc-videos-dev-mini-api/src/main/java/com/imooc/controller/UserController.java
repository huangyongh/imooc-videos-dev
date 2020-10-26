package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;
import com.imooc.pojo.Vo.PublisherVideo;
import com.imooc.pojo.Vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@RestController
@Api(value = "用户相关业务的接口",tags = "用户相关业务的Controller")
@RequestMapping(value = "/user")
public class UserController extends BasicController {
    @Autowired
    private UserService userService;
    @ApiOperation(value = "用户上传头像",tags = "用户上传头像接口")
    @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String", paramType ="query")
    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws Exception{

        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        String fileSpace = "D:/imooc_videos_dev";
        String uploadPathDB = "/" + userId + "/face";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
       try {
           if (files != null || files.length > 0) {
               String fileName = files[0].getOriginalFilename();//文件名
               if (StringUtils.isNoneBlank(fileName)) {
                   String finalPath = fileSpace + uploadPathDB + "/" + fileName;
                   uploadPathDB += ("/" + fileName);

                   File outFile = new File(finalPath);
                   if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                       outFile.getParentFile().mkdirs();
                   }

                   fileOutputStream = new FileOutputStream(outFile);
                   inputStream = files[0].getInputStream();
                   IOUtils.copy(inputStream, fileOutputStream);
               }

           }else{
               return IMoocJSONResult.errorMsg("上传出错......");
           }
       }catch (Exception e){
           e.printStackTrace();
           return IMoocJSONResult.errorMsg("上传错误.......");
       }finally {
           if(fileOutputStream!= null){
               fileOutputStream.flush();
               fileOutputStream.close();
           }
       }

       Users users = new Users();
       users.setId(userId);
       users.setFaceImage(uploadPathDB);
       userService.updatUserInfo(users);
        return IMoocJSONResult.ok(uploadPathDB);
    }
    @ApiOperation(value = "查询用户信息",tags = "用户信息查询接口")
    @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String", paramType ="query")
    @PostMapping(value = "/query")
    public IMoocJSONResult query(String userId ,String fanId)throws Exception{
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }
        Users userInfo = userService.queryUserInfo(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userInfo,usersVO);
        usersVO.setFollow(userService.queryIfFollow(userId,fanId));
        return IMoocJSONResult.ok(usersVO);
    }

    @PostMapping(value = "/queryPublisher")
    public IMoocJSONResult queryPublisher(String loginUserId,String videoId,String publisherUserId) throws Exception{
        if(StringUtils.isBlank(publisherUserId)){
            return IMoocJSONResult.errorMsg("");
        }
        Users users = userService.queryUserInfo(publisherUserId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);

        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId,videoId);
        PublisherVideo publisherVideo = new PublisherVideo();
        publisherVideo.setPublisher(usersVO);

        publisherVideo.setUserLikeVideo(userLikeVideo);

        return IMoocJSONResult.ok(publisherVideo);
    }

    @PostMapping("/beyourfans")
    public IMoocJSONResult beyourfans(String userId, String fanId) throws Exception {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return IMoocJSONResult.errorMsg("");
        }

        userService.saveUserFanRelation(userId, fanId);

        return IMoocJSONResult.ok("关注成功...");
    }

    @PostMapping("/dontbeyourfans")
    public IMoocJSONResult dontbeyourfans(String userId, String fanId) throws Exception {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return IMoocJSONResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId, fanId);

        return IMoocJSONResult.ok("取消关注成功...");
    }

    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport) throws Exception {

        // 保存举报信息
        userService.reportUser(usersReport);

        return IMoocJSONResult.errorMsg("举报成功...有你平台变得更美好...");
    }


}
