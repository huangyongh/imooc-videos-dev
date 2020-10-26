package com.imooc.mapper;

import com.imooc.pojo.Vo.CommentsVO;
import com.imooc.utils.MyMapper;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<CommentsVO> {
    public List<CommentsVO> queryComments(String videoId);
}
