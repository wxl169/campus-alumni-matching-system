package org.wxl.alumniMatching.utils;

import com.google.gson.Gson;
import org.wxl.alumniMatching.domain.entity.Team;
import org.wxl.alumniMatching.domain.entity.User;
import org.wxl.alumniMatching.domain.vo.MessageVO;
import org.wxl.alumniMatching.domain.vo.TeamMessageVO;

/**
 * @author 16956
 */
public class TeamMessageUtils {
    /**
     *
     * @param isSysteamMessage 是否为系统消息 0——不是，1——是
     * @param user 发送用户
     * @param teamId 发送消息的队伍id
     * @param message 发送的消息
     * @param position 放置界面的位置
     * @return json格式的消息
     */
    public static  String getMessage(Integer isSysteamMessage, User user, Long teamId, Object message,String position){
        TeamMessageVO teamMessageVO = new TeamMessageVO();
        teamMessageVO.setIsSystem(isSysteamMessage);
        teamMessageVO.setContent(message);
        teamMessageVO.setPosition(position);
        Long userId = user.getId();
        String username = user.getUsername();
        String avatarUrl = user.getAvatarUrl();
        teamMessageVO.setSendUserAvatar(avatarUrl);
        teamMessageVO.setSendUserName(username);
        if (userId!=null && userId > 0){
            teamMessageVO.setSendUserId(userId);
        }
        if (teamId!=null && teamId > 0){
            teamMessageVO.setTeamId(teamId);
        }
        //把字符串转成json格式的字符串
        Gson gson = new Gson();
        return gson.toJson(teamMessageVO);
    }
}
