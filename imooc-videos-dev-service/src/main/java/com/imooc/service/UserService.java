package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;

public interface UserService {

    public boolean queryUsernameIsExist(String username);


    public void saverUser(Users user);

    public Users queryUserForLogin(String username, String password);

   // 用户想改信息
    public void updatUserInfo(Users users);

    public Users queryUserInfo(String userId);

    public boolean isUserLikeVideo(String userId,String videoId);

    /**
     * @Description: 增加用户和粉丝的关系
     */
    public void saveUserFanRelation(String userId, String fanId);

    /**
     * @Description: 删除用户和粉丝的关系
     */
    public void deleteUserFanRelation(String userId, String fanId);

    /**
     * @Description: 查询用户是否关注
     */
    public boolean queryIfFollow(String userId, String fanId);

    /**
     * @Description: 举报用户
     */
    public void reportUser(UsersReport userReport);




}
