

import java.io.Serializable;

import lombok.*;

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
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
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
}