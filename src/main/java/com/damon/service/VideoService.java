package com.damon.service;

import com.damon.entity.Video;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public interface VideoService {
    HashMap<String, Object> queryVideoPage(Integer page, Integer rows);

    String add(Video video);

    void edit(Video video);

    void del(Video video);

    void uploadVideo(MultipartFile videoPath, String id, HttpServletRequest request);

    void uploadVideoAliyun(MultipartFile videoPath, String id, HttpServletRequest request);

}
