package org.wxl.alumniMatching.mapper;

import org.wxl.alumniMatching.domain.dto.UserUpdateDTO;
import org.wxl.alumniMatching.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2023-07-16
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 修改用户信息
     * @param userUpdateDTO 用户信息
     * @return 返回队伍主键
     */
    int updateByUserUpdateDTO(UserUpdateDTO userUpdateDTO);
}
