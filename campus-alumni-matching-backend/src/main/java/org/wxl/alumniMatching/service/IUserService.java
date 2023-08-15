package org.wxl.alumniMatching.service;

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
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    UserLoginVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 管理员登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    UserLoginVO userAdminLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
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
     *
     * @return 所有用户信息列表
     */
    List<UserTagVO> searchUserList();

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList 用户要拥有的标签
     * @return 拥有查询标签的用户信息列表
     */
    List<UserTagVO> searchUserByTags(List<String> tagNameList);


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
     *
     * @return 主页推荐用户的信息列表
     */
    PageVO getRecommendUser(Integer pageNum,Integer pageSize, HttpServletRequest request);





    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 判断是否是管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);
    boolean isAdmin(User loginUser);


}
