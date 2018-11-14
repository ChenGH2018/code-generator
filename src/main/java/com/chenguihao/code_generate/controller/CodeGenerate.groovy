package com.chenguihao.code_generate.controller

import com.chenguihao.code_generate.bean.beantemplateinfo.BeanPropertyInfo
import com.chenguihao.code_generate.bean.generateconfig.GenerateConfig
import com.chenguihao.code_generate.util.Const
import com.google.common.base.Strings
import freemarker.cache.NullCacheStorage
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import javax.management.RuntimeMBeanException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 * Author:  chenguihao by 2018-11-09 22:56
 */
@RestController
@RequestMapping("CodeGenerate")
public class CodeGenerate {
    static  def configuration;

    CodeGenerate() {
        configuration = new Configuration(Configuration.VERSION_2_3_28)
        String filePath = this.getClass().getClassLoader().getResource("template").getPath();//获取文件路径
        configuration.setDirectoryForTemplateLoading(new File(filePath))
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setCacheStorage(NullCacheStorage.INSTANCE);
    }

    @PostMapping("handleSqlFile")
    public void handleSqlFile(@RequestParam("sqlFile") MultipartFile sqlFile, GenerateConfig generateConfig,
                              HttpServletResponse response, HttpServletRequest request) {
        if (sqlFile.isEmpty()) throw new RuntimeException("文件不能为空")
        if (!sqlFile.getOriginalFilename().endsWith(".sql")) throw new RuntimeException("文件类型需要为sql文件");
        if(Strings.isNullOrEmpty(generateConfig.getPackageName()) || Strings.isNullOrEmpty(generateConfig.getModuleName()))
            throw new RuntimeMBeanException("包名或者模块名不能为空")
        def originalFile = new File(request.getSession().getServletContext().getRealPath("/") + sqlFile.getOriginalFilename())
//sqlFile
        if (!originalFile.exists()) originalFile.createNewFile()//不存在创建
        println originalFile.getAbsolutePath()
        sqlFile.transferTo(originalFile)//MultipartFile转换成File类型
        //初始化
        List<BeanPropertyInfo> proList = new ArrayList<>()
        initSqlFile(originalFile,proList,generateConfig)
        File beanFile = generateBeanFile(proList,generateConfig)//生成bean
        File controllerFile = generateControllerFile(proList,generateConfig)//生成controller
        File serviceFile = generateServiceFile(proList,generateConfig)//生成service文件
        File serviceImplFile = generateServiceImplFile(proList,generateConfig)//生成service
        File mapperFile = generateMapperFile(proList,generateConfig)//生成mapper
        File mapperXmlFile = generateMapperXmlFile(proList,generateConfig)//生成mapperXml
    }

    File generateMapperXmlFile(ArrayList<BeanPropertyInfo> beanPropertyInfos, GenerateConfig generateConfig) {
        def prefix = ".xml"
        def beanName = tableNameToFileName(generateConfig.getTableName())
        def fileName = beanName+"Mapper"
        Map<String, Object> model = new HashMap<>()//freamarker解析的model
        List<String> fields = new ArrayList<>()

        //填充数据
        model.put("mapperPath", generateConfig.getPackageName()+"."+"mapper"+"."+beanName.toLowerCase()+"."+fileName)
        //去掉id属性
        for (int i =0 ; i<beanPropertyInfos.size();i++){
            if(beanPropertyInfos.get(i).getColumName() == "id") beanPropertyInfos.remove(beanPropertyInfos.get(i))
        }
        //生成fields
        beanPropertyInfos.forEach({
            fields.add(it.getColumName())
        })
        model.put("tableAlias",generateTableAlias(generateConfig.getTableName()))
        model.put("BeanPropertyInfos",beanPropertyInfos)
        model.put("fields",fields)
        model.put("beanName",beanName)
        model.put("tableName",generateConfig.getTableName())
        def template = configuration.getTemplate("MapperXmlTemplate.flt")
        def file = new File(getGenerateUrl().getPath() + '/Mapper/'+beanName.toLowerCase()+"/"+ fileName + prefix)
        file.getParentFile().mkdirs()
        file.createNewFile()
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        template.process(model, writer)
        writer.flush()
        writer.close()
        return file

    }

