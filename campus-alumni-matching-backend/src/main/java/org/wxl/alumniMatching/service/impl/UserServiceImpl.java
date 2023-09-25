package org.wxl.alumniMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cn.hutool.core.lang.Pair;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.contant.UserConstant;
import org.wxl.alumniMatching.domain.dto.UserListDTO;
import org.wxl.alumniMatching.domain.dto.UserUpdateDTO;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.*;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.mapper.TagMapper;
import org.wxl.alumniMatching.mapper.UserMapper;
import org.wxl.alumniMatching.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wxl.alumniMatching.utils.AlgorithmUtils;
import org.wxl.alumniMatching.utils.BeanCopyUtils;
import org.wxl.alumniMatching.utils.RegexUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private TagMapper tagMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private RedissonClient redissonClient;


    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "wxl";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String phone) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (RegexUtils.isPhoneInvalid(phone)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"手机号格式错误");
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
        String validPattern = UserConstant.USER_ACCOUNT_REGULAR;
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        //账号不能包含空格和特殊字符
        if (matcher.find() || userAccount.contains(UserConstant.USER_ACCOUNT_EXIT_SPACE)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号不能含有特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验码不同");
        }
        // 账户手机号不能重复
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount)
                .or()
                .eq(User::getPhone,phone);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复或手机号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUsername("用户"+ userAccount);
        user.setUserAccount(userAccount);
        user.setGender(0);
        user.setPhone(phone);
        user.setAvatarUrl(UserConstant.USER_DEFAULT_AVATAR);
        user.setUserPassword(encryptPassword);
        String jsonString = "[\"男\"]";
        user.setTags(jsonString);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败");
        }
        return user.getId();
    }


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
        String validPattern = UserConstant.USER_ACCOUNT_REGULAR;
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
        // 4. 记录用户的登录态,将登录信息存入session
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return userLoginVo;
    }


    @Override
    public UserLoginVO userAdminLogin(String userAccount, String userPassword, HttpServletRequest request) {
        UserLoginVO userLogin = this.userLogin(userAccount, userPassword, request);
        //判断用户是否为管理员
        if (!userLogin.getUserRole().equals(UserConstant.ADMIN_ROLE)){
            throw new BusinessException(ErrorCode.NO_AUTH,"该账户暂无权限");
        }
        return userLogin;
    }


    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }


    @Override
    public UserCurrentVO getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"请先登录");
        }
        long userId = currentUser.getId();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,userId)
                .eq(User::getUserStatus,UserConstant.USER_STATUS_NORMAL)
                .eq(User::getIsDelete,UserConstant.USER_STATUS_NORMAL);
        User user = this.getOne(queryWrapper);
        if (user == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"暂不存在该用户");
        }
        return BeanCopyUtils.copyBean(user, UserCurrentVO.class);
    }


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
                .eq(userListDto.getUserStatus() != null,User::getUserStatus,userListDto.getUserStatus())
                .eq(userListDto.getUserRole() != null,User::getUserRole,userListDto.getUserRole());

        page(page,queryWrapper);

        List<UserListVO> userListVOS = BeanCopyUtils.copyBeanList(page.getRecords(), UserListVO.class);
        return new PageVO(userListVOS,page.getTotal());
    }


    //-------------------------------------------- 用户页  ---------------------------------------------------------


    @Override
    public PageVO searchUserByTags(Integer pageNum,Integer pageSize,List<String> tagNameList){
        if (pageNum == null){
            pageNum = 1;
        }
        if (pageSize == null){
            pageSize = 30;
        }
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"标签为空");
        }
        //获取标签库已有的标签
        List<String> childrenTagName = tagMapper.getAllChildrenTagName();
        tagNameList = tagNameList.stream().filter(tag -> {
            if (StringUtils.isBlank(tag)){
                return false;
            }
            //比较标签库是否有该标签
            for (String childrenName : childrenTagName) {
                if (childrenName.equals(tag)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        if (tagNameList.size() == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择展示标签");
        }

        //将数据存入缓存中
        String redisKey = String.format("alumniMatching:user:searchUserByTag:%s",tagNameList.get(0));
        PageVO pageVO = null;
        Gson gson = new Gson();
        List<UserTagVO> userTagVOList = null;
        //查询第一个标签数据
        userTagVOList = (List<UserTagVO>) redisTemplate.opsForValue().get(redisKey);
        if (userTagVOList == null){
            //如果第一个标签没有数据，则查出存入redis
            //查询状态正常，有标签的所有用户
            List<User> userList = userMapper.selectAllUserHavingTag();
            //2.在内存中判断是否包含要求的标签
            List<String> finalTagNameList1 = tagNameList;
            List<User> users = userList.stream().filter(user -> {
                String tagsStr = user.getTags();
                Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {}.getType());
                tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
                return tempTagNameSet.contains(finalTagNameList1.get(0));
            }).collect(Collectors.toList());
            userTagVOList = BeanCopyUtils.copyBeanList(users, UserTagVO.class);

            //生成一个随机数，
            Random random = new Random();
            // 生成1~10之间的随机数
            int randomMinutes = random.nextInt(10) + 1;
            int totalMinutes = 60 + randomMinutes;
            redisTemplate.opsForValue().set(redisKey, userTagVOList, totalMinutes, TimeUnit.MINUTES);
        }
        if (tagNameList.size() > 1){
            //如果返回一个以上，则在第一个标签数据的基础上，比较。
            //1.先查询所有用户
            List<String> finalTagNameList = tagNameList;
            userTagVOList = userTagVOList.stream().filter(user -> {
                String tagsStr = user.getTags();
                Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {}.getType());
                //如果tempTagNameSet为空，则赋初值
                tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
                for (String tagName : finalTagNameList) {
                    //集合中数据比较
                    if (!tempTagNameSet.contains(tagName)) {
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());
            redisTemplate.opsForValue().set(redisKey,userTagVOList,60,TimeUnit.MINUTES);
        }
        userTagVOList = userTagVOList.stream()
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
        pageVO = new PageVO(userTagVOList, (long) userTagVOList.size());
        return pageVO;
    }


    @Override
    public PageVO searchUserList(Integer pageNum,Integer pageSize) {
        if (pageNum == null){
            pageNum = 1;
        }
        if (pageSize == null){
            pageSize = 30;
        }
        Page<User> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserStatus,UserConstant.USER_STATUS_NORMAL);
        page(page,queryWrapper);
        return  new PageVO(BeanCopyUtils.copyBeanList(page.getRecords(), UserTagVO.class),page.getTotal());

    }




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

        //检验用户信息是否符合要求
        isInspectUserInformation(BeanCopyUtils.copyBean(userUpdateDTO,User.class));

        if (Objects.equals(userUpdateDTO.getProfile(), "")){
            userUpdateDTO.setProfile(" ");
        }
        //如果没有图片则设置默认图片
        if(StringUtils.isBlank(oldUser.getAvatarUrl())  && StringUtils.isBlank(userUpdateDTO.getAvatarUrl()) ){
            userUpdateDTO.setAvatarUrl(UserConstant.USER_DEFAULT_AVATAR);
        }

        //修改性别后，将性别添加在标签里
        if(userUpdateDTO.getGender() != null){
            String genderTag;
            if (userUpdateDTO.getGender() == 1){
                genderTag = "女";
            } else {
                genderTag = "男";
            }
            //修改标签
            String tags = oldUser.getTags();
            Integer gender = oldUser.getGender();
            String oldGenderTag = "男";
            if (gender != null && gender == 1){
                oldGenderTag = "女";
            }
            Gson gson = new Gson();
            List<String> tagList = gson.fromJson(tags,new TypeToken<List<String>>(){}.getType());
            AtomicBoolean isExist = new AtomicBoolean(false);
            String finalOldGenderTag = oldGenderTag;
            List<String> collect = tagList.stream().map(team -> {
                if (team.equals(finalOldGenderTag)) {
                    isExist.set(true);
                    team = genderTag;
                }
                return team;
            }).collect(Collectors.toList());
            if(!isExist.get()){
                collect.add(genderTag);
            }
            String tag = gson.toJson(collect);
            userUpdateDTO.setTags(tag);

        }

        return userMapper.updateByUserUpdateDTO(userUpdateDTO);
    }


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
     * 获取最匹配的用户
     * <p> 思路：将自己的标签与其他用户的标签用编辑距离算法计算匹配度
     *
     * @param pageNum 当前页码
     * @param pageSize 每页多少条数据
     * @param loginUser 当前登录用户信息
     * @return 返回最匹配用户的列表信息
     */
    @Override
    public PageVO getMatchUsers(Integer pageNum, Integer pageSize, User loginUser) {
        //如果没有标签，则返回null
        if (loginUser.getTags() == null){
            return  null;
        }
        if (pageNum == null){
            pageNum = 1;
        }
        if (pageSize == null){
            pageSize = 10;
        }
        //查询所有标签不为空，状态正常
        List<User> userList = userMapper.getMatchUsers(loginUser.getId());
        //将自己关注的用户排除
        User user1 = this.getById(loginUser.getId());
        String friends = user1.getFriends();
        Gson gson = new Gson();
        List<Long> friendIdList = gson.fromJson(friends,new TypeToken<List<Long>>(){}.getType());
        if (friendIdList != null){
            userList = userList.stream().filter(user -> {
                for (Long userId : friendIdList) {
                    if (user.getId().equals(userId)) {
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());
        }
        //自己的标签
        String tags = user1.getTags();
        List<String> tagList = gson.fromJson(tags,new TypeToken<List<String>>(){}.getType());

        //用户列表的下标 =》 相似度
        List<Pair<User,Long>> list = new ArrayList<>();
        //依次计算所有用户和当前用户的相似度
        for (User user : userList) {
            //限制展示用户的list长度
            if (list.size() >= (pageSize * 5)){
                 break;
            }
            String userTags = user.getTags();
            //无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId().equals(user1.getId())) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            //计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        //按编辑距离从小到大排序
        List<Pair<User,Long>> topUserPairList = list.stream().
                sorted((a,b) -> (int)(a.getValue() - b.getValue()))
                //分页
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
       userQueryWrapper.in(User::getId,userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<UserTagVO>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> BeanCopyUtils.copyBean(user,UserTagVO.class))
                .collect(Collectors.groupingBy(UserTagVO::getId));
        List<UserTagVO> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return new PageVO(finalUserList, (long) list.size());

    }


    @Override
    public boolean addFriend(Long friendId, User loginUser) {
        if (friendId == null || friendId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        if(friendId.equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能关注自己");
        }
        // 只有一个线程能获取到锁
        RLock lock = redissonClient.getLock("alumniMatching:user:addFriend:lock");
        try {
            while (true){
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    //获取用户的好友列表信息
                    String friendJson = null;
                    Gson gson = new Gson();
                    User user = this.getById(loginUser.getId());
                    if (StringUtils.isBlank(user.getFriends()) || UserConstant.TAG_FRIEND_NULL.equals(user.getFriends())){
                        friendJson =  String.format("[%d]", friendId);
                    }else{
                        String friends = user.getFriends();
                        List<Long> friendList = gson.fromJson(friends,new TypeToken<List<Long>>(){}.getType());
                        //判断当前好友id是否在好友列表中
                        boolean judge = false;
                        for (Long friend: friendList) {
                            if (friend.equals(friendId)){
                                judge = true;
                                break;
                            }
                        }
                        if (judge){
                            throw new BusinessException(ErrorCode.PARAMS_ERROR,"已添加该好友，请勿重复添加");
                        }
                        friendList.add(friendId);
                        friendJson = gson.toJson(friendList);
                    }
                    LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(User::getId,user.getId());
                    updateWrapper.set(User::getFriends,friendJson);
                    return this.update(updateWrapper);
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
            return false;
        }finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }


    @Override
    public List<UserTagVO> getFriendList(User loginUser) {
        if (loginUser == null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        loginUser = this.getById(loginUser.getId());
        String userFriends = loginUser.getFriends();
        Gson gson = new Gson();
        List<Long> friendIdList = gson.fromJson(userFriends,new TypeToken<List<Long>>(){}.getType());
        if (friendIdList == null || friendIdList.size() < 1){
            return null;
        }
        List<User> users = userMapper.selectBatchIds(friendIdList);
        return BeanCopyUtils.copyBeanList(users,UserTagVO.class);
    }


    @Override
    public UserDetailVO getUserDetailById(Long userId, User loginUser) {
        if (userId == null || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        User user = this.getById(userId);
        if (user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"暂无该用户");
        }
        loginUser = this.getById(loginUser.getId());
        //判断查看信息的用户是否为当前登录用户的好友
        boolean judge = judgeISFriend(userId,loginUser);

        UserDetailVO userDetailVO = BeanCopyUtils.copyBean(user, UserDetailVO.class);
        if (judge){
            //是好友
            userDetailVO.setIsFriend(1);
        }
        return userDetailVO;
    }


    @Override
    public boolean unfollowById(Long friendId, User loginUser) {
        if (friendId == null || friendId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        //首先判断该好友id是否为用户好友
        User user = this.getById(loginUser.getId());
        String friends = user.getFriends();
        Gson gson = new Gson();
        List<Long> friendList = gson.fromJson(friends,new TypeToken<List<Long>>(){}.getType());
        if (friendList == null || friendList.size() < 1){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求参数错误");
        }
        //除去要删除的好友id
        List<Long> friendIdList = friendList.stream()
                .filter(friend -> !friend.equals(friendId))
                .collect(Collectors.toList());
        String updateFriendId = gson.toJson(friendIdList);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getFriends,updateFriendId)
                .eq(User::getId,loginUser.getId());
        boolean update = this.update(updateWrapper);
        if (!update){
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"修改好友列表失败");
        }
        return true;
    }


    @Override
    public boolean userAddTags(List<String> tagNameList, User loginUser) {
        if (tagNameList == null || tagNameList.size() == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请至少选择一个标签");
        }
        User user = this.getById(loginUser.getId());
        Gson gson = new Gson();

        String tags = null;
        if (StringUtils.isBlank(user.getTags()) || UserConstant.TAG_FRIEND_NULL.equals(user.getTags())){
            //直接添加
             tags = gson.toJson(tagNameList);
        }else{
            //todo 某些父标签下的子标签只能选择一种。

            //将获取的标签与标签库中的标签对比，没有的去除
            Set<String> tagSet = gson.fromJson(user.getTags(),new TypeToken<Set<String>>(){}.getType());
            List<String> childrenTagName = tagMapper.getAllChildrenTagName();
            tagNameList = tagNameList.stream().filter(tag -> {
                if (StringUtils.isBlank(tag)){
                    return false;
                }
                //排除已选择的标签
                for (String havingTag : tagSet) {
                    if (havingTag.equals(tag)) {
                        return false;
                    }
                }
                //比较标签库是否有该标签
                for (String childrenName : childrenTagName) {
                    if (childrenName.equals(tag)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
            if (tagNameList.size() == 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请勿重复选择且选择展示标签");
            }
            //通过set集合不重复的特性，去除重复的标签名
             tagSet.addAll(tagNameList);
             tags = gson.toJson(tagSet);
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,user.getId())
                .set(User::getTags,tags);
        boolean update = this.update(updateWrapper);
        if (!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"标签更新失败");
        }
        return true;
    }


    @Override
    public boolean userDeleteTags(String tagName, User loginUser) {
        if (StringUtils.isBlank(tagName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请选择一个标签");
        }
        User user = this.getById(loginUser.getId());
        if (StringUtils.isBlank(user.getTags()) || UserConstant.TAG_FRIEND_NULL.equals(user.getTags())){
            throw new BusinessException(ErrorCode.NULL_ERROR,"无标签可删");
        }else{
            Gson gson = new Gson();
            List<String> tagNameList = gson.fromJson(user.getTags(),new TypeToken<List<String>>(){}.getType());

            tagNameList =  tagNameList.stream().filter(tag -> !tagName.equals(tag)).collect(Collectors.toList());
            String tags = gson.toJson(tagNameList);
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId,user.getId())
                    .set(User::getTags,tags);
            boolean update = this.update(updateWrapper);
            if (!update){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除标签失败");
            }
            return true;
        }
    }


    @Override
    public boolean userSendMessageCode(String phone, HttpServletRequest request) {
        //1.检验手机号码
        if (RegexUtils.isPhoneInvalid(phone)){
            //无效手机号码
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"手机号码无效");
        }
        //2.符合，生产验证码
        
        return false;
    }

    @Override
    public void userCacheByTag() {
        //先将所有子标签查询出
        List<String> childrenTagName = tagMapper.getAllChildrenTagName();
        //如果没有子标签则结束
        if (childrenTagName.size() == 0){
            return;
        }
        //过滤出标签对应用户数据已经过期的标签名
        Set<String> childrenTagNameSet = childrenTagName.stream().filter(tagName -> {
            String redisKey = String.format("alumniMatching:user:searchUserByTag:%s", tagName);
            //检查redis的key是否存在,如果存在则排除
            return Boolean.FALSE.equals(redisTemplate.hasKey(redisKey));
        }).collect(Collectors.toSet());

        //如果全部标签都存在，则直接返回
        if (childrenTagNameSet.size() == 0){
            return;
        }
        // 查询状态正常，有标签的所有用户
        List<User> userList = userMapper.selectAllUserHavingTag();
        Gson gson = new Gson();
        //如果标签对应用户数据已经过期，则重新搜索数据库 使用并行流
        childrenTagNameSet.parallelStream().forEach(tagName ->{
            String redisKey = String.format("alumniMatching:user:searchUserByTag:%s", tagName);
            //在内存中判断是否包含要求的标签
            List<User> users = userList.stream().filter(user -> {
                String tagsStr = user.getTags();
                Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {}.getType());
                tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
                return tempTagNameSet.contains(tagName);
            }).collect(Collectors.toList());
            List<UserTagVO> userTagVOList = BeanCopyUtils.copyBeanList(users, UserTagVO.class);
            //生成一个随机数，
            Random random = new Random();
            // 生成1~10之间的随机数
            int randomMinutes = random.nextInt(10) + 1;
            int totalMinutes = 60 + randomMinutes;
            redisTemplate.opsForValue().set(redisKey, userTagVOList, totalMinutes, TimeUnit.MINUTES);
        });


    }



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


    /**
     * 检验用户信息是否符合要求
     * @param user 用户信息
     */
    private void isInspectUserInformation(User user){
        //用户名控制在1 ~ 10 位
        if (StringUtils.isNotBlank(user.getUsername())) {
            int length = user.getUsername().length();
            if (length < UserConstant.USER_NAME_MIN_LENGTH || length > UserConstant.USER_NAME_MAX_LENGTH){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名请控制在1~10位");
            }

        }
        //用户密码控制在 8 ~ 15 位
        if (StringUtils.isNotBlank(user.getUserPassword())) {
            int length = user.getUserPassword().length();
            if (length < UserConstant.USER_PASSWORD_MIN_LENGTH || length > UserConstant.USER_PASSWORD_MAX_LENGTH){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码请控制在8~15位");
            }
        }

        //用户账号控制在5 ~ 10 位
        if (StringUtils.isNotBlank(user.getUserAccount())) {
            int length = user.getUserAccount().length();
            if (length < UserConstant.USER_ACCOUNT_MIN_LENGTH || length > UserConstant.USER_ACCOUNT_MAX_LENGTH){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号请控制在5~10位");
            }
        }

        //个人简介请控制在0 ~ 50 位
        if (StringUtils.isNotBlank(user.getProfile())) {
            int length = user.getProfile().length();
            if (length > UserConstant.USER_PROFILE_MAX_LENGTH){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "个人简介请控制在0~50位");
            }
        }
    }

    /**
     * 判断查看信息的用户是否为当前登录用户的好友
     *
     * @param userId 查看信息的用户
     * @param loginUser 当前登录用户
     * @return 是否为好友
     */
    private  boolean judgeISFriend(Long userId,User loginUser){
        String friends = loginUser.getFriends();
        Gson gson = new Gson();
        List<Long> friendList = gson.fromJson(friends,new TypeToken<List<Long>>(){}.getType());
        boolean judge = false;
        if (friendList != null && friendList.size() >= 1){
            for (Long friendId: friendList) {
                if (friendId.equals(userId)){
                    judge = true;
                    break;
                }
            }
        }
        return judge;
    }

}
