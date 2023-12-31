package org.wxl.alumniMatching.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.common.ResultUtils;
import org.wxl.alumniMatching.config.OSSConfig;
import org.wxl.alumniMatching.domain.dto.*;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.*;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.IUserService;
import org.wxl.alumniMatching.utils.RegexUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 16956
 */
@Api(tags = "用户模块")
@RestController
//@CrossOrigin(origins = {"http://127.0.0.1:5173","http://localhost:8000","http://42.193.15.245:8080"},allowCredentials = "true")
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册数据
     * @return 主键id
     */
    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public BaseResponse userRegister(@RequestBody UserRegisterDTO userRegisterDto) {
        // 校验
        if (userRegisterDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        String userAccount = userRegisterDto.getUserAccount();
        String userPassword = userRegisterDto.getUserPassword();
        String phone = userRegisterDto.getUserPhone();
        String checkPassword = userRegisterDto.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        if (RegexUtils.isPhoneInvalid(phone)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"手机号格式错误");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword,phone);
        if (result > 0){
           return ResultUtils.success(true);
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"注册失败");
    }

    /**
     * 管理员登录
     *
     * @param userLoginDto 账号密码
     * @param request 获取当前用户信息
     * @return 登录用户信息
     */
    @ApiOperation(value = "管理员登录")
    @PostMapping("/admin/login")
    public BaseResponse<UserLoginVO> userAdminLogin(@RequestBody UserLoginDTO userLoginDto, HttpServletRequest request) {
        if (userLoginDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        String userAccount = userLoginDto.getUserAccount();
        String userPassword = userLoginDto.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        UserLoginVO userLoginVO = userService.userAdminLogin(userAccount, userPassword, request);
        return ResultUtils.success(userLoginVO);
    }

    /**
     * 用户登录
     *
     * @param userLoginDto 账号密码
     * @param request 获取当前用户信息
     * @return 登录用户信息
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public BaseResponse<UserLoginVO> userLogin(@RequestBody UserLoginDTO userLoginDto, HttpServletRequest request) {
        if (userLoginDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        String userAccount = userLoginDto.getUserAccount();
        String userPassword = userLoginDto.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        UserLoginVO userLoginVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(userLoginVO);
    }

    /**
     * TODO 用户发送短信
     * @param phone 用户手机号码
     * @param request 信息
     * @return 是否发送成功
     */
    @ApiOperation(value = "发送短信")
    @PostMapping("/send/code")
    public BaseResponse<Boolean> userSendMessageCode(@RequestParam("phone") String phone, HttpServletRequest request) {
        if (RegexUtils.isPhoneInvalid(phone)){
            //无效手机号码
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"手机号码无效");
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"暂不支持发送短信");
//        return ResultUtils.success(userService.userSendMessageCode(phone,request));
    }

    /**
     * TODO 用户通过手机号登录
     * @param messageCodeDTO  手机号和验证码
     * @return 用户信息
     */
    @ApiOperation(value = "手机号登录")
    @PostMapping("/login/phone")
    public BaseResponse<UserLoginVO> userLoginByMessageCode(@RequestBody UserMessageCodeDTO messageCodeDTO, HttpServletRequest request) {
        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"暂不支持手机号登录");
