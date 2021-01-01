package com.damon.service;

import com.damon.dao.UserMapper;
import com.damon.entity.User;
import com.damon.entity.UserExample;
import com.damon.util.AliyunOSSUtil;
import com.damon.util.UUIDUtil;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public HashMap<String, Object> queryUserPage(Integer page, Integer rows) {
        // Integer page, Integer rows(每页展示条数)
        //返回  page=当前页   rows=[User,User]数据    tolal=总页数   records=总条数
        HashMap<String, Object> map = new HashMap<>();

        //设置当前页
        map.put("page", page);
        //创建条件对象
        UserExample example = new UserExample();
        //创建分页对象   参数：从第几条开始，展示几条
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        //查询数据
        List<User> users = userMapper.selectByExampleAndRowBounds(example, rowBounds);

        //遍历集合
        for (User user : users) {
            //根据用户id查询学分  redis
            String id = user.getId();
            //查询学分并赋值
            user.setScore(88.0);
        }

        map.put("rows", users);

        //查询总条数
        int records = userMapper.selectCountByExample(example);
        map.put("records", records);

        //计算总页数
        Integer tolal = records % rows == 0 ? records / rows : records / rows + 1;
        map.put("total", tolal);

        return map;
    }

    @Override
    public String add(User u) {
        String uuid = UUIDUtil.getUUID();
        u.setId(uuid);
        u.setCreateDate(new Date());
        u.setStatus("1");

        userMapper.insertSelective(u);

        return uuid;
    }

    @Override
    public void edit(User u) {
        userMapper.updateByPrimaryKeySelective(u);
    }

    @Override
    public void del(User u) {
        userMapper.deleteByPrimaryKey(u);
    }

    @Override
    public void uploadUserCover(MultipartFile headImg, String id, HttpServletRequest request) {
        //1.获取文件名
        String filename = headImg.getOriginalFilename();
        //图片拼接时间戳
        String newName = new Date().getTime() + "-" + filename;

        //2.根据相对路径获取绝对路径
        String realPath = request.getServletContext().getRealPath("/upload/cover");

        //获取文件夹
        File file = new File(realPath);
        //判断文件夹是否存在
        if (!file.exists()) {
            file.mkdirs();//创建文件夹
        }

        //3.文件上传
        try {
            headImg.transferTo(new File(realPath, newName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = new User();
        user.setId(id);
        user.setHeadImg(newName);

        //4.修改数据
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public void uploadUserCoverAliyun(MultipartFile headImg, String id, HttpServletRequest request) {
        //获取文件名
        String filename = headImg.getOriginalFilename();
        String newName = new Date().getTime() + "-" + filename;
        //拼接文件夹
        String objectName = "user/cover/" + newName;
        /*
         * 1.文件上传
         * 参数：
         *   headImg：MultipartFile类型的文件
         *   bucketName:存储空间名
         *   objectName:文件名
         * */
        AliyunOSSUtil.uploadBytesFile(headImg, "yingx2006", objectName);

        //2.修改图片地址
        //http://yingx2006.oss-cn-beijing.aliyuncs.com/cover/小汽车.jpg
        //拼接网络地址
        String headImgs = "http://yingx2006.oss-cn-beijing.aliyuncs.com/" + objectName;
        User user = new User();
        user.setId(id);
        user.setHeadImg(headImgs);

        //4.修改数据
        userMapper.updateByPrimaryKeySelective(user);
    }
}
