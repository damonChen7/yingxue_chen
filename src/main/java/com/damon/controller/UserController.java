package com.damon.controller;

import com.damon.entity.User;
import com.damon.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@RequestMapping("user")
public class UserController {
    @Resource
    UserService userService;

    @ResponseBody
    @RequestMapping("queryUserPage")
    public HashMap<String, Object> queryUserPage(Integer page, Integer rows) {
        return userService.queryUserPage(page, rows);
    }

    @ResponseBody
    @RequestMapping("edit")
    public String edit(User user, String oper) {
        String id = null;
        if (oper.equals("add")) {
            id = userService.add(user);
        }
        if (oper.equals("edit")) {
            userService.edit(user);
        }
        if (oper.equals("del")) {
            userService.del(user);
        }
        return id;
    }

    @ResponseBody
    @RequestMapping("uploadUserCover")
    public void uploadUserCover(MultipartFile headImg, String id, HttpServletRequest request) {
        //上传图片
        //userService.uploadUserCover(headImg, id, request);
        //阿里云
        userService.uploadUserCoverAliyun(headImg, id, request);
    }
}
