package org.wxl.alumniMatching.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.common.ResultUtils;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.IUserService;
import org.wxl.alumniMatching.service.UploadService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 16956
 */
@Api(tags = "上传文件模块")
@RestController
//跨域
//@CrossOrigin(origins = {"http://127.0.0.1:5173","http://localhost:8000","http://42.193.15.245:8080"},allowCredentials = "true")
public class UploadController {

   @Resource
    private UploadService uploadService;
   @Resource
   private IUserService userService;


    /**
     * 上传文件
     *
     * @param avatarUrl 文件名
     * @return 返回图片路径
     */
    @ApiOperation(value = "上传图片")
    @PostMapping("/upload")
    public BaseResponse<String> uploadImg(MultipartFile avatarUrl, HttpServletRequest request){
        if (avatarUrl == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请上传图片");
        }
        userService.getLoginUser(request);
        String img = uploadService.uploadImg(avatarUrl);
        if (StringUtils.isBlank(img)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传图片失败");
        }
        return ResultUtils.success(img);
    }
}
