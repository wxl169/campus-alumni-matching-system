package org.wxl.alumniMatching.mapper;

import org.wxl.alumniMatching.domain.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wxl.alumniMatching.domain.vo.TagShowVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2023-07-16
 */
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 展示父标签
     * @return 展示父标签
     */
    List<Tag> getShowTag();
}
