

import java.util.List;

<#if primaryKeyColumnImportClassName??>
import ${primaryKeyColumnImportClassName};
</#if>

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.RuntimeException;

import lombok.extern.slf4j.Slf4j;

/**
 * ${name} 表service接口实现
 *
 * @Description ${comment}
 * @author ${author}
 * @date ${createDate}
 * @version 1.0
 */
@Slf4j
@Service
public class ${prefix}${className}${suffix} implements ${serviceInterfaceClassName}{

	@Autowired
	private ${repositoryClassName} ${repositoryClassParameterName};

	@Override
	public void insert(${entityClassName} ${entityClassParameterName}){
        ${repositoryClassParameterName}.saveAndFlush(${entityClassParameterName});
	}

	@Transactional
	@Override
	public void batchInsert(List<${entityClassName}> ${entityClassParameterName}List){
		${repositoryClassParameterName}.saveAll(${entityClassParameterName}List);
	}

	@Override
	public void update(${entityClassName} ${entityClassParameterName}){
		${repositoryClassParameterName}.saveAndFlush(${entityClassParameterName});
	}

	@Transactional
	@Override
	public void batchUpdate(List<${entityClassName}> ${entityClassParameterName}List){
		${repositoryClassParameterName}.saveAll(${entityClassParameterName}List);
	}

	<#if primaryKeyColumn??>
	@Override
	public void deleteById(${primaryKeyColumnClassName} ${primaryKeyColumn.javaName}){
		${repositoryClassParameterName}.deleteById(${primaryKeyColumn.javaName});
	}

	</#if>
	@Override
	public void delete(${entityClassName} ${entityClassParameterName}){
		${repositoryClassParameterName}.delete(${entityClassParameterName});
	}

	<#if primaryKeyColumn??>
	@Override
	public ${entityClassName} findById(${primaryKeyColumnClassName} ${primaryKeyColumn.javaName}){
		${entityClassName} ${entityClassParameterName} = ${repositoryClassParameterName}.getOne(${primaryKeyColumn.javaName});
		if(${entityClassParameterName} == null){
			log.error("根据id查询失败,查询参数:[{}]", ${primaryKeyColumn.javaName});
		}
		return ${entityClassParameterName};
	}

	</#if>
	@Override
	public ${entityClassName} find(${entityClassName} ${entityClassParameterName}){
		return ${repositoryClassParameterName}.findOne(Example.of(${entityClassParameterName})).orElse(null);
	}

	@Override
	public List<${entityClassName}> findList(${entityClassName} ${entityClassParameterName}, String sort, String order){
		//校验order只能是asc或desc
		if(StringUtils.hasLength(order) && !"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)){
			throw new RuntimeException("order只能是ASC或DESC");
		}

        Sort s = Sort.unsorted();

        if(StringUtils.hasLength(sort)){
			s = Sort.by(StringUtils.hasLength(order) ? Direction.fromString(order) : Direction.ASC, sort);
        }

		return ${repositoryClassParameterName}.findAll(Example.of(${entityClassParameterName}), s);
	}

	@Override
	public Page<${entityClassName}> findPage(${entityClassName} ${entityClassParameterName},
			Integer offset, Integer limit, String sort, String order){
        //校验order只能是asc或desc
        if(StringUtils.hasLength(order) && !"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)){
            throw new RuntimeException("order只能是ASC或DESC");
        }
        //根据传过来的字段名获取到对应的表明

        Pageable pageable = Pageable.unpaged();

        if(StringUtils.hasLength(sort)){
			pageable = PageRequest.of(offset / limit, limit, Sort.by(StringUtils.hasLength(order) ? Direction.fromString(order) : Direction.ASC, sort));
        }else{
            pageable = PageRequest.of(offset / limit, limit);
        }

        return ${repositoryClassParameterName}.findAll(Example.of(${entityClassParameterName}), pageable);
	}

	@Override
	public long findCount(${entityClassName} ${entityClassParameterName}){
		return ${repositoryClassParameterName}.count(Example.of(${entityClassParameterName}));
	}
}