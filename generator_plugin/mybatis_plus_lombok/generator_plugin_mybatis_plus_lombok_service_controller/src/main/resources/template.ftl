

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

<#if primaryKeyColumnImportClassName??>
	import ${primaryKeyColumnImportClassName};
</#if>

/**
* ${name} 表controller
*
* @Description ${comment}
* @author ${author}
* @date ${createDate}
* @version 1.0
*/
@Controller
@RequestMapping("/${entityClassParameterName}")
public class ${prefix}${className}${suffix}{

	@InitBinder("${entityClassParameterName}")
	public void initBinder${className}(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("${entityClassParameterName}.");
	}

	@Autowired
	private ${serviceInterfaceClassName} ${serviceInterfaceClassParameterName};

	/**
	* 首页//TODO
	*
	* @param model
	*
	* @return 页面模板地址
	* **/
//	@GetMapping("/index")
	public String index(Model model) {
		//TODO
		return "index";
	}

	/**
	* 根据对象查询列表//TODO
	*
	* @param ${entityClassParameterName} ${comment}
	* @param sort 排序字段
	* @param order 排序方式
	*
	* @return 数据列表
	**/
//	@GetMapping("/findList")
	@ResponseBody
	public List<${className}> findList(${className} ${entityClassParameterName},
		@RequestParam(value = "sort", required = false)String sort, @RequestParam(value = "order", required = false)String order) {

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
//	@GetMapping("/findPage")
	@ResponseBody
	public IPage<${className}> findPage(${className} ${entityClassParameterName},
		@RequestParam("offset")Integer offset, @RequestParam("limit")Integer limit,
		@RequestParam(value = "sort", required = false)String sort, @RequestParam(value = "order", required = false)String order) {

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
//	@GetMapping("/find")
	@ResponseBody
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
//	@GetMapping("/findById/{${primaryKeyColumn.javaName}}")
	@ResponseBody
	public ${className} findById(@PathVariable("${primaryKeyColumn.javaName}") ${primaryKeyColumnClassName} ${primaryKeyColumn.javaName}) {

		return ${serviceInterfaceClassParameterName}.getById(${primaryKeyColumn.javaName});
	}
</#if>

	/**
	* 新增//TODO
	*
	* @return 新增页面模板地址
	**/
//	@GetMapping("/editNew")
	public String editNew(Model model) {
		//TODO
		return "add";
	}

<#if primaryKeyColumn??>
	/**
	* 修改//TODO
	*
	* @param ${primaryKeyColumn.javaName}
	* @param model
	*
	* @return 编辑页面模板地址
	**/
//	@GetMapping("/edit/{${primaryKeyColumn.javaName}}")
	public String edit(@PathVariable("${primaryKeyColumn.javaName}") ${primaryKeyColumnClassName} ${primaryKeyColumn.javaName}, Model model) {
		//TODO

		model.addAttribute("${entityClassParameterName}", ${serviceInterfaceClassParameterName}.getById(${primaryKeyColumn.javaName}));
		return "edit";
	}
<#else>
	/**
	* 修改//TODO
	*
	* @param id
	* @param model
	*
	* @return 编辑页面模板地址
	**/
//	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Object id, Model model) {
		//TODO

		return "edit";
	}
</#if>

	/**
	* 新增保存//TODO
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return 影响行数，如果新增成功则返回值大于0，新增失败返回值等于0
	* **/
//	@PostMapping
	@ResponseBody
	public boolean create(@Validated ${className} ${entityClassParameterName}) {

		return ${serviceInterfaceClassParameterName}.save(${entityClassParameterName});
	}

	/**
	* 修改保存//TODO
	*
	* @param ${entityClassParameterName} ${comment}
	*
	* @return 影响行数，如果新增成功则返回值大于0，修改失败返回值等于0
	* **/
//	@PutMapping
	@ResponseBody
	public boolean update(@Validated ${className} ${entityClassParameterName}) {

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
//	@DeleteMapping("/{${primaryKeyColumn.javaName}}")
	@ResponseBody
	public boolean deleteById(@PathVariable("${primaryKeyColumn.javaName}") ${primaryKeyColumnClassName} ${primaryKeyColumn.javaName}) {

		return ${serviceInterfaceClassParameterName}.removeById(${primaryKeyColumn.javaName});
	}
</#if>
}