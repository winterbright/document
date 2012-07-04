//freemarker��ģ���ļ���.ftl��β,velocity��ģ���ļ���.vm��β

//freemarker
//1�����������ļ�
Configuration cfg = new Configuration();  
//2������ģ��·��
cfg.setDirectoryForTemplateLoading(new File("E:/ma"));  
//ָ��ģ����β鿴����ģ��**
cfg.setObjectWrapper(new DefaultObjectWrapper());  
//����
cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));  
//3������ģ��
Template temp = cfg.getTemplate("example.ftl");  
//4���������
Writer out = new OutputStreamWriter(System.out);  
//5���ϲ�ģ��������ģ�ͣ�����ģ�ͣ�context��
temp.process(context, out);  
out.flush(); 

//1����ʼ��
Velocity.init();  
//2����������ģ��
VelocityContext context = new  VelocityContext(root);  
//3������ģ�� 
Template template = Velocity.getTemplate("html/example.vm");  
//4���������
StringWriter sw = new  StringWriter();  
//5���ϲ�ģ��������ģ��
template.merge(context, sw);  
System.out.print(sw.toString()); 

string�� ${name}--${number}--${user.age}

condition(freemarker)��	
<#if  user ==  "Big Joe" >  
list iterator-----------  
<#list list as aa>  
${aa}  
</#list>   
</#if > 


condition(velocity)��	
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