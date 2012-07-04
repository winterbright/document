//freemarker的模版文件以.ftl结尾,velocity的模版文件以.vm结尾

//freemarker
//1、定义配置文件
Configuration cfg = new Configuration();  
//2、加载模版路径
cfg.setDirectoryForTemplateLoading(new File("E:/ma"));  
//指定模版如何查看数据模型**
cfg.setObjectWrapper(new DefaultObjectWrapper());  
//缓存
cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));  
//3、加载模版
Template temp = cfg.getTemplate("example.ftl");  
//4、定义输出
Writer out = new OutputStreamWriter(System.out);  
//5、合并模版与数据模型（数据模型：context）
temp.process(context, out);  
out.flush(); 

//1、初始化
Velocity.init();  
//2、加载数据模型
VelocityContext context = new  VelocityContext(root);  
//3、加载模版 
Template template = Velocity.getTemplate("html/example.vm");  
//4、定义输出
StringWriter sw = new  StringWriter();  
//5、合并模版与数据模型
template.merge(context, sw);  
System.out.print(sw.toString()); 

string： ${name}--${number}--${user.age}

condition(freemarker)：	
<#if  user ==  "Big Joe" >  
list iterator-----------  
<#list list as aa>  
${aa}  
</#list>   
</#if > 


condition(velocity)：	
#if  ($user ==  "Big Joe" )  
list iterator-----------  
#foreach( $aa in $list )  
$aa  
#end  
#end


maven:
    <dependency>
      <groupId>org.freemarker</groupId>
      <artifactId>freemarker</artifactId>
      <version>2.3.19</version>
    </dependency>
    <dependency>
      <groupId>org.apache.velocity</groupId>
      <artifactId>velocity</artifactId>
      <version>1.7</version>
    </dependency>