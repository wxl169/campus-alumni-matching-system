package org.wxl.alumniMatching.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.UploadService;
import org.wxl.alumniMatching.utils.PathUtils;

import java.io.InputStream;

/**
 * @author 16956
 */
@Service
@Data
@ConfigurationProperties(prefix = "oss")
public class OssUploadServiceImpl implements UploadService {


    @Override
    public String uploadImg(MultipartFile avatarUrl) {
        if (avatarUrl == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请上传图片");
        }
        //判断文件类型或者文件大小
        //获取原始文件名
        String originalFilename = avatarUrl.getOriginalFilename();
        //对原始文件名进行判断
        assert originalFilename != null;
        if(!originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpg")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片格式不符");
        }
        //如果判断通过上传文件到OSS
        String filePath = PathUtils.generateFilePath(originalFilename);
        return uploadOss(avatarUrl,filePath);
    }


    private  String accessKey;
    private  String secretKey;
    private  String bucket;
    private  String cdnTest;


    private String uploadOss(MultipartFile imgFile,String filePath){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(	Region.autoRegion());
        // 指定分片上传版本
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = filePath;

        try {
            InputStream inputStream = imgFile.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return cdnTest + key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return null;
    }

}