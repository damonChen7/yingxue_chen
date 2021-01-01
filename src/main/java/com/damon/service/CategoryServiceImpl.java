package com.damon.service;

import com.damon.dao.CategoryMapper;
import com.damon.entity.Category;
import com.damon.entity.CategoryExample;
import com.damon.util.UUIDUtil;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;


@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Resource
    CategoryMapper categoryMapper;

    @Override
    public HashMap<String, Object> queryOneCategory(Integer page, Integer rows) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        //设置当前页
        map.put("page", page);
        //创建条件对象
        CategoryExample example = new CategoryExample();
        example.createCriteria().andLevelsEqualTo(1);
        //创建分页对象   参数：从第几条开始，展示几条
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        //查询数据
        List<Category> categorys = categoryMapper.selectByExampleAndRowBounds(example, rowBounds);
        map.put("rows", categorys);

        //查询总条数
        int records = categoryMapper.selectCountByExample(example);
        map.put("records", records);

        //计算总页数
        Integer tolal = records % rows == 0 ? records / rows : records / rows + 1;
        map.put("total", tolal);

        return map;
    }

    @Override
    public HashMap<String, Object> queryTwoCategory(Integer page, Integer rows, String parentId) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        //设置当前页
        map.put("page", page);
        //创建条件对象
        CategoryExample example = new CategoryExample();
        example.createCriteria().andParentIdEqualTo(parentId);
        //创建分页对象   参数：从第几条开始，展示几条
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        //查询数据
        List<Category> categorys = categoryMapper.selectByExampleAndRowBounds(example, rowBounds);
        map.put("rows", categorys);
        //查询总条数
        int records = categoryMapper.selectCountByExample(example);
        map.put("records", records);
        //计算总页数
        Integer tolal = records % rows == 0 ? records / rows : records / rows + 1;
        map.put("total", tolal);

        return map;
    }

    @Override
    public void add(Category category) {
        //判断添加的是一级类别还是二级类别
        if (category.getParentId() == null) {
            category.setLevels(1);
        } else {
            category.setLevels(2);
        }

        category.setId(UUIDUtil.getUUID());
        categoryMapper.insertSelective(category);
    }

    @Override
    public void edit(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);
    }

    @Override
    public HashMap<String, Object> del(Category category) {
        HashMap<String, Object> map = new HashMap<>();
        //判断某级类别
        if (category.getParentId() == null) {
            //一级类别，查看是否有子分类
            CategoryExample example = new CategoryExample();
            example.createCriteria().andParentIdEqualTo(category.getId());
            int count = categoryMapper.selectCountByExample(example);
            if (count == 0) {
                //没有子分类
                categoryMapper.deleteByPrimaryKey(category);
                map.put("message", "删除成功");
            } else {
                //有子分类
                map.put("message", "删除失败，该分类下有其他子分类");
            }
        } else {
            //二级分类
            categoryMapper.deleteByPrimaryKey(category);
            map.put("message", "删除成功");
        }
        return map;
    }


}
