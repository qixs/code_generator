

import java.util.List;

import org.springframework.data.domain.Page;

<#if primaryKeyColumnImportClassName??>
import ${primaryKeyColumnImportClassName};
</#if>

/**
 * ${name} 表service接口
 *
 * @Description ${comment}
 * @author ${author}
 * @date ${createDate}
 * @version 1.0
 */
public interface ${prefix}${className}${suffix}{

	/***
	* 新增保存
	*
	* @param ${entityClassParameterName} ${comment}
	*
	**/
	void insert(${entityClassName} ${entityClassParameterName});
	
	/***
	* 批量新增保存
	*
	* @param ${entityClassParameterName}List ${comment}集合
	*
	**/
    void batchInsert(List<${entityClassName}> ${entityClassParameterName}List);
	
	/***
	* 更新保存
	*
	* @param ${entityClassParameterName} ${comment}
	*
	**/
    void update(${entityClassName} ${entityClassParameterName});
	
	/***
	* 批量更新保存
	*
	* @param ${entityClassParameterName}List ${comment}集合
	*
	**/
    void batchUpdate(List<${entityClassName}> ${entityClassParameterName}List);

	<#if primaryKeyColumn??>
	/***
	* 根据主键删除
	*
	* @param ${primaryKeyColumn.javaName} 主键
	*
	* @return 影响行数，如果删除成功则返回值大于0，删除失败返回值等于0
	**/
    void deleteById(${primaryKeyColumnClassName} ${primaryKeyColumn.javaName});

	</#if>
	/***
	* 根据对象删除
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return int 是否删除成功   1:成功  0:不成功
	**/
    void delete(${entityClassName} ${entityClassParameterName});

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
	* @param sort 排序字段
	* @param order 排序方式
	*
	* @return List<${entityClassName}>
	**/
	List<${entityClassName}> findList(${entityClassName} ${entityClassParameterName}, String sort, String order);

	/***
	* 根据对象查询列表(带分页)
	*
	* @param ${entityClassParameterName} ${comment}
	* @param offset 开始下标
	* @param limit 查询条数
	* @param sort 排序字段
	* @param order 排序方式
	*
	* @return Page<${entityClassName}>
	**/
	Page<${entityClassName}> findPage(${entityClassName} ${entityClassParameterName}, Integer offset, Integer limit, String sort, String order);

	/***
	* 查询总数
	*
	* @param${entityClassParameterName} ${comment}
	*
	* @return long 总条数
	**/
	long findCount(${entityClassName} ${entityClassParameterName});
	
}