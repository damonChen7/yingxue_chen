package com.damon.service;

import com.damon.dao.VideoMapper;
import com.damon.entity.Video;
import com.damon.entity.VideoExample;
import com.damon.util.AliyunOSSUtil;
import com.damon.util.UUIDUtil;
import com.damon.util.VideoCoverUtil;
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
public class VideoServiceImpl implements VideoService {
    @Resource
    VideoMapper videoMapper;

    @Override
    public HashMap<String, Object> queryVideoPage(Integer page, Integer rows) {
        // Integer page, Integer rows(每页展示条数)
        //返回  page=当前页   rows=[User,User]数据    tolal=总页数   records=总条数
        HashMap<String, Object> map = new HashMap<>();

        //设置当前页
        map.put("page", page);
        //创建条件对象
        VideoExample example = new VideoExample();
        //创建分页对象   参数：从第几条开始，展示几条
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        //查询数据
        List<Video> users = videoMapper.selectByExampleAndRowBounds(example, rowBounds);

        map.put("rows", users);

        //查询总条数
        int records = videoMapper.selectCountByExample(example);
        map.put("records", records);

        //计算总页数
        Integer total = records % rows == 0 ? records / rows : records / rows + 1;
        map.put("total", total);

        return map;
    }

    @Override
    public String add(Video video) {
        video.setId(UUIDUtil.getUUID());
        video.setUploadTime(new Date());
        video.setLikeCount(0);
        video.setPlayCount(0);

        video.setStatus("1");
        videoMapper.insertSelective(video);
        return video.getId();
    }

    @Override
    public void edit(Video video) {
        if (video.getVideoPath() == "")
            video.setVideoPath(null);
        videoMapper.updateByPrimaryKeySelective(video);

    }

    @Override
    public void del(Video video) {
        //设置条件
        VideoExample example = new VideoExample();
        example.createCriteria().andIdEqualTo(video.getId());
        //根据id查询视频数据
        Video videos = videoMapper.selectOneByExample(example);

        //1.删除数据
        videoMapper.deleteByExample(example);

        //http://yingx2006.oss-cn-beijing.aliyuncs.com/   video/1608781629917-动画.mp4
        //http://yingx2006.oss-cn-beijing.aliyuncs.com/   cover/1608781629917-动画.jpg

        //获取视频名字并拆分
        String videoName = videos.getVideoPath().replace("http://yingx2006.oss-cn-beijing.aliyuncs.com/", "");
        //获取封面名字并拆分
        String coverName = videos.getCoverPath().replace("http://yingx2006.oss-cn-beijing.aliyuncs.com/", "");

        //2.删除视频
        AliyunOSSUtil.deleteFile("yingx2006", videoName);
        //3.删除封面
        AliyunOSSUtil.deleteFile("yingx2006", coverName);
    }

    @Override
    public void uploadVideo(MultipartFile videoPath, String id, HttpServletRequest request) {
        //1.获取文件名
        String filename = videoPath.getOriginalFilename();
        //视频拼接时间戳
        String newName = new Date().getTime() + "-" + filename;

        //2.根据相对路径获取绝对路径
        String realPath = request.getServletContext().getRealPath("/upload/video");
        //获取文件夹
        File file = new File(realPath);
        //判断文件夹是否存在
        if (!file.exists()) {
            file.mkdirs();//创建文件夹
        }

        //3.文件上传
        try {
            videoPath.transferTo(new File(realPath, newName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //4.修改
        //判断修改条件
        VideoExample example = new VideoExample();
        example.createCriteria().andIdEqualTo(id);

        Video video = new Video();
        video.setCoverPath("aa");
        video.setVideoPath(newName);

        videoMapper.updateByExampleSelective(video, example);
    }

    @Override
    public void uploadVideoAliyun(MultipartFile videoPath, String id, HttpServletRequest request) {
        //1.获取文件名
        String filename = videoPath.getOriginalFilename();

        //视频本地名称
        String newName = new Date().getTime() + "-" + filename;
        String objectName = "user/video/video/" + newName;
        //2.上传视频阿里云
        //  MultipartFile类型的文件
        //  bucketName:存储空间名
        //  objectName:文件名
        AliyunOSSUtil.uploadBytesFile(videoPath, "yingx2006", objectName);

        //3.获取文件上传路径     根据相对路径获取绝对路径
        String realPath = request.getServletContext().getRealPath("/upload/video");
        //获取文件夹
        File file = new File(realPath);
        //判断文件夹是否存在
        if (!file.exists()) {
            file.mkdirs();//创建文件夹
        }

        //视频网络路径
        String netVideoPath = "http://yingx2006.oss-cn-beijing.aliyuncs.com/" + objectName;


        //图片本地路径
        String[] split = newName.split("\\.");
        String localCoverPath = realPath + "/" + split[0] + ".jpg";

        //截取视频封面
        // @param videofile  源视频文件路径
        // @param framefile  截取帧的图片存放路径
        // @throws Exception
        try {
            VideoCoverUtil.fetchFrame(netVideoPath, localCoverPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //图片网络名称
        String netCoverName = "user/video/cover/" + split[0] + ".jpg";
        //图片网络路径
        String netCoverPath = "http://yingx2006.oss-cn-beijing.aliyuncs.com/" + netCoverName;

        //上传封面阿里云
        // bucketName:存储空间名
        // objectName:文件名
        // localFilePath:本地文件路径
        AliyunOSSUtil.uploadLocalFile("yingx2006", netCoverName, localCoverPath);

        //删除本地图片文件
        File file1 = new File(localCoverPath);
        //判断是一个文件，并且文件存在
        if (file1.isFile() && file1.exists()) {
            //删除文件
            boolean isDel = file1.delete();
        }

        //判断修改条件
        VideoExample example = new VideoExample();
        example.createCriteria().andIdEqualTo(id);

        Video video = new Video();
        video.setCoverPath(netCoverPath);
        video.setVideoPath(netVideoPath);

        videoMapper.updateByExampleSelective(video, example);
    }


}