    def generateTableAlias(String tableName) {
        if(!tableName.length())
            if(tableName.length()<= 0) throw new RuntimeException("tableName不能为空")
        def offset = tableName[0] == "t"? 2:0;
        def result = tableName[offset].toLowerCase()
        offset++
        for (;offset<tableName.length();offset++){
            if(tableName[offset] == "_")
                result += tableName[offset+1].toLowerCase()
        }
        return result;
    }

    File generateMapperFile(ArrayList<BeanPropertyInfo> beanPropertyInfos, GenerateConfig generateConfig) {
        def prefix = ".java"
        def beanName = tableNameToFileName(generateConfig.getTableName())
        def fileName = beanName+"Mapper"
        Map<String, Object> model = new HashMap<>()//freamarker解析的model
        List<String> importPackages = new ArrayList<>()

        //填写导入包信息
        importPackages.add(generateConfig.getPackageName()+".bean."+beanName.toLowerCase()+"."+beanName)
        importPackages.add(generateConfig.getPackageName()+"."+generateConfig.getPagePath())
        importPackages.add("java.util.List")

        //开启生成
        model.put("author", generateConfig.getAuthor())
        model.put("createDate", Const.dateFormat.format(new Date()))
//        model.put("package", generateConfig.getPackageName())
//        model.put("packageName", generateConfig.getPackageName()+"."+fileName.toLowerCase())
        model.put("importPackages", importPackages)
        model.put("fileName",fileName)//${mapperName} ${mapperArgName}
        model.put("moduleName",generateConfig.getModuleName())
        model.put("beanName",beanName)
        model.put("beanArgName",beanName[0].toLowerCase()+beanName.substring(1,beanName.length()))
        def template = configuration.getTemplate("MapperTemplate.flt")
        def file = new File(getGenerateUrl().getPath() + '/Mapper/'+beanName.toLowerCase()+"/"+ fileName + prefix)
        file.getParentFile().mkdirs()
        file.createNewFile()
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        template.process(model, writer)
        writer.flush()
        writer.close()
        return file
    }

    File generateServiceImplFile(ArrayList<BeanPropertyInfo> beanPropertyInfos, GenerateConfig generateConfig) {
        def prefix = ".java"
        def beanName = tableNameToFileName(generateConfig.getTableName())
        def fileName = beanName+"ServiceImpl"
        Map<String, Object> model = new HashMap<>()
        List<String> importPackages = new ArrayList<>()
        importPackages.add(generateConfig.getPackageName()+".bean."+beanName.toLowerCase()+"."+beanName) //beanPath
        importPackages.add(generateConfig.getPackageName()+".service."+beanName.toLowerCase()+"."+beanName+"Service") //ServicePath
        importPackages.add(generateConfig.getPackageName()+".mapper."+beanName.toLowerCase()+"."+beanName+"Mapper") //MapperPath
        importPackages.add(generateConfig.getPackageName()+"."+generateConfig.getPagePath())

        importPackages.add("java.util.List")
        importPackages.add("lombok.extern.log4j.Log4j") //loggerPath
        importPackages.add("org.springframework.transaction.annotation.*")
        importPackages.add("org.springframework.stereotype.Service")
        importPackages.add("com.google.common.base.Strings")
        importPackages.add("org.springframework.beans.factory.annotation.Autowired")

        //开启生成
        model.put("author", generateConfig.getAuthor())
        model.put("createDate", Const.dateFormat.format(new Date()))
//        model.put("package", generateConfig.getPackageName())
        model.put("packageName", generateConfig.getPackageName()+"."+fileName.toLowerCase())
        model.put("importPackages", importPackages)
        model.put("fileName",fileName)//${mapperName} ${mapperArgName}
        model.put("mapperName",beanName+"Mapper")
        model.put("mapperArgName",beanName[0].toLowerCase()+beanName.substring(1,beanName.length())+"Mapper")
        model.put("moduleName",generateConfig.getModuleName())
        model.put("serviceName",beanName+"Service")
        model.put("beanName",beanName)
        model.put("beanArgName",beanName[0].toLowerCase()+beanName.substring(1,beanName.length()))
        def template = configuration.getTemplate("ServiceImplTemplate.flt")
        def file = new File(getGenerateUrl().getPath() + '/service/'+beanName.toLowerCase() +'/impl/'+ fileName + prefix)
        file.getParentFile().mkdirs()
        file.createNewFile()
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        template.process(model, writer)
        writer.flush()
        writer.close()
        return file;
    }

