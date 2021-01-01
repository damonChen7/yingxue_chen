package com.damon.controller;

import com.damon.entity.Video;
import com.damon.service.VideoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@RequestMapping("video")
public class VideoController {
    @Resource
    VideoService videoService;

    @ResponseBody
    @RequestMapping("queryVideoPage")
    public HashMap<String, Object> queryVideoPage(Integer page, Integer rows) {
        return videoService.queryVideoPage(page, rows);
    }

    @ResponseBody
    @RequestMapping("edit")
    public String edit(Video video, String oper) {
        if (oper.equals("add")) {
            String id = videoService.add(video);
            return id;
        }

        if (oper.equals("edit")) {
            videoService.edit(video);
        }

        if (oper.equals("del")) {
            videoService.del(video);
        }

        return "";
    }


    @ResponseBody
    @RequestMapping("uploadVideo")
    public void uploadVideo(MultipartFile videoPath, String id, HttpServletRequest request) {
        videoService.uploadVideoAliyun(videoPath, id, request);
    }

    @ResponseBody
    @RequestMapping("searchVideo")
    public void searchVideo(String content) {
        //
    }
}
