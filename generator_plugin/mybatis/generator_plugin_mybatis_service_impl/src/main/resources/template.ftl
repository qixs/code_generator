

import java.util.List;

<#if primaryKeyColumnImportClassName??>
import ${primaryKeyColumnImportClassName};
</#if>

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.beans.PropertyDescriptor;
import java.lang.RuntimeException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * ${name} 表service接口实现
 *
 * @Description ${comment}
 * @author ${author}
 * @date ${createDate}
 * @version 1.0
 */
@Service
public class ${prefix}${className}${suffix} implements ${serviceInterfaceClassName}{

	//数据库字段名支持的字符
	private static final Pattern COLUMN_PATTERN = Pattern.compile("^[0-9a-zA-Z_$]+$");

	private transient Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ${daoClassName} ${daoClassParameterName};

	@Override
	public int insert(${entityClassName} ${entityClassParameterName}){
		int i = ${daoClassParameterName}.insert(${entityClassParameterName});
		if(i <= 0){
			logger.error("插入失败,插入参数:[{}]", ${entityClassParameterName});
			throw new RuntimeException("插入失败");
		}
		return i;
	}

	@Transactional
	@Override
	public int batchInsert(List<${entityClassName}> ${entityClassParameterName}List){
		int i = ${daoClassParameterName}.batchInsert(${entityClassParameterName}List);
		if(i < ${entityClassParameterName}List.size()){
			logger.error("批量插入失败,插入参数:[{}]", ${entityClassParameterName}List);
			throw new RuntimeException("批量插入失败");
		}
		return i;
	}

	@Override
	public int update(${entityClassName} ${entityClassParameterName}){
		int i = ${daoClassParameterName}.update(${entityClassParameterName});
		if(i <= 0){
			logger.error("更新失败,更新参数:[{}]", ${entityClassParameterName});
		}
		return i;
	}

	@Transactional
	@Override
	public int batchUpdate(List<${entityClassName}> ${entityClassParameterName}List){
		int i = ${daoClassParameterName}.batchUpdate(${entityClassParameterName}List);
		if(i < ${entityClassParameterName}List.size()){
			logger.error("批量更新失败,更新参数:[{}]", ${entityClassParameterName}List);
			throw new RuntimeException("批量更新失败");
		}
		return i;
	}

	<#if primaryKeyColumn??>
	@Override
	public int deleteById(${primaryKeyColumnClassName} ${primaryKeyColumn.javaName}){
		int i = ${daoClassParameterName}.deleteById(${primaryKeyColumn.javaName});
		if(i <= 0){
			logger.error("删除失败,删除参数:[{}]", ${primaryKeyColumn.javaName});
		}
		return i;
	}

	</#if>
	@Override
	public int delete(${entityClassName} ${entityClassParameterName}){
		int i = ${daoClassParameterName}.delete(${entityClassParameterName});
		logger.debug("删除[{}]条,删除参数:[{}]", i, ${entityClassParameterName});
		return i;
	}

	<#if primaryKeyColumn??>
	@Override
	public ${entityClassName} findById(${primaryKeyColumnClassName} ${primaryKeyColumn.javaName}){
		${entityClassName} ${entityClassParameterName} = ${daoClassParameterName}.findById(${primaryKeyColumn.javaName});
		if(${entityClassParameterName} == null){
			logger.error("根据id查询失败,查询参数:[{}]", ${primaryKeyColumn.javaName});
		}
		return ${entityClassParameterName};
	}

	</#if>
	@Override
	public ${entityClassName} find(${entityClassName} ${entityClassParameterName}){
		return ${daoClassParameterName}.find(${entityClassParameterName});
	}

	@Override
	public List<${entityClassName}> findList(${entityClassName} ${entityClassParameterName}, String sort, String order){
		//校验order只能是asc或desc
		if(StringUtils.hasLength(order) && !"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)){
			throw new RuntimeException("order只能是ASC或DESC");
		}
		//根据传过来的字段名获取到对应的表明
		String sortColumn = getSortColumn(sort);

		//查询条件
		Map<String, Object> conditions = castToMap(${entityClassParameterName});
		conditions.put("sortColumn", sortColumn);
		conditions.put("order", order);

		return ${daoClassParameterName}.findListByMap(conditions);
	}

	@Override
	public Page<${entityClassName}> findPage(${entityClassName} ${entityClassParameterName},
			Integer offset, Integer limit, String sort, String order){
		//校验order只能是asc或desc
		if(StringUtils.hasLength(order) && !"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)){
			throw new RuntimeException("order只能是ASC或DESC");
		}

		//根据传过来的字段名获取到对应的表明
		String sortColumn = getSortColumn(sort);

		//查询条件
		Map<String, Object> conditions = castToMap(${entityClassParameterName});
		conditions.put("offset", offset);
		conditions.put("limit", limit);
		conditions.put("sortColumn", sortColumn);
		conditions.put("order", order);

		List<${entityClassName}> list = ${daoClassParameterName}.findListByMap(conditions);

		long total = findCount(${entityClassParameterName});

		Sort s = Sort.unsorted();
		if(StringUtils.hasLength(sortColumn)) {
			s = new Sort(Direction.fromOptionalString(order).orElse(Direction.ASC), sortColumn);
		}

		Pageable pageable = PageRequest.of(offset / limit, limit, s);

		return new PageImpl<>(list, pageable, total);
	}

	@Override
	public long findCount(${entityClassName} ${entityClassParameterName}){
		return ${daoClassParameterName}.findCount(${entityClassParameterName});
	}

	/**
	* 把对象转为map
	* @param obj 要转换的对象
	* @return
	**/
	private Map<String, Object> castToMap(Object obj){
		if(obj == null){
			return Collections.emptyMap();
		}

		Map<String, Object> map = new HashMap<>();

		Class<?> clazz = obj.getClass();
		do{
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields){
				String fieldName = field.getName();
				try{
					PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);

					map.put(fieldName, pd.getReadMethod().invoke(obj));
				}catch (Exception e){
					logger.debug("读取字段的值出错，对象：[{}] 字段名：[{}]", obj, fieldName);
				}
			}
			clazz = clazz.getSuperclass();
		}while(clazz != null && !Object.class.equals(clazz));

		return map;
	}

	/**
	* 根据前台传过来的字段名获取对应的数据库列名
	*
	* @param sortField 前台传过来的字段名
	*
	* @return 数据库列名
	**/
	private String getSortColumn(String sortField){
		if(!StringUtils.hasLength(sortField)){
			return null;
		}
		//校验字段名是否在配置中
		String columnName = JAVA_FIELD_TABLE_COLUMN_MAPPING.get(sortField);
		if(columnName != null){
			return columnName;
		}

		//如果字段名不在配置中校验字段名是否合法(只包含字母数字下划线及$)
		if(!COLUMN_PATTERN.matcher(sortField).matches()){
			throw new RuntimeException("字段名不合法，字段名只支持英文字母、数字、下划线和$");
		}
		//字段名不在配置中且合法则直接返回即可，不会存在sql注入问题
		return sortField;
	}
	private static final Map<String, String> JAVA_FIELD_TABLE_COLUMN_MAPPING;
	static{
		Map<String, String> map = new HashMap<>();
		<#list columns as column>
			map.put("${column.javaName}", "${column.name}");
		</#list>

		JAVA_FIELD_TABLE_COLUMN_MAPPING = Collections.unmodifiableMap(map);
	}
}