package org.wxl.alumniMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.contant.UserConstant;
import org.wxl.alumniMatching.domain.dto.UserListDTO;
import org.wxl.alumniMatching.domain.dto.UserUpdateDTO;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.*;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.mapper.UserMapper;
import org.wxl.alumniMatching.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.alumniMatching.utils.BeanCopyUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 * @since 2023-07-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "wxl";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //用户账号控制在 5 ~ 10 位
        int userAccountLength = userAccount.length();
        if (userAccountLength < UserConstant.USER_ACCOUNT_MIN_LENGTH || userAccountLength > UserConstant.USER_ACCOUNT_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度不满要求");
        }
        //用户密码控制在 8 ~ 15 位
        int userPasswordLength = userPassword.length();
        if (userPasswordLength < UserConstant.USER_PASSWORD_MIN_LENGTH || userPasswordLength > UserConstant.USER_PASSWORD_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码长度不满要求");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        //账号不能包含空格和特殊字符
        if (matcher.find() || userAccount.contains(UserConstant.USER_ACCOUNT_EXIT_SPACE)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号不能含有特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验码不同");
        }
        // 账户不能重复
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败");
        }
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public UserLoginVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码不能为空");
        }
        if (userAccount.length() < UserConstant.USER_ACCOUNT_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"登录失败");
        }
        if (userPassword.length() < UserConstant.USER_PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"登录失败");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"登录失败");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(User::getUserAccount,userAccount);
        queryWrapper.eq(User::getUserPassword, encryptPassword);
        User user = userMapper.selectOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"登录失败");
        }
        // 3. 用户脱敏
        UserLoginVO userLoginVo = BeanCopyUtils.copyBean(user, UserLoginVO.class);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return userLoginVo;
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public UserLoginVO userAdminLogin(String userAccount, String userPassword, HttpServletRequest request) {
        UserLoginVO userLogin = this.userLogin(userAccount, userPassword, request);
        //判断用户是否为管理员
        if (!userLogin.getUserRole().equals(UserConstant.ADMIN_ROLE)){
            throw new BusinessException(ErrorCode.NO_AUTH,"该账户暂无权限");
        }
        return userLogin;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @Override
    public UserCurrentVO getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"请先登录");
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userMapper.selectById(userId);
        return BeanCopyUtils.copyBean(user, UserCurrentVO.class);
    }

    /**
     * 根据条件分页查询用户列表信息
     *
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param userListDto 查询条件
     * @return 每页的用户数据
     */
    @Override
    public PageVO searchUserList(Integer pageNum, Integer pageSize, UserListDTO userListDto, HttpServletRequest request) {
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"该账户无权限");
        }
        if (pageNum == null){
            pageNum = 1;
        }
        if (pageSize == null){
            pageSize = 5;
        }
        Page<User> page = new Page<>(pageNum,pageSize);
        //按条件查询用户列表信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(userListDto.getUsername()),User::getUsername,userListDto.getUsername())
                .like(!StringUtils.isEmpty(userListDto.getUserAccount()),User::getUserAccount,userListDto.getUserAccount())
                .eq(userListDto.getGender()!= null,User::getGender,userListDto.getGender())
                .like(!StringUtils.isEmpty(userListDto.getSchool()),User::getSchool,userListDto.getSchool())
                .eq(userListDto.getUserStatus() != null,User::getUserStatus,userListDto.getUserStatus())
                .eq(userListDto.getUserRole() != null,User::getUserRole,userListDto.getUserRole());

        page(page,queryWrapper);

        List<UserListVO> userListVOS = BeanCopyUtils.copyBeanList(page.getRecords(), UserListVO.class);
        return new PageVO(userListVOS,page.getTotal());
    }


    //-------------------------------------------- 用户页  ---------------------------------------------------------

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList 用户要拥有的标签
     * @return 所有用户信息列表
     */
    @Override
    public List<UserTagVO> searchUserByTags(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"标签为空");
        }
        //SQL查询方式
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//        //拼接 and 查询
//        //like '%Java%' and like '%Python%'
//        for (String tagName : tagNameList){
//            queryWrapper = queryWrapper.like(StringUtils.hasText(tagName),User::getTags,tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        List<UserTagVO> userTagVOS = BeanCopyUtils.copyBeanList(userList, UserTagVO.class);

        //内存查询方式
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserStatus, UserConstant.USER_STATUS_NORMAL);
        //1.先查询所有用户
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.在内存中判断是否包含要求的标签
        List<User> users = userList.stream().filter(user -> {
            String tagsStr = user.getTags();
//            if (StringUtils.isBlank(tagsStr)){
//                return false;
//            }
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {}.getType());
            //Optional.ofNullable相当于if判断，如果不为空这返回当前值，为空则为自己付的值。
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        return BeanCopyUtils.copyBeanList(users, UserTagVO.class);
    }

    /**
     * 返回所有用户
     *
     * @return 所有用户信息列表
     */
    @Override
    public List<UserTagVO> searchUserList() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserStatus,UserConstant.USER_STATUS_NORMAL);
        List<User> users = userMapper.selectList(queryWrapper);
        return BeanCopyUtils.copyBeanList(users, UserTagVO.class);
    }



    /**
     * 修改用户信息
     *
     * @param userUpdateDTO 修改的数据
     * @param loginUser 当前登录用户的信息
     * @return 成功 —— 1，失败 —— -1
     */
    @Override
    public int updateUser(UserUpdateDTO userUpdateDTO, User loginUser) {
        if (userUpdateDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求参数为空");
        }
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        //修改用户的id
        Long updateDTOId = userUpdateDTO.getId();

        //如果不是管理员，并且不是更新自己的信息
        if (!isAdmin(loginUser) && !updateDTOId.equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH,"该账户暂无权限");
        }
        //查询修改账户是否在数据库中存在
        User oldUser = userMapper.selectById(updateDTOId);
        if (oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"暂无该用户");
        }

        return userMapper.updateByUserUpdateDTO(userUpdateDTO);
    }

    /**
     * 主页推荐用户信息列表
     *
     * @return 主页推荐用户的信息列表
     */
    @Override
    public PageVO getRecommendUser(Integer pageNum,Integer pageSize, HttpServletRequest request) {
        if (pageNum == null){
            pageNum = 1;
        }
        if (pageSize == null){
            pageSize = 10;
        }
        //获取登录用户
        User loginUser = this.getLoginUser(request);
        //redis的key
         String redisKey = String.format("alumniMatching:user:recommend:%s",loginUser.getId());
         PageVO pageVO = null;
        //判断当前用户是否将推荐用户存入redis
         pageVO = (PageVO) redisTemplate.opsForValue().get(redisKey);
         if (pageVO != null){
             return  pageVO;
         }
        //如果redis没有缓存，则查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
         //排除自己的信息
         queryWrapper.ne(User::getId,loginUser.getId());
        Page<User> page = new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        List<UserTagVO> userTagVOS = BeanCopyUtils.copyBeanList(page.getRecords(), UserTagVO.class);
        pageVO = new PageVO(userTagVOS,page.getTotal());
        redisTemplate.opsForValue().set(redisKey,pageVO,1000 * 60 * 60, TimeUnit.MILLISECONDS);
        return pageVO;
    }








    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }
    @Override
    public boolean isAdmin(User loginUser) {
        // 仅管理员可查询
        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }




    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        User user= (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        return user;
    }
}