package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.*;
import com.imooc.pojo.Comments;
import com.imooc.pojo.SearchRecords;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.pojo.Videos;
import com.imooc.pojo.Vo.CommentsVO;
import com.imooc.pojo.Vo.VideosVo;
import com.imooc.service.VideoService;
import com.imooc.utils.PagedResult;
import com.imooc.utils.TimeAgoUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private Sid sid;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired

    private CommentsMapperCustom commentsMapperCustom;
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String saveVideo(Videos videos) {
        String id = sid.nextShort();
        videos.setId(id);
        videosMapper.insertSelective(videos);
        return id;
    }

    @Override
    public void updateVideo(String videoId, String coverPath) {

        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(videos);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PagedResult getAllVideos(Videos video,Integer isScavecord,Integer page, Integer pageSize) {

        String desc = video.getVideoDesc();
        //保存热搜词
        String userId = video.getUserId();
        System.out.println("UsrId2:" + video.getUserId());
        if(isScavecord != null && isScavecord == 1){
            SearchRecords searchRecords = new SearchRecords();
            String serachId = sid.nextShort();
            searchRecords.setId(serachId);
            searchRecords.setContent(desc);
            searchRecordsMapper.insert(searchRecords);
        }
        PageHelper.startPage(page,pageSize);
        List<VideosVo> list =  videosMapperCustom.queryAllVideos(desc,userId);
        PageInfo<VideosVo> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);
        List<VideosVo> list = videosMapperCustom.queryMyLikeVideos(userId);

        PageInfo<VideosVo> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVo> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVo> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> getHotworld() {

        return searchRecordsMapper.getHotworlds();
    }

    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {

        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        usersLikeVideosMapper.insert(ulv);

        videosMapperCustom.addVideoLikeCount(videoId);

        usersMapper.addReceiveLikeCount(userId);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {
        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        usersLikeVideosMapper.deleteByExample(example);
        videosMapperCustom.reduceVideoLikeCount(videoId);
        usersMapper.reduceiveLikeCount(userId);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comment) {
        String id = sid.nextShort();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentsMapper.insert(comment);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);

        for (CommentsVO c : list) {
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }

        PageInfo<CommentsVO> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());

        return grid;
    }
}
