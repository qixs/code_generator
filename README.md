## 代码生成器

### 一、用法介绍：
1. 首先执行打包命令：mvn clean package
2. 执行启动命令：java -jar generator_web-0.0.1-SNAPSHOT.jar

___注：源码启动类：com.qxs.generator.web.GeneratorApplication，直接使用GeneratorApplication启动也需要执行打包命令：mvn clean package，打包命令会自动部署所有的代码生成插件___

### 二、自定义插件
1. 新建插件工程，在resources目录下新建plugin.yml文件（必须项），配置如下：
```
plugin: 
  groupName: jpa                #插件组名
  name: entity                  #插件名
  description: 实体类生成插件     #插件描述
  templatePath: template.ftl    #插件模板名（必须在resources目录下）
  generator: com.qxs.plugin.generator.jpa.entity.JpaEntityGenerator   #插件生成器（用于组装模板所需数据）
  fileRelativeDir: jpa/entity   #生成的代码在zip包中为位置
  fileSuffix: .java             #代码文件后缀名
  prefix:                       #类名前缀
  suffix:                       #类名后缀
  dependencies:                 #依赖的插件,以英文逗号(,)分割
```
2. 编写代码生成器，代码生成器实现com.qxs.plugin.factory.generator.IGenerator接口（可以继承com.qxs.plugin.factory.generator.AbstractGenerator类）
3. 打包发布上传到代码生成器系统（管理员用户登录之后插件管理菜单）或者放到代码生成器所在目录下的plugins目录下重启服务器

___注：参考code_generator/generator_plugin下的插件___

### 三、帮助手册
参见wiki