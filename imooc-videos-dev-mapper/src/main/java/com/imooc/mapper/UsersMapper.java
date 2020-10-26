package com.imooc.mapper;

import com.imooc.pojo.Users;
import com.imooc.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {

    public void addReceiveLikeCount(String userId);

    public void reduceiveLikeCount(String userId);


    /**
     * @Description: 增加粉丝数
     */
    public void addFansCount(String userId);

    /**
     * @Description: 增加关注数
     */
    public void addFollersCount(String userId);

    /**
     * @Description: 减少粉丝数
     */
    public void reduceFansCount(String userId);

    /**
     * @Description: 减少关注数
     */
    public void reduceFollersCount(String userId);



}