package org.wxl.alumniMatching.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.common.ResultUtils;
import org.wxl.alumniMatching.domain.vo.TagShowVO;
import org.wxl.alumniMatching.service.ITagService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 16956
 */
@Api(tags = "标签模块")
@RestController
//@CrossOrigin(origins = {"http://127.0.0.1:5173","http://localhost:8000","http://42.193.15.245:8080"},allowCredentials = "true")
@RequestMapping("/tag")
public class TagController {
    @Resource
    private ITagService tagService;

    /**
     * 展示所有标签
     * @return 展示所有标签数据
     */
    @ApiOperation(value = "展示所有标签")
    @GetMapping("/show")
    public BaseResponse<List<TagShowVO>> tagShow() {
        return ResultUtils.success(tagService.tagShow());
    }

}
