package com.example.entity;

import lombok.Data;

/**
 * config_project_bind 表实体类
 * 对应ClickHouse数据库中的 solarmonitortest.config_project_bind 表
 */
@Data
public class ConfigProjectBind {

    /**
     * 绑定ID
     */
    private Long bindId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 电站ID
     */
    private Long orderId;

    /**
     * 绑定状态
     */
    private Integer bindState;

    /**
     * 最后修改时间
     */
    private String updateTime;

    /**
     * 最后修改人
     */
    private Long updateUserId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private Long createUserId;

    /**
     * 电站编号
     */
    private String stationno;
}