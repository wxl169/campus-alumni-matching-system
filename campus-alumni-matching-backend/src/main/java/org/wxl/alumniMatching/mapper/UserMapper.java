package org.wxl.alumniMatching.mapper;

import org.wxl.alumniMatching.domain.dto.UserUpdateDTO;
import org.wxl.alumniMatching.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

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

    /**
     * 查找匹配用户
     * </po>查询所有标签不为空，状态正常
     * @param  userId 当前用户id
     * @return 匹配用户列表
     */
    List<User> getMatchUsers(Long userId);

    /**
     * 查询所有用户
     * @return 所有用户列表
     */
    List<User> selectAllUserHavingTag();

    /**
     * 查找用户姓名和头像
     *
     * @param friendId 好友id
     * @return
     */
    User selectNameAndAvatar(Long friendId);

}
