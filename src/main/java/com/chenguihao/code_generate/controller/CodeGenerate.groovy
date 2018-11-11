package com.chenguihao.code_generate.controller

import com.chenguihao.code_generate.bean.beantemplateinfo.BeanPropertyInfo
import com.chenguihao.code_generate.bean.beantemplateinfo.BeanTemplateInfo
import com.chenguihao.code_generate.bean.generateconfig.GenerateConfig
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author:  chenguihao by 2018-11-09 22:56
 */
@RestController
@RequestMapping("CodeGenerate")
public class CodeGenerate {

    @PostMapping("handleSqlFile")
    public void handleSqlFile(@RequestParam("sqlFile") MultipartFile sqlFile, GenerateConfig generateConfig,
                              HttpServletResponse response, HttpServletRequest request) {
        if (sqlFile.isEmpty()) throw new RuntimeException("文件不能为空")
        if (!sqlFile.getOriginalFilename().endsWith(".sql")) throw new RuntimeException("文件类型需要为sql文件");
        def originalFile = new File(request.getSession().getServletContext().getRealPath("/") + sqlFile.getOriginalFilename())
//sqlFile
        if (!originalFile.exists()) originalFile.createNewFile()//不存在创建
        println originalFile.getAbsolutePath()
        try {
            sqlFile.transferTo(originalFile)//MultipartFile转换成File类型
        } catch (Exception e) {
            e.getMessage()
        }
        File beanFile = generateBean(originalFile, generateConfig)//解析File生成beanFile对象
    }

    File generateBean(File originalFile, GenerateConfig generateConfig) {
        def beanTem = new BeanTemplateInfo()
        Map<String, Object> model = new HashMap<>()

        def isStart;
        originalFile.eachLine({
            if (it.startsWith('CREATE TABLE')) {
                isStart = true
                def temp = it.substring(it.charAt("'") + 1)
                temp = temp.substring(0, it.charAt("'"))
                model.put('tableName', temp)

            } else if (isStart) {

            }

        })
    }

    BeanPropertyInfo parseColum(String colum) {
        def proInfo = new BeanPropertyInfo()
        colum = colum.substring(1)
        proInfo.setName(colum.substring(0, colum.charAt("'")))
        colum = colum.substring(colum.charAt(" ") + 1) //类型开始的字段
        String type = colum.substring(0, colum.charAt("(")) //只有type的String
        switch (type) {
            case 'int': case 'integer': case 'tinyint':
                proInfo.setType("Integer"); break
            case "double":
                proInfo.setType("Double"); break
            case "float":
                proInfo.setType("Float"); break
            case "varchar": case "char":
                proInfo.setType("String"); break
            case "date": case "datetime": case "time": case "timestamp":
                proInfo.setType("Date"); break
//            case 'tinyint':
//                def sizeStr = colum.substring(colum.charAt("(")+1).substring(colum.charAt(")"))//大小信息的String
//                if(sizeStr.toInteger()>1)
//                    proInfo.setType("Integer")
//                else
//                    proInfo.setType("byte")
        }

    }
}
