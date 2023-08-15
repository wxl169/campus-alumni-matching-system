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

    int updateByUserUpdateDTO(UserUpdateDTO userUpdateDTO);
}
