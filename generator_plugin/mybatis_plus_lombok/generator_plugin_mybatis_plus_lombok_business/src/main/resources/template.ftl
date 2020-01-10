

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

<#if primaryKeyColumnImportClassName??>
	import ${primaryKeyColumnImportClassName};
</#if>

/**
* ${name} 表business
*
* @Description ${comment}
* @author ${author}
* @date ${createDate}
* @version 1.0
*/
@Component
public class ${prefix}${className}${suffix}{

	@Autowired
	private ${serviceInterfaceClassName} ${serviceInterfaceClassParameterName};


	/**
	* 根据对象查询列表//TODO
	*
	* @param ${entityClassParameterName} ${comment}
	* @param sort 排序字段
	* @param order 排序方式
	*
	* @return 数据列表
	**/
	public List<${className}> findList(${className} ${entityClassParameterName}, String sort, String order) {

		QueryWrapper<${className}> queryWrapper = ${entityClassParameterName} == null ? Wrappers.query() : Wrappers.query(${entityClassParameterName});
		if(StringUtils.hasLength(sort)){
			if("desc".equalsIgnoreCase(order)){
				queryWrapper.orderByDesc(sort);
			}else{
				queryWrapper.orderByAsc(sort);
			}
		}

		return ${serviceInterfaceClassParameterName}.list(queryWrapper);
	}

	/**
	* 根据对象查询列表(带分页)//TODO
	*
	* @param ${entityClassParameterName} ${comment}
	* @param offset 开始下标
	* @param limit 查询条数
	* @param sort 排序字段
	* @param order 排序方式
	*
	* @return Page对象
	**/
	public IPage<${className}> findPage(${className} ${entityClassParameterName}, Integer offset, Integer limit, String sort, String order) {

		QueryWrapper<${className}> queryWrapper = ${entityClassParameterName} == null ? Wrappers.query() : Wrappers.query(${entityClassParameterName});
		if(StringUtils.hasLength(sort)){
			if("desc".equalsIgnoreCase(order)){
				queryWrapper.orderByDesc(sort);
			}else{
				queryWrapper.orderByAsc(sort);
			}
		}

		return ${serviceInterfaceClassParameterName}.getBaseMapper().selectPage(new Page<>(offset / limit, limit), queryWrapper);
	}

	/**
	* 根据对象查询对象//TODO
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return 对象
	**/
	public ${className} find(${className} ${entityClassParameterName}) {

		return ${serviceInterfaceClassParameterName}.getOne(${entityClassParameterName} == null ? Wrappers.query() : Wrappers.query(${entityClassParameterName}));
	}

	<#if primaryKeyColumn??>
	/**
	* 根据${primaryKeyColumn.javaName}查询对象//TODO
	*
	* @param ${primaryKeyColumn.javaName}
	*
	* @return 对象
	**/
	public ${className} findById(${primaryKeyColumnClassName} ${primaryKeyColumn.javaName}) {

		return ${serviceInterfaceClassParameterName}.getById(${primaryKeyColumn.javaName});
	}
	</#if>

	/**
	* 新增保存//TODO
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return 影响行数，如果新增成功则返回值大于0，新增失败返回值等于0
	* **/
	public boolean insert(${className} ${entityClassParameterName}) {

		return ${serviceInterfaceClassParameterName}.save(${entityClassParameterName});
	}

	/**
	* 修改保存//TODO
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return 影响行数，如果新增成功则返回值大于0，修改失败返回值等于0
	* **/
	public boolean update(${className} ${entityClassParameterName}) {

		return ${serviceInterfaceClassParameterName}.updateById(${entityClassParameterName});
	}

	<#if primaryKeyColumn??>
	/**
	* 根据${primaryKeyColumn.javaName}删除对象//TODO
	*
	* @param ${primaryKeyColumn.javaName}
	*
	* @return 影响行数，如果删除成功则返回值大于0，删除失败返回值等于0
	**/
	public boolean deleteById(${primaryKeyColumnClassName} ${primaryKeyColumn.javaName}) {

		return ${serviceInterfaceClassParameterName}.removeById(${primaryKeyColumn.javaName});
	}
	</#if>
}