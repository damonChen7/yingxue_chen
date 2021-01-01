package com.damon.service;

import com.damon.entity.Category;

import java.util.HashMap;


public interface CategoryService {
    HashMap<String, Object> queryOneCategory(Integer page, Integer rows);

    HashMap<String, Object> queryTwoCategory(Integer page, Integer rows, String parentId);

    void add(Category category);

    void edit(Category category);

    HashMap<String, Object> del(Category category);

}
