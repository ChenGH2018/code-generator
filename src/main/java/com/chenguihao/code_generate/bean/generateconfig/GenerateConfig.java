package com.chenguihao.code_generate.bean.generateconfig;

import lombok.Data;
import org.springframework.context.annotation.Bean;

@Data
public class GenerateConfig {
    private String packageName;//不能为空
    private String moduleName;//不能为空
    private String tableName;
    //下面的有默认值
    private String pagePath;
    private String resultVoPath;
    private String author;

    public GenerateConfig() {
        this.pagePath = "bean.Page";
        this.resultVoPath = "bean.ResultVo";
        this.author = "chenguihao";
    }
}
