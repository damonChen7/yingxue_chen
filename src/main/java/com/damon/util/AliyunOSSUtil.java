package com.damon.util;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * @author AliyunOSSUtil
 * @time 2020/12/23-15:58
 */
public class AliyunOSSUtil {


    // Endpoint以杭州为例，其它Region请按实际情况填写。Region：存储地址
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
    private static String accessKeyId = "LTAI4G7E6Gns2NsyNaWmZrCo";
    private static String accessKeySecret = "lYtxh4E5vulmHW2dbZ2LZfwLv47dbp";

    /*
     * 将文件上传至阿里云
     * 参数：
     *   headImg：MultipartFile类型的文件
     *   bucketName:存储空间名
     *   objectName:文件名
     * */
    public static void uploadBytesFile(MultipartFile headImg, String bucketName, String objectName) {

        byte[] bytes = null;
        try {
            //将文件转为byte数组
            bytes = headImg.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传Byte数组。
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));

        // 关闭OSSClient。
        ossClient.shutdown();
    }


    /*
     * 将本地文件上传至阿里云
     * 参数：
     *   bucketName:存储空间名
     *   objectName:文件名
     *   localFilePath:本地文件路径
     * */
    public static void uploadLocalFile(String bucketName, String objectName, String localFilePath) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 创建PutObjectRequest对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new File(localFilePath));

        // 上传文件。
        ossClient.putObject(putObjectRequest);

        // 关闭OSSClient。
        ossClient.shutdown();
    }


    /*
     * 视频截取帧
     * 参数：
     *   bucketName:存储空间名
     *   objectName:视频文件名
     * */
    public static URL videoInterceptCover(String bucketName, String objectName) {


        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 设置视频截帧操作。
        String style = "video/snapshot,t_1000,f_jpg,w_800,h_600";
        // 指定过期时间为10分钟。
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 10);
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.GET);
        req.setExpiration(expiration);
        req.setProcess(style);
        URL signedUrl = ossClient.generatePresignedUrl(req);

        System.out.println(signedUrl);
        // 关闭OSSClient。
        ossClient.shutdown();

        return signedUrl;
    }

    /*
     * 上传网络图片
     * 参数：
     *   bucketName:存储空间名
     *   objectName:保存的图片名
     *   netFilePath:文件网络地址
     * */
    public static void uploadNetFile(String bucketName, String objectName, String netFilePath) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传网络流。
        InputStream inputStream = null;
        try {
            inputStream = new URL(netFilePath).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ossClient.putObject(bucketName, objectName, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
    }


    /*
     * 视频截取帧并上传至阿里云
     * 参数：
     *   bucketName:存储空间名
     *   videoObjectName:视频文件名
     *   coverObjectName:封面文件名
     * */
    public static void videoInterceptCoverUpload(String bucketName, String videoObjectName, String coverObjectName) {


        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 设置视频截帧操作。
        String style = "video/snapshot,t_1000,f_jpg,w_800,h_600";
        // 指定过期时间为10分钟。
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 10);
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, videoObjectName, HttpMethod.GET);
        req.setExpiration(expiration);
        req.setProcess(style);
        URL signedUrl = ossClient.generatePresignedUrl(req);

        // 上传网络流。
        InputStream inputStream = null;
        try {
            inputStream = new URL(signedUrl.toString()).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //上传图片
        ossClient.putObject(bucketName, coverObjectName, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
    }

    /*
     * 删除文件
     * 参数：
     *   bucketName:存储空间名
     *   objectName:保存的图片名
     * */
    public static void deleteFile(String bucketName, String objectName) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
        ossClient.deleteObject(bucketName, objectName);

        // 关闭OSSClient。
        ossClient.shutdown();

    }


    public static void main(String[] args) {
        /*URL url = videoInterceptCover("yingx2006", "video/1608781629917-动画.mp4");
        System.out.println(url.toString());

        uploadNetFile("yingx2006", "cover/1608781629917-动画sss.jpg",url.toString());*/

        videoInterceptCoverUpload("yingx2006", "video/1608781629917-动画.mp4", "cover/1608781629917-动画aaaa.jpg");
    }

}
