

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

<#list imports as import>
	import ${import};
</#list>

/**
* ${name}实体类
*
* @Description ${comment}
* @author ${author}
* @date ${createDate}
* @version 1.0
*/
@TableName("${name}")
public class ${prefix}${className}${suffix} extends Model<${prefix}${className}${suffix}> implements Serializable {
private static final long serialVersionUID = ${serialVersionUID};

<#list columns as column>
	/**
	* ${column.comment}  (数据库字段名:${column.name})<#if  column.defaultValue??> (默认值:${column.defaultValue}) </#if>
	*/
	<#if column.javaType == "java.util.Date">
		@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
		@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	</#if>
	<#if column.isPrimaryKey ?? && column.isPrimaryKey == true>
		@TableId(value="${column.name}", type = IdType.AUTO)
	<#else>
		@TableField("${column.name}")
	</#if>
	private ${column.javaTypeClassSimpleName} ${column.javaName};

</#list>

public ${prefix}${className}${suffix}(){}

/***
<#list columns as column>
	* @param ${column.javaName} ${column.comment}
</#list>
**/
public ${prefix}${className}${suffix}(<#list columns as column><#if column_index != 0>, </#if>${column.javaTypeClassSimpleName} ${column.javaName}</#list>){
<#list columns as column>
	this.${column.javaName} = ${column.javaName};
</#list>
}

<#list columns as column>
	<#if  column.comment?? && column.comment != ''>
		/**
		* @param ${column.javaName} ${column.comment}
		*
		* return${prefix}${className}${suffix}
		*/
	</#if>
	<#if column.javaType == "java.util.Date">
		@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	</#if>
	public ${prefix}${className}${suffix} set${column.methodName}(${column.javaTypeClassSimpleName} ${column.javaName}){
	this.${column.javaName} = ${column.javaName};
	return this;
	}

	<#if  column.comment?? && column.comment != ''>
		/**
		* @return ${column.javaTypeClassSimpleName} ${column.comment}
		*/
	</#if>
	<#if column.javaType == "java.util.Date">
		@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	</#if>
	public ${column.javaTypeClassSimpleName} get${column.methodName}(){
	return ${column.javaName};
	}

</#list>
@Override
public String toString() {
return String.format("${prefix}${className}${suffix}={<#list columns as column><#if column_index != 0>, </#if>${column.javaName} = %s</#list>}", <#list columns as column><#if column_index != 0>, </#if>${column.javaName}</#list>);
}
}