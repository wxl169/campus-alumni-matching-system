package org.wxl.alumniMatching.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.common.ResultUtils;
import org.wxl.alumniMatching.service.UploadService;

import javax.annotation.Resource;

/**
 * @author 16956
 */
@Api(tags = "上传文件模块")
@RestController
//跨域
@CrossOrigin(origins = {"http://127.0.0.1:5173","http://localhost:8000"},allowCredentials = "true")
public class UploadController {

   @Resource
    private UploadService uploadService;

    /**
     * 上传文件
     *
     * @param img 文件名
     * @return 返回图片路径
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadImg(MultipartFile img){
        return ResultUtils.success(uploadService.uploadImg(img));
    }
}
