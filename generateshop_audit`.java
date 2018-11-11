package com.test.codegenerate;

import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * author by chenguihao
 * createTime 2018-11-11
 */

@Data
@Table(name = "t_shop_audit`")
public class BeanTemplate {
    @Id
    @ApiModelProperty(hidden = true)
    private String id;
    private String  shopCompleteId //申请商家ID;
    private String  applicationPerson //申请人;
    private Date  applicationTime //申请时间;
    private String  applicationDescription //申请说明;
    private Integer  auditStatus //审核状态;
    private String  auditOpinion //审核意见;
    private Date  auditTime //审核时间;
}
