package com.imooc.mapper;

import com.imooc.pojo.Videos;
import com.imooc.pojo.Vo.VideosVo;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {
    public List<VideosVo> queryAllVideos(@Param("videoDesc") String videoDesc,@Param("userId") String userId);


    /**
     * @Description: 查询关注的视频
     */
    public List<VideosVo> queryMyFollowVideos(String userId);

    /**
     * @Description: 查询点赞视频
     */
    public List<VideosVo> queryMyLikeVideos(@Param("userId") String userId);


    public void addVideoLikeCount(String videoId);


    public void reduceVideoLikeCount(String videoId);





}
