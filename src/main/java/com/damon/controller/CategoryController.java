package com.damon.controller;

import com.damon.entity.Category;
import com.damon.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;

@Controller
@RequestMapping("category")
public class CategoryController {
    @Resource
    CategoryService categoryService;

    @ResponseBody
    @RequestMapping("queryOneCategory")
    public HashMap<String, Object> queryOneCategory(Integer page, Integer rows) {
        return categoryService.queryOneCategory(page, rows);
    }

    @ResponseBody
    @RequestMapping("queryTwoCategory")
    public HashMap<String, Object> queryTwoCategory(Integer page, Integer rows, String parentId) {
        return categoryService.queryTwoCategory(page, rows, parentId);
    }

    @ResponseBody
    @RequestMapping("edit")
    public HashMap<String, Object> edit(Category category, String oper) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (oper.equals("add")) {
            categoryService.add(category);
        }
        if (oper.equals("edit")) {
            categoryService.edit(category);
        }
        if (oper.equals("del")) {
            map = categoryService.del(category);
        }
        return map;
    }


}
