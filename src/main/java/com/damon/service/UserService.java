package com.damon.service;

import com.damon.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public interface UserService {
    HashMap<String, Object> queryUserPage(Integer page, Integer rows);

    public String add(User u);

    public void edit(User u);

    public void del(User u);

    public void uploadUserCover(MultipartFile headImg, String id, HttpServletRequest request);

    public void uploadUserCoverAliyun(MultipartFile headImg, String id, HttpServletRequest request);


}
