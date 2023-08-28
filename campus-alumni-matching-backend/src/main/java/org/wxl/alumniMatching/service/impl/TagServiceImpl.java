package org.wxl.alumniMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.domain.entity.Tag;
import org.wxl.alumniMatching.domain.vo.TagChildrenVO;
import org.wxl.alumniMatching.domain.vo.TagShowVO;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.mapper.TagMapper;
import org.wxl.alumniMatching.service.ITagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2023-07-16
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {
    @Resource
    private TagMapper tagMapper;
    /**
     * 展示所有标签
     * @return 展示所有标签数据
     */
    @Override
    public List<TagShowVO> tagShow() {
        List<Tag> showTag = tagMapper.getShowTag();
        List<TagShowVO> tagShowVOS = showTag.stream().map(tag -> {
            TagShowVO tagShowVO = new TagShowVO();
                tagShowVO.setText(tag.getTagName());
                tagShowVO.setChildren(getChildrenTagName(tag.getId()));
            return tagShowVO;
        }).collect(Collectors.toList());
        return tagShowVOS;
    }


    /**
     * 根据根评论的id查询所有对应的子评论的集合
     * @param parentId 父标签id
     * @return 子标签列表
     */
    private List<TagChildrenVO> getChildrenTagName(Long parentId){
        if (parentId == null || parentId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getTagName)
                .eq(Tag::getParentId,parentId);
        List<Tag> tagList = this.list(queryWrapper);
        return tagList.stream().map(tag ->{
            TagChildrenVO tagChildrenVO = new TagChildrenVO();
            tagChildrenVO.setText(tag.getTagName());
            tagChildrenVO.setId(tag.getTagName());
                    return tagChildrenVO;
        }).collect(Collectors.toList());
    }
}
