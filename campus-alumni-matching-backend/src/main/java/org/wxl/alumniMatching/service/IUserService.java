package org.wxl.alumniMatching.service;

import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.domain.dto.UserListDTO;
import org.wxl.alumniMatching.domain.dto.UserUpdateDTO;
import org.wxl.alumniMatching.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wxl.alumniMatching.domain.vo.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 * @since 2023-07-16
 */
public interface IUserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param phone 手机号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword,String phone);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request 当前用户信息
     * @return 脱敏后的用户信息
     */
    UserLoginVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 管理员登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request 当前用户信息
     * @return 脱敏后的用户信息
     */
    UserLoginVO userAdminLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 当前用户信息
     * @return 是否退出成功
     */
    int userLogout(HttpServletRequest request);

    /**
     * 获取当前用户
     *
     */
    UserCurrentVO getCurrentUser(HttpServletRequest request);

    /**
     * 根据条件分页查询用户列表信息
     *
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param userListDto 查询条件
     * @return 每页的用户数据
     */
    PageVO searchUserList(Integer pageNum, Integer pageSize, UserListDTO userListDto, HttpServletRequest request);



    /**
     * 返回所有用户
     * @param pageNum 页面
     * @param pageSize 每页数据量
     * @return 所有用户信息列表
     */
    PageVO searchUserList(Integer pageNum,Integer pageSize);

    /**
     * 根据标签搜索用户
     * @param pageNum 页面
     * @param pageSize 每页数据量
     * @param tagNameList 用户要拥有的标签
     * @return 拥有查询标签的用户信息列表
     */
    PageVO searchUserByTags(Integer pageNum,Integer pageSize,List<String> tagNameList);


    /**
     * 修改用户信息
     *
     * @param userUpdateDTO 修改的数据
     * @param loginUser 当前登录用户的信息
     * @return 成功 —— 1，失败 —— null
     */
    int updateUser(UserUpdateDTO userUpdateDTO,User loginUser);


    /**
     * 主页推荐用户信息列表
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return 主页推荐用户的信息列表
     */
    PageVO getRecommendUser(Integer pageNum,Integer pageSize, HttpServletRequest request);

    /**
     * 获取最匹配的用户
     * @param pageNum 当前页码
     * @param pageSize 每页多少条数据
     * @param loginUser 当前登录用户信息
     * @return 返回最匹配用户的列表信息
     */
    PageVO getMatchUsers(Integer pageNum, Integer pageSize, User loginUser);


    /**
     * 获取当前登录用户信息
     *
     * @param request 当前用户信息
     * @return 用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 判断是否是管理员
     *
     * @param request 当前用户信息
     * @return 是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);
    boolean isAdmin(User loginUser);


    /**
     * 添加好友
     *
     * @param friendId 好友id
     * @param user 当前登录用户信息
     * @return 返回是否添加成功
     */
    boolean addFriend(Long friendId,User user);

    /**
     * 关注的好友信息
     *
     * @param user 当前登录用户信息
     * @return 返回关注的好友列表
     */
    List<UserTagVO> getFriendList(User user);

    /**
     * 根据id获取用户信息
     *
     * @param userId 获取信息的用户id
     * @param loginUser 获取当前信息
     * @return 获取用户的信息
     */
    UserDetailVO getUserDetailById(Long userId, User loginUser);

    /**
     * 取消好友关注
     *
     * @param friendId 好友id
     * @param loginUser 当前用户信息
     * @return 是否取消成功
     */
    boolean unfollowById(Long friendId, User loginUser);

    /**
     * 添加标签
     *
     * @param tagNameList 标签列表
     * @param loginUser 当前登录用户
     * @return 是否添加成功
     */
    boolean userAddTags(List<String> tagNameList, User loginUser);

    /**
     * 删除标签
     *
     * @param tagName 标签
     * @param loginUser 当前登录用户
     * @return 是否删除成功
     */
    boolean userDeleteTags(String tagName, User loginUser);
    /**
     * 用户发送邮件
     * @param phone 用户手机号码
     * @param request 信息
     * @return 是否发送成功
     */
    boolean userSendMessageCode(String phone, HttpServletRequest request);

    /**
     * 将所有标签对应的用户信息缓存到redis中
     */
    void userCacheByTag();

    /**
     * 查询符合条件的用户信息
     *
     * @param condition 查询用户名或账号
     * @param loginUser 当前用户信息
     * @return 分页信息
     */
    List<UserTagVO> selectConditionUser(String condition, User loginUser);
}
