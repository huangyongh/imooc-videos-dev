package com.imooc.controller;

import com.imooc.enums.VideoStatusEnum;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Comments;
import com.imooc.pojo.Users;
import com.imooc.pojo.Videos;
import com.imooc.service.BgmService;
import com.imooc.service.VideoService;
import com.imooc.utils.FetchVideoCover;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MergeVideoMp3;
import com.imooc.utils.PagedResult;
import io.swagger.annotations.*;
import io.swagger.models.auth.In;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@Api(value = "视频相关业务" ,tags = {"视频相关业务的Controller"})
@RequestMapping(value = "/video")
public class VideoController extends BasicController{
    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "用户上传视频",tags = "用户上传视频接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String", paramType ="form"),
            @ApiImplicitParam(name = "bgmId",value = "背景音乐id",required = false,dataType = "String", paramType ="form"),
            @ApiImplicitParam(name = "videoSeconds",value = "视频播放时长",required = true,dataType = "String", paramType ="form"),
            @ApiImplicitParam(name = "videoWidth",value = "视频宽度",required = true,dataType = "int", paramType ="form"),
            @ApiImplicitParam(name = "videoHeight",value = "视频高度",required = true,dataType = "int", paramType ="form"),
            @ApiImplicitParam(name = "desc",value = "视频描述",required = false,dataType = "String", paramType ="form")
    })

    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public IMoocJSONResult upload(String userId,String bgmId,double duration,Integer tmpwidth,Integer tmpheight,
                                  String desc ,@ApiParam(value="短视频", required=true) MultipartFile files) throws Exception{

        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        //String fileSpace = "D:/imooc_videos_dev";
        String uploadPathDB = "/" + userId + "/video";
        String CoverPathDB = "/" + userId + "/video";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        String finalPath = "";
        try {
            if (files != null ) {
                String fileName = files.getOriginalFilename();//文件名

                String filenamePrefix = fileName.split("\\.")[0];

                if (StringUtils.isNoneBlank(fileName)) {
                    finalPath = File_Space + uploadPathDB + "/" + fileName;
                    uploadPathDB += ("/" + fileName);
                    CoverPathDB = CoverPathDB + "/" + filenamePrefix + ".jpg";

                    File outFile = new File(finalPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    files.transferTo(outFile);
                    inputStream = files.getInputStream();
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
        if(StringUtils.isNoneBlank(bgmId)){
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String mp3InputPath = File_Space + bgm.getPath();
            MergeVideoMp3 mergeVideoMp3 = new MergeVideoMp3(FFMEGE_EXE);
            String videoInputPath = finalPath;

            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            finalPath = File_Space + uploadPathDB;
            mergeVideoMp3.convertor(videoInputPath,mp3InputPath,duration,finalPath);
        }

        FetchVideoCover videoCover = new FetchVideoCover(FFMEGE_EXE);
        videoCover.getCover(finalPath,File_Space + CoverPathDB);

        Videos videos = new Videos();
        videos.setAudioId(bgmId);
        videos.setUserId(userId);
        videos.setVideoSeconds((float)duration);
        videos.setVideoHeight(tmpheight);
        videos.setVideoWidth(tmpwidth);
        videos.setVideoDesc(desc);
        videos.setVideoPath(uploadPathDB);
        videos.setCoverPath(CoverPathDB);
        videos.setStatus(VideoStatusEnum.SUCCESS.value);
        videos.setCreateTime(new Date());
        String ViodeId = videoService.saveVideo(videos);
        return IMoocJSONResult.ok(ViodeId);

    }


    @ApiOperation(value = "上传封面",tags = "用户上传封面接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId",value = "视频id",required = true,dataType = "String", paramType ="form"),
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String", paramType ="form")
    })

    @PostMapping(value = "/uploadCover",headers = "content-type=multipart/form-data")
    public IMoocJSONResult uploadCover(String videoId,String userId
                                       ,@ApiParam(value="视频封面", required=true) MultipartFile file) throws Exception{

        if(StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id和视频ID不能为空...");
        }
        //String fileSpace = "D:/imooc_videos_dev";
        String uploadPathDB = "/" + userId + "/video";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        String finalCoverPath = "";
        try {
            if (file != null ) {
                String fileName = file.getOriginalFilename();//文件名
                if (StringUtils.isNoneBlank(fileName)) {
                    finalCoverPath = File_Space + uploadPathDB + "/" + fileName;
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalCoverPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
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

        videoService.updateVideo(videoId,uploadPathDB);
        return IMoocJSONResult.ok();

    }


    @PostMapping(value = "/showAll")
    public IMoocJSONResult showAll(@RequestBody Videos video , Integer isSaveRecord,
                                   Integer page) throws Exception{
        System.out.println("UserId:" + video.getUserId());

        if(page == null){
            page = 1;
        }
        PagedResult result = videoService.getAllVideos(video,isSaveRecord,page,PAGE_SIZE);
        return IMoocJSONResult.ok(result);
    }

    /**
     * @Description: 我关注的人发的视频
     */
    @PostMapping("/showMyFollow")
    public IMoocJSONResult showMyFollow(String userId, Integer page) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        int pageSize = 6;

        PagedResult videosList = videoService.queryMyFollowVideos(userId, page, pageSize);

        return IMoocJSONResult.ok(videosList);
    }

    /**
     * @Description: 我收藏(点赞)过的视频列表
     */
    @PostMapping("/showMyLike")
    public IMoocJSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 6;
        }

        PagedResult videosList = videoService.queryMyLikeVideos(userId, page, pageSize);

        return IMoocJSONResult.ok(videosList);
    }


    @PostMapping(value = "/hot")
    public IMoocJSONResult hot(){
        return IMoocJSONResult.ok(videoService.getHotworld());
    }


    @PostMapping(value = "userLike")
    public IMoocJSONResult userLike(String userId, String videoId, String videoCreaterId)
    throws Exception{
        videoService.userLikeVideo(userId,videoId,videoCreaterId);
        return IMoocJSONResult.ok();
    }


    @PostMapping(value = "userUnlike")
    public IMoocJSONResult userUnlike(String userId, String videoId, String videoCreaterId)
        throws Exception{
        videoService.userUnLikeVideo(userId,videoId,videoCreaterId);
        return IMoocJSONResult.ok();
    }


    @PostMapping("/saveComment")
    public IMoocJSONResult saveComment(@RequestBody Comments comment,
                                       String fatherCommentId, String toUserId) throws Exception {

        comment.setFatherCommentId(fatherCommentId);
        comment.setToUserId(toUserId);

        videoService.saveComment(comment);
        return IMoocJSONResult.ok();
    }

    @PostMapping("/getVideoComments")
    public IMoocJSONResult getVideoComments(String videoId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(videoId)) {
            return IMoocJSONResult.ok();
        }

        // 分页查询视频列表，时间顺序倒序排序
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PagedResult list = videoService.getAllComments(videoId, page, pageSize);

        return IMoocJSONResult.ok(list);
    }





}