    File generateServiceFile(ArrayList<BeanPropertyInfo> beanPropertyInfos, GenerateConfig generateConfig) {
        def prefix = ".java"
        def beanName = tableNameToFileName(generateConfig.getTableName())
        def fileName = beanName+"Service"
        Map<String, Object> model = new HashMap<>()//freamarker解析的model
        List<String> importPackages = new ArrayList<>()
        importPackages.add(generateConfig.getPackageName()+".bean."+beanName.toLowerCase()+"."+beanName)
        importPackages.add("java.util.List")
        importPackages.add(generateConfig.getPackageName()+"."+generateConfig.getPagePath())

        //填充数据
        model.put("author", generateConfig.getAuthor())
        model.put("createDate", Const.dateFormat.format(new Date()))
//        model.put("package", generateConfig.getPackageName())
        model.put("packageName", generateConfig.getPackageName()+"."+fileName.toLowerCase())
        model.put("importPackages", importPackages)
        model.put("fileName",fileName)
        model.put("moduleName",generateConfig.getModuleName())
        model.put("serviceName",beanName+"Service")
        model.put("beanName",beanName)
        model.put("beanArgName",beanName[0].toLowerCase()+beanName.substring(1,beanName.length()))
        def template = configuration.getTemplate("ServiceTemplate.flt")
        def file = new File(getGenerateUrl().getPath() + '/service/'+beanName.toLowerCase() +'/'+ fileName + prefix)
        file.getParentFile().mkdirs()
        file.createNewFile()
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        template.process(model, writer)
        writer.flush()
        writer.close()
        return file;
    }

    File generateControllerFile(ArrayList<BeanPropertyInfo> beanPropertyInfos, GenerateConfig generateConfig) {
        def prefix = ".java"
        def beanName = tableNameToFileName(generateConfig.getTableName())
        def fileName = beanName+"Controller"
        Map<String, Object> model = new HashMap<>()//freamarker解析的model
        List<String> importPackages = new ArrayList<>()
        importPackages.add(generateConfig.getPackageName()+".bean."+beanName.toLowerCase()+"."+beanName)//bean
        importPackages.add(generateConfig.getPackageName()+".service."+beanName.toLowerCase()+"."+beanName+"Service")//service
        importPackages.add(generateConfig.getPackageName()+"."+generateConfig.getPagePath())
        importPackages.add(generateConfig.getPackageName()+"."+generateConfig.getResultVoPath())
        importPackages.add(generateConfig.getPackageName()+"."+generateConfig.getBaseExceptionPath())
        importPackages.add("io.swagger.annotations.Api")
        importPackages.add("org.springframework.web.bind.annotation.*")
        importPackages.add("org.springframework.beans.factory.annotation.Autowired")
        importPackages.add("io.swagger.annotations.*")

        //填充数据
        model.put("author", generateConfig.getAuthor())
        model.put("createDate", Const.dateFormat.format(new Date()))
        model.put("package", generateConfig.getPackageName())
        model.put("packageName", generateConfig.getPackageName()+"."+fileName.toLowerCase())
        model.put("importPackages", importPackages)
        model.put("fileName",fileName)
        model.put("moduleName",generateConfig.getModuleName())
        model.put("controllerRequest",beanName)
        model.put("serviceName",beanName+"Service")
        model.put("beanName",beanName)
        model.put("beanArgName",beanName[0].toLowerCase()+beanName.substring(1,beanName.length()))
        def template = configuration.getTemplate("ControllerTemplate.flt")
        def file = new File(getGenerateUrl().getPath() + '/controller/'+beanName.toLowerCase() +'/'+ fileName + prefix)
        file.getParentFile().mkdirs()
        file.createNewFile()
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")
        template.process(model, writer)
        writer.flush()
        writer.close()
        return file;
    }

    void initSqlFile(File originalFile, List<BeanPropertyInfo> proList, GenerateConfig generateConfig) {
        def isStart;
        originalFile.eachLine({
            if (it.startsWith('CREATE TABLE')) {
                isStart = true
                def tableName = it.substring(it.indexOf("`") + 1, it.lastIndexOf("`"))
                generateConfig.setTableName(tableName)
//                fileName = tableName[2].toUpperCase() + tableName.substring(2, tableName.length())//首字符大写
            } else if (isStart) {
                def pro = parseColum(it)
                if (pro)
                    proList.add(pro)
            }
        })
    }