//        return ResultUtils.success(null);
    }

    /**
     * 用户注销
     *
     * @param request 获取当前用户信息
     * @return 退出是否成功
     */
    @ApiOperation(value = "用户注销")
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }


    /**
     * 获取当前用户
     *
     * @param request 获取当前用户信息
     * @return 当前用户信息
     */
    @ApiOperation(value = "获取当前用户信息")
    @GetMapping("/currentUser")
    public BaseResponse<UserCurrentVO> getCurrentUser(HttpServletRequest request) {
        UserCurrentVO userCurrentVo = userService.getCurrentUser(request);
        return ResultUtils.success(userCurrentVo);
    }

    /**
     * 分页查询用户信息
     *
     * @param userListDto 查询条件
     * @param request 获取当前用户信息
     * @return 分页信息
     */
    @ApiOperation(value = "用户条件查询")
    @GetMapping("/search")
    public BaseResponse<PageVO> searchUserList(@RequestParam(value = "current",required=false) Integer pageNum,
                                               Integer pageSize, UserListDTO userListDto, HttpServletRequest request){
        return ResultUtils.success(userService.searchUserList(pageNum,pageSize,userListDto,request));
    }


    /**
     * 查询符合条件的用户信息
     *
     * @param condition 查询用户名或账号
     * @param request 获取当前用户信息
     * @return 分页信息
     */
    @ApiOperation(value = "用户条件查询")
    @GetMapping("/search/user")
    public BaseResponse<List<UserTagVO>> searchUserList2(String condition, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"请登录");
        }
        return ResultUtils.success(userService.selectConditionUser(condition,loginUser));
    }


    /**
     * 查询符合标签的用户
     * @param pageNum 页面
     * @param pageSize 每页数据量
     * @param tagNameList 标签列表
     * @return 分页信息
     */
    @ApiOperation(value = "查询符合标签的用户")
    @GetMapping("/search/tags")
    public BaseResponse<PageVO> searchUsersByTags(
            @ApiParam(value = "标签列表",required = false)
            Integer pageNum,Integer pageSize,
           @RequestParam(required = false) List<String> tagNameList
    ){
        PageVO pageVO = null;
        if (CollectionUtils.isEmpty(tagNameList)){
            pageVO = userService.searchUserList(pageNum,pageSize);
        }else {
            pageVO = userService.searchUserByTags(pageNum,pageSize,tagNameList);
        }
        return ResultUtils.success(pageVO);
    }

    /**
     * 修改用户信息
     *
     * @param userUpdateDTO 修改的数据
     * @param request 获取当前用户信息
     * @return 是否修改成功
     */
    @ApiOperation(value = "修改用户信息")
    @PutMapping("/updateUser")
    public BaseResponse<Integer> updateUser(
            @ApiParam(value = "修改的数据",required = true)
            @RequestBody UserUpdateDTO userUpdateDTO,
            HttpServletRequest request
    ){
        if (userUpdateDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        //获取当前用户登录信息
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"请登录");
        }
        int updateUser = userService.updateUser(userUpdateDTO, loginUser);
        return  ResultUtils.success(updateUser);
    }



    /**
     * 主页推荐用户信息列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param request 获取当前用户信息
     * @return 分页数据
     */
    @ApiOperation(value = "主页推荐用户信息列表")
    @GetMapping("/recommend")
    public BaseResponse<PageVO> recommendUsers(
            Integer pageNum,Integer pageSize, HttpServletRequest request
    ){
        PageVO pageVO = userService.getRecommendUser(pageNum,pageSize,request);
        return ResultUtils.success(pageVO);
    }


    /**
     * 根据id获取用户信息
     *
     * @param userId 获取信息的用户id
     * @param request 获取当前信息
     * @return 获取用户的信息
     */
    @ApiOperation("根据id获取用户信息")
    @GetMapping("/get")
    public BaseResponse<UserDetailVO> getUserDetailById(Long userId,HttpServletRequest request){
        if (userId == null || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getUserDetailById(userId,loginUser));
    }


    /**
     * 获取最匹配的用户
     * @param pageNum 当前页码
     * @param pageSize 每页多少条数据
     * @param request 当前登录用户信息
     * @return 返回最匹配用户的列表信息
     */
    @ApiOperation(value = "获取最匹配的用户信息")
    @GetMapping("/match")
    public BaseResponse matchUsers(Integer pageNum,Integer pageSize, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser.getTags()  == null ){
            return ResultUtils.error(ErrorCode.NULL_ERROR,"暂无匹配用户，请添加合适的标签");
        }
        PageVO matchUsers = userService.getMatchUsers(pageNum, pageSize, loginUser);
        if (matchUsers == null){
            return ResultUtils.error(ErrorCode.NULL_ERROR,"暂无匹配用户，请添加合适的标签");
        }
        return ResultUtils.success(matchUsers);
    }


    /**
     * 添加好友
     *
     * @param friendId 好友id
     * @param request 当前用户信息
     * @return 是否添加成功
     */
    @ApiOperation(value = "添加好友")
    @PostMapping("/add/friend")
    public BaseResponse<Boolean> addFriends(Long friendId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (friendId == null || friendId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        boolean addJudge = userService.addFriend(friendId,loginUser);
        if (!addJudge){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"添加好友失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 取消好友关注
     *
     * @param friendId 好友id
     * @param request 当前用户信息
     * @return 是否取消成功
     */
    @ApiOperation(value = "取消好友关注")
    @DeleteMapping("/delete/friend")
    public BaseResponse<Boolean> unfollowById(Long friendId,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (friendId == null || friendId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        boolean unfollowById = userService.unfollowById(friendId,loginUser);
        if (!unfollowById){
            throw new BusinessException(ErrorCode.NULL_ERROR,"取消关注失败");
        }
        return  ResultUtils.success(true);
    }

    /**
     * 关注的好友信息
     *
     * @param request 当前用户信息
     * @return 返回关注的好友列表
     */
    @ApiOperation(value = "关注列表")
    @GetMapping("/list/friend")
    public BaseResponse<List<UserTagVO>> friendList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getFriendList(loginUser));
    }

    /**
     * 添加标签
     *
     * @param userAddTagDTO 标签列表
     * @param request 当前登录用户
     * @return 是否添加成功
     */
    @ApiOperation(value = "添加标签")
    @PostMapping("/add/tags")
    public BaseResponse<Boolean> userAddTags(@RequestBody UserUpdateTagDTO userAddTagDTO, HttpServletRequest request){
        if (userAddTagDTO.getTagNameList() == null || userAddTagDTO.getTagNameList().size() == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请至少选择一个标签");
        }
        User loginUser = userService.getLoginUser(request);
        boolean judge = userService.userAddTags(userAddTagDTO.getTagNameList(),loginUser);
        if (!judge){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"添加失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 删除标签
     *
     * @param tagName 标签列表
     * @param request 当前登录用户
     * @return 是否删除成功
     */
    @ApiOperation(value = "删除标签")
    @DeleteMapping("/delete/tag")
    public BaseResponse<Boolean> userDeleteTags(String tagName, HttpServletRequest request){
        if (StringUtils.isBlank(tagName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择一个标签");
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.userDeleteTags(tagName,loginUser));
    }

    /**
     * 当进入搜索用户时，将所有标签对应的用户信息缓存在redis中
     * @param request 判断是否登录
     * @return 无返回值
     */
    @ApiOperation(value = "根据标签缓存用户信息")
    @GetMapping("/cache/tag")
    public BaseResponse userCacheByTag(HttpServletRequest request){
        //判断当前用户是否登录
        userService.getLoginUser(request);
        //将所有标签对应的用户信息缓存在redis中
        userService.userCacheByTag();
        return ResultUtils.success(true);
    }


}
