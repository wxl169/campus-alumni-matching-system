package org.wxl.alumniMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.wxl.alumniMatching.common.ErrorCode;
import org.wxl.alumniMatching.domain.entity.UserTeam;
import org.wxl.alumniMatching.exception.BusinessException;
import org.wxl.alumniMatching.mapper.UserTeamMapper;
import org.wxl.alumniMatching.service.IUserTeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  用户队伍关系服务实现类
 * </p>
 * @author 16956
 */
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam> implements IUserTeamService {
@Resource
private UserTeamMapper userTeamMapper;


    @Override
    public long countTeamUserByTeamId(long teamId) {
        if (teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTeam::getTeamId, teamId);
        return this.count(queryWrapper);
    }


    @Override
    public List<Long> getJoinTeamId(Long userId) {
        if (userId == null ||  userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
        }
        return userTeamMapper.getJoinTeamId(userId);
    }


    @Override
    public Map<String, Integer> getUserTags(Long teamId) {
        //获取参与该队伍的所有成员的标签
        List<String> tags = userTeamMapper.getUserTags(teamId);
        Gson gson = new Gson();
        Map<String, Integer> map = new HashMap<>(32);
        if (tags.size() < 1 ){
            return null;
        }
        //按出现次数排列
        tags.forEach(tag->{
            List<String> tagList = gson.fromJson(tag, new TypeToken<List<String>>() {}.getType());
            tagList.forEach(tagSort->{
                map.put(tagSort,map.getOrDefault(tagSort,0)+1);
            });
        });
        return map;
    }
}