    File generateBeanFile(List<BeanPropertyInfo> proList,GenerateConfig generateConfig) {
        def prefix = ".java"
        def fileName = tableNameToFileName(generateConfig.getTableName())
        Map<String, Object> model = new HashMap<>()//freamarker解析的model
        List<String> importPackages = new ArrayList<>()
        generateImportPackage(importPackages,proList)
        //开启生成
        model.put("tableName",generateConfig.getTableName())
        model.put("author", generateConfig.getAuthor())
        model.put("createDate", Const.dateFormat.format(new Date()))
        model.put("packageName", generateConfig.getPackageName()+".bean."+fileName.toLowerCase())
        model.put("properties", proList)
        model.put("importPackages", importPackages)
        model.put("fileName",fileName)

        def template = configuration.getTemplate("BeanTemplate.flt")
        def file = new File(getGenerateUrl().getPath() + '/bean/'+fileName.toLowerCase() +'/'+ fileName + prefix)
        file.getParentFile().mkdirs()
        file.createNewFile()
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        template.process(model, writer)
        writer.flush()
        writer.close()
        return file;
    }

    def generateImportPackage(ArrayList<String> importPackages, List<BeanPropertyInfo> beanPropertyInfos) {
        Set<String> set =new HashSet<>()
        set.add("io.swagger.annotations.ApiModelProperty")
        beanPropertyInfos.forEach({
            switch (it.getType()){
                case "Date":
                    set.add("java.util.Date")
            }
        })
        importPackages.addAll(set)
    }

    BeanPropertyInfo parseColum(String colum) {
        if (!colum.startsWith("  `")) return null; //不是字段的行返回
        def proInfo = new BeanPropertyInfo()
        colum = colum.substring(colum.indexOf("`") + 1)
        proInfo.setColumName(colum.substring(0, colum.indexOf("`")))
        proInfo.setPropertyName(UnderscoreTocapital(proInfo.getColumName()))//驼峰转换 e.g. my_name --> myName
        colum = colum.substring(colum.indexOf(" ") + 1) //类型开始的字段
        String type
        if (colum.contains("(")) {
            type = colum.substring(0, colum.indexOf("(")) //有大小用(截取
        } else {
            type = colum.substring(0, colum.indexOf(" ")) //没有大小用第一个空格截取
        }
        switch (type) {
            case 'int': case 'integer': case 'tinyint': case 'smallint':
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
//                def sizeStr = colum.substring(colum.indexOf("(")+1).substring(colum.indexOf(")"))//大小信息的String
//                if(sizeStr.toInteger()>1)
//                    proInfo.setType("Integer")
//                else
//                    proInfo.setType("byte")
            default:
                throw new RuntimeException("未知的类型"+type)
        }
        if (colum.contains("COMMENT '")) {
            proInfo.setDescription(colum.substring(colum.indexOf("COMMENT '") + "COMMENT '".length(), colum.lastIndexOf("'")))
//提取注释 e.g. COMMENT '申请商家ID',
        }
        return proInfo
    }

    String UnderscoreTocapital(String s) {//驼峰转换 e.g. my_name --> myName
        def result = "";
        for (int i = 0; i < s.length(); i++) {
            if (s[i] != "_")
                result = result + s[i]
            else {
                i++; result = result + s[i].toUpperCase()
            }

        }
        return result
    }

//    def tableNameToFileName(String s) {//表名to文件名
//        def result = "";
//        def offset = 0;
//        if (s.startsWith("t_")) {
//            offset = 2
//        }
//        result removeUnderline(s.substring(offset))
//    }
    def tableNameToFileName(String s) {//表名to文件名
        def result = ""
        def offset = 0
        if (s.startsWith("t_")) {
            offset = 2
        }
        result = UnderscoreTocapital(s.substring(offset))
        return result[0].toUpperCase()+result.substring(1)
    }

    URL getGenerateUrl(){
        def resource = this.getClass().getClassLoader().getResource("generate")
        if(!resource) {
            new File(this.getClass().getResource("/").getPath() + "/generate").mkdir()
            resource = this.getClass().getClassLoader().getResource("generate")
        }
        return resource
    }
    def removeUnderline(String s){
        def result = ""
        for(int i = 0 ;i<s.length();i++){
            if(s[i] != '_')
                result += s[i]
        }
        return result
    }
}
