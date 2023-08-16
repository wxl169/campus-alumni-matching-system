package org.wxl.alumniMatching.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author 16956
 */
public interface UploadService {
    /**
     * 上传文件
     *
     * @param img 文件名
     * @return 返回图片路径
     */
    String uploadImg(MultipartFile img);
}
