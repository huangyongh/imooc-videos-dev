package com.imooc.controller;

import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {
    @Autowired

    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    public static final String File_Space = "D:/imooc_videos_dev";

    public static final String FFMEGE_EXE ="F:\\ffmpge\\ffmpeg-20190614-dd357d7-win64-static\\bin\\ffmpeg.exe";


    public static final Integer PAGE_SIZE = 5;

}
