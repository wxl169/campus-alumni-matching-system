package org.wxl.alumniMatching.domain.enums;

/**
 * 队伍状态枚举
 *
 * @author 16956
 */
public enum TeamStatusEnum {
    /**
     * 公开队伍：
     */
    PUBLIC(0, "公开"),
    /**
     * 私有化队伍：不允许其他用户加入
     */
    PRIVATE(1, "私有"),
    /**
     * 加密队伍：进队伍需要密码
     */
    SECRET(2, "加密");

    private int value;

    private String text;

    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if (teamStatusEnum.getValue() == value) {
                return teamStatusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
