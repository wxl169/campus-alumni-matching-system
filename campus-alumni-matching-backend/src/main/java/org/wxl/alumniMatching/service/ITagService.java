package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.domain.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.TagShowVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2023-07-16
 */
public interface ITagService extends IService<Tag> {

    /**
     * 展示所有标签
     * @return 展示所有标签数据
     */
    List<TagShowVO> tagShow();
}
