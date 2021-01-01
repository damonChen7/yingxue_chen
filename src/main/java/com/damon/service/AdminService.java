package com.damon.service;

import com.damon.entity.Admin;

import java.util.HashMap;

public interface AdminService {
    //登录
    public HashMap<String, Object> login(Admin admin, String code);

    //管理
    //展示所有管理员
    public HashMap<String, Object> queryAdminPage(Integer page, Integer rows);

    //编辑管理员信息
    public String add(Admin admin);

    public void edit(Admin admin);

    public void del(Admin admin);

}
