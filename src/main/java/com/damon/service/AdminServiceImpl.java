package com.damon.service;

import com.damon.dao.AdminMapper;
import com.damon.entity.Admin;
import com.damon.entity.AdminExample;
import com.damon.util.Md5Utils;
import com.damon.util.UUIDUtil;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

@Transactional
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    HttpSession session;
    @Resource
    AdminMapper adminMapper;

    @Override
    public HashMap<String, Object> login(Admin admin, String code) {
        //获取验证码
        String imageCode = (String) session.getAttribute("imageCode");

        HashMap<String, Object> map = new HashMap<>();

        //判断验证码
        if (imageCode.equals(imageCode)) {
            //根据用户名查询用户数据
            Admin admins = adminMapper.queryByUsername(admin.getUsername());
            //判断用户是否存在
            if (admins != null) {
                //判断用户状态
                if (admins.getStatus().equals("1")) {
                    //判断密码
                    String password = Md5Utils.getMd5Code(admins.getSalt() + admin.getPassword());
                    if (admins.getPassword().equals(password)) {
                        //存储用户标记
                        session.setAttribute("admin", admins);

                        map.put("status", "200");
                        map.put("message", "登录成功");
                    } else {
                        map.put("status", "401");
                        map.put("message", "密码不正确");
                    }
                } else {
                    map.put("status", "401");
                    map.put("message", "该用户已冻结");
                }
            } else {
                map.put("status", "401");
                map.put("message", "该用户不存在");
            }
        } else {
            map.put("status", "401");
            map.put("message", "验证码不正确");
        }

        return map;

    }

    @Override
    public HashMap<String, Object> queryAdminPage(Integer page, Integer rows) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("page", page);
        AdminExample example = new AdminExample();
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        List<Admin> admins = adminMapper.selectByExampleAndRowBounds(example, rowBounds);
        map.put("rows", admins);
        //查询总条数
        int records = adminMapper.selectCountByExample(example);
        map.put("records", records);
        //计算总页数
        Integer total = records % rows == 0 ? records / rows : records / rows + 1;
        map.put("total", total);

        return map;
    }

    @Override
    public String add(Admin admin) {
        String uuid = UUIDUtil.getUUID();
        admin.setId(uuid);
        //加盐
        String salt = Md5Utils.getSalt(8);
        String password = Md5Utils.getMd5Code(salt + admin.getPassword());
        System.out.println(password);
        admin.setPassword(password);
        admin.setSalt(salt);

        admin.setStatus("1");
        adminMapper.insertSelective(admin);
        return uuid;
    }

    @Override
    public void edit(Admin admin) {
        adminMapper.updateByPrimaryKeySelective(admin);
    }

    @Override
    public void del(Admin admin) {
        adminMapper.deleteByPrimaryKey(admin);
    }
}
