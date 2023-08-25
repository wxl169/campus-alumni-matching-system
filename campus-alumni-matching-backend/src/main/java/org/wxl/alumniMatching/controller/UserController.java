package org.wxl.alumniMatching.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.wxl.alumniMatching.common.BaseResponse;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.common.ResultUtils;
import org.wxl.alumniMatching.domain.dto.UserListDTO;
import org.wxl.alumniMatching.domain.dto.UserLoginDTO;
import org.wxl.alumniMatching.domain.dto.UserRegisterDTO;
import org.wxl.alumniMatching.domain.dto.UserUpdateDTO;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.*;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.service.IUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 16956
 */
@Api(tags = "用户模块")
@RestController
//跨域
@CrossOrigin(origins = {"http://127.0.0.1:5173","http://localhost:8000"},allowCredentials = "true")
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDto) {
        // 校验
        if (userRegisterDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        String userAccount = userRegisterDto.getUserAccount();
        String userPassword = userRegisterDto.getUserPassword();
        String checkPassword = userRegisterDto.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 管理员登录
     *
     * @param userLoginDto 账号密码
     * @param request
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
     * @param request
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
     * 用户注销
     *
     * @param request
     * @return
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
     * @param request
     * @return
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
     * @param request
     * @return 分页信息
     */
    @ApiOperation(value = "用户条件查询")
    @GetMapping("/search")
    public BaseResponse<PageVO> searchUserList(@RequestParam(value = "current",required=false) Integer pageNum,
                                               Integer pageSize, UserListDTO userListDto, HttpServletRequest request){
        return ResultUtils.success(userService.searchUserList(pageNum,pageSize,userListDto,request));
    }


    /**
     * 查询符合标签的用户
     * @param pageNum 页面
     * @param pageSize 每页数据量
     * @param tagNameList 标签列表
     * @return
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
     * @param request
     * @return
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
     * @param request
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
     * 获取最匹配的用户
     * @param pageNum 当前页码
     * @param pageSize 每页多少条数据
     * @param request 当前登录用户信息
     * @return 返回最匹配用户的列表信息
     */
    @GetMapping("/match")
    public BaseResponse<PageVO> matchUsers(Integer pageNum,Integer pageSize, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getMatchUsers(pageNum,pageSize,loginUser));
    }


}
