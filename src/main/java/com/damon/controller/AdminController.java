package com.damon.controller;

import com.damon.entity.Admin;
import com.damon.service.AdminService;
import com.damon.util.ImageCodeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

@RequestMapping("admin")
@Controller
public class AdminController {

    @Resource
    AdminService adminService;


    //管理员登录

    @RequestMapping("getImageCode")
    public void getImageCode(HttpSession session, HttpServletResponse response) {
        //1.获取随机字符
        String randomCode = ImageCodeUtil.getSecurityCode();
        System.out.println(randomCode);
        //2. 存储随机字符
        session.setAttribute("imageCode", randomCode);
        //3. 生成验证码图片
        BufferedImage image = ImageCodeUtil.createImage(randomCode);
        //4. 将图片上传
        try {
            ImageIO.write(image, "png", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping("login")
    public HashMap<String, Object> login(Admin admin, String code) {
        return adminService.login(admin, code);
    }

    @RequestMapping("exit")
    public String exit(HttpSession session) {
        session.removeAttribute("admin");
        return "redirect:/login/login.jsp";
    }

    //管理员管理

    @ResponseBody
    @RequestMapping("queryAdminPage")
    public HashMap<String, Object> queryAdminPage(Integer page, Integer rows) {
        return adminService.queryAdminPage(page, rows);
    }

    @ResponseBody
    @RequestMapping("edit")
    public String edit(Admin admin, String oper) {
        String id = null;
        if (oper.equals("add")) {
            id = adminService.add(admin);
            System.out.println(admin);
        }
        if (oper.equals("edit")) {
            adminService.edit(admin);
        }
        if (oper.equals("del")) {
            adminService.del(admin);
        }
        return id;
    }

}
