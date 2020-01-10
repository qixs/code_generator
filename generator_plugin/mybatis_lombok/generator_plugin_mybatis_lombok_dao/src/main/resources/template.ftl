

import java.util.List;
import java.util.Map;
<#if primaryKeyColumnImportClassName??>

import ${primaryKeyColumnImportClassName};
</#if>

/**
 * ${name} 表数据接口
 *
 * @Description ${comment}
 * @author ${author}
 * @date ${createDate}
 * @version 1.0
 */
public interface ${prefix}${className}${suffix}{
	/***
	* 新增
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return 影响行数，如果新增成功则返回值大于0，新增失败返回值等于0
	**/
	int insert(${entityClassName} ${entityClassParameterName});
	
	/***
	* 批量新增
	*
	* @param ${entityClassParameterName}List ${comment}集合
	*
	* @return int 插入成功条数
	**/
	int batchInsert(List<${entityClassName}> ${entityClassParameterName}List);
	
	/***
	* 更新
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return 影响行数，如果更新成功则返回值大于0，更新失败返回值等于0
	**/
	int update(${entityClassName} ${entityClassParameterName});
	
	/***
	* 批量更新
	*
	* @param ${entityClassParameterName}List ${comment}集合
	*
	* @return int 更新成功条数
	**/
	int batchUpdate(List<${entityClassName}> ${entityClassParameterName}List);

	<#if primaryKeyColumn??>
	/***
	* 根据主键删除
	*
	* @param ${primaryKeyColumn.javaName} 主键
	*
	* @return 影响行数，如果删除成功则返回值大于0，删除失败返回值等于0
	**/
	int deleteById(${primaryKeyColumnClassName} ${primaryKeyColumn.javaName});

	</#if>
	/***
	* 根据对象删除
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return 影响行数，如果删除成功则返回值大于0，删除失败返回值等于0
	**/
	int delete(${entityClassName} ${entityClassParameterName});

	<#if primaryKeyColumn??>
	/***
	* 根据主键获取对象
	*
	* @param ${primaryKeyColumn.javaName} 主键
	*
	* @return ${entityClassName} 对象
	**/
	${entityClassName} findById(${primaryKeyColumnClassName} ${primaryKeyColumn.javaName});

	</#if>
	/***
	* 根据对象查询
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return ${entityClassName}
	**/
	${entityClassName} find(${entityClassName} ${entityClassParameterName});
	
	/***
	* 根据对象查询列表
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return List<${entityClassName}>
	**/
	List<${entityClassName}> findList(${entityClassName} ${entityClassParameterName});

	/***
	* 根据map查询列表
	*
	* @param conditions 查询条件列表
	*
	* @return List<${entityClassName}>
	**/
	List<${entityClassName}> findListByMap(Map<String, Object> conditions);
	
	/***
	* 查询总数
	*
	* @param${entityClassParameterName} ${comment}
	*
	* @return long 总条数
	**/
	long findCount(${entityClassName} ${entityClassParameterName});
	
}