package com.damon.dao;


import com.damon.entity.Admin;
import tk.mybatis.mapper.common.Mapper;

public interface AdminMapper extends Mapper<Admin> {
    public Admin queryByUsername(String name);

}