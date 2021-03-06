package com.example.design.template.jdbc.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author SuccessZhang
 * @date 2020/05/23
 */
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 自增主键
     */
    private Long aid;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 用户类型
     */
    private String type;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 最近一次登录日期
     */
    private Date lastLoginDate;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 1逻辑删除，0不删除
     */
    private Boolean dr;
}