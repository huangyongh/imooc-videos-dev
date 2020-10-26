package com.imooc.service;

import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

import java.util.List;

public interface VideoService {
    public String saveVideo(Videos videos);


    public void updateVideo(String videoId,String coverPath);

    public PagedResult getAllVideos(Videos video, Integer isSavecord, Integer page, Integer pageSize);

    /**
     * @Description: 查询我喜欢的视频列表
     */
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

    /**
     * @Description: 查询我关注的人的视频列表
     */
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);



    public List<String> getHotworld();


    public void userLikeVideo(String userId,String videoId,String videoCreaterId);


    public void userUnLikeVideo(String userId,String videoId,String videoCreaterId);

    /**
     * @Description: 用户留言
     */
    public void saveComment(Comments comment);

    /**
     * @Description: 留言分页
     */
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}
