package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */


@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 用户上传文件
     *     ↓
     * Spring接收 → 封装成 MultipartFile（临时存在服务器内存中）
     *     ↓
     * 调用 aliOssUtil.upload() → 文件传到阿里云OSS（永久存储）
     *     ↓
     * 阿里云返回URL → https://cangqiongto.oss-cn-hangzhou.aliyuncs.com/xxx.jpg
     *     ↓
     * 后端返回URL给前端 → 前端可以用这个URL访问图片
     */

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile  file){
        log.info("文件上传：{}",file);
        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //获取文件后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            //生成随机文件名
            String objectName = UUID.randomUUID().toString() + suffix;
            //上传文件到阿里云,获取上传文件的URL
            String url =aliOssUtil.upload(file.getBytes(),objectName);
            //返回上传文件的URL給前端
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败：{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
