

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

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
@Repository
public interface ${prefix}${className}${suffix} extends JpaRepository<${entityClassName}, ${primaryKeyColumnClassName}>,JpaSpecificationExecutor<${entityClassName}> {

}