package com.chenguihao.code_generate.bean.generateconfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.annotation.Bean;

@Data
public class GenerateConfig {
    private String packageName;//这个参数为包名。如com.zhwl.nbdpc、不能为空
    private String moduleName;//模块名，模块接口说，如：商家管理模块、不能为空

    @ApiModelProperty(hidden = true)
    private String tableName;   //表名，不需要输入的参数


    //下面的有默认值
    private String baseExceptionPath;   //base异常路径，不需要加包名
    private String pagePath;
    private String resultVoPath;
    private String author;

    public GenerateConfig() {
        this.pagePath = "bean.Page";
        this.resultVoPath = "bean.ResultVo";
        this.author = "chenguihao";
        this.baseExceptionPath = "system.SysEnum.BaseException";
    }
}
