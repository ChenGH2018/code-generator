package com.chenguihao.code_generate.bean.memberinfo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

/**
 * Author by chenguihao on 2018-11-11
 */

@Data
@Table(name = "t_memberinfo")
public class Memberinfo {

    @ApiModelProperty(hidden = true)
    @Id
    private String id;
    private String cardId;   //卡号
    private String certificateName;   //身份证名字
    private String allotStandardId;   //人员类别外键
    private Double canBiao;   //用餐标准
    private Double governmentSubsidy;   //政府补贴标准
    private Double merchantsSubsidies;
    private Double pay;   //自费标准
    private String phone;
    private String certificate;   //身份证
    private String cardName;   //卡名
    private String roomNumber;
    private Integer isDelete;   //1:删除，0：不删除
    private Double deposit;   //余额
    private String cardCode;   //信息卡密码
    private Double canhe;   //餐盒费用
    private String watchCode;
    private String boxCode;   //餐盒编码
    private Integer freeze;   //0 未 冻结 1 冻结
    private Integer breakfastAuthority;   //早餐菜单查看权限：1有；0没有
    private Integer lunchAuthority;   //午餐菜单查看权限：1有；0没有
    private Integer dinnerAuthority;   //晚餐菜单查看权限：1有；0没有
    private String addUser;
    private Date addTime;
    private String updateUser;
    private Date updateTime;
}
