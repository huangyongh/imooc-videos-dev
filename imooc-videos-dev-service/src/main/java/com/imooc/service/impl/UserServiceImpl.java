package com.imooc.service.impl;

import com.imooc.mapper.UsersFansMapper;
import com.imooc.mapper.UsersLikeVideosMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.mapper.UsersReportMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersFans;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.pojo.UsersReport;
import com.imooc.pojo.Vo.UsersVO;
import com.imooc.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;
    @Autowired
    private Sid sid;
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users users = new Users();
        users.setUsername(username);
        Users result = usersMapper.selectOne(users);
        return result == null ? false : true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saverUser(Users user) {
        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result = usersMapper.selectOneByExample(userExample);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updatUserInfo(Users users) {
        Example userExampe = new Example(Users.class);
        Criteria criteria = userExampe.createCriteria();
        criteria.andEqualTo("id", users.getId());
        usersMapper.updateByExampleSelective(users,userExampe);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserInfo(String userId) {
        Example userExampe = new Example(Users.class);
        Criteria criteria = userExampe.createCriteria();
        criteria.andEqualTo("id", userId);
        Users users = usersMapper.selectOneByExample(userExampe);
        return users;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isUserLikeVideo(String userId, String videoId) {

        if(StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)){
            return false;
        }
        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);

        criteria.andEqualTo("videoId",videoId);
        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);

        if(list != null && list.size() > 0){
            return true;
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUserFanRelation(String userId, String fanId) {
        String fId = sid.nextShort();
        UsersFans usersFans = new UsersFans();
        usersFans.setId(fId);
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.insert(usersFans);
        usersMapper.addFansCount(userId);
        usersMapper.addFollersCount(fanId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserFanRelation(String userId, String fanId) {
        System.out.println(userId + fanId);
       Example example = new Example(UsersFans.class);
       Criteria criteria = example.createCriteria();
       criteria.andEqualTo("userId",userId);
       criteria.andEqualTo("fanId",fanId);
       usersFansMapper.deleteByExample(example);
       usersMapper.reduceFansCount(userId);
       usersMapper.reduceFollersCount(fanId);

    }

    @Override
    public boolean queryIfFollow(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        List<UsersFans> list = usersFansMapper.selectByExample(example);

        if (list != null && !list.isEmpty() && list.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void reportUser(UsersReport userReport) {

        String urId = sid.nextShort();
        userReport.setId(urId);
        userReport.setCreateDate(new Date());
        usersReportMapper.insert(userReport);

    }


}
