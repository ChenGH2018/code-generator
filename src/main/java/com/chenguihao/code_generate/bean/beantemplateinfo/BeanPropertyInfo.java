package com.chenguihao.code_generate.bean.beantemplateinfo;

import lombok.Data;

@Data
public class BeanPropertyInfo {
    String type;
    String propertyName;
    String columName;
    String description;
    Integer size;
    boolean primaryKey; //目前不需要的属性
}
