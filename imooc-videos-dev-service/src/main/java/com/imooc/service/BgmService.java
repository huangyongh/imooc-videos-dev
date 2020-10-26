package com.imooc.service;

import com.imooc.pojo.Bgm;

import java.util.List;

public interface BgmService {
    //查询背景音乐列表
    public List<Bgm> queryBgmList();

    public Bgm queryBgmById(String bgmId);
}
