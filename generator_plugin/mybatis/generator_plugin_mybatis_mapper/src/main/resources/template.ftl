<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace=".${daoClassName}">
	<resultMap type="${entityClassParameterName}" id="${entityClassParameterName}Map">
		<#if  primaryKeyColumn??>
		<id column="${primaryKeyColumn.name}" property="${primaryKeyColumn.javaName}"<#if primaryKeyColumn.mapperJdbcType??> jdbcType="${primaryKeyColumn.mapperJdbcType}"</#if>/><!-- ${primaryKeyColumn.comment} -->
		</#if>
		<#list columns as column>
		<#if  !column.isPrimaryKey>
		<result column="${column.name}" property="${column.javaName}"<#if column.mapperJdbcType??> jdbcType="${column.mapperJdbcType}"</#if>/><!-- ${column.comment} -->
		</#if>
	    </#list>
	</resultMap>

    <!-- 新增 -->
    <insert id="insert" parameterType="${entityClassParameterName}" <#if generatePrimaryKey == 'useGeneratedKeys'> useGeneratedKeys="true" keyProperty="${primaryKeyColumn.javaName}"</#if>>
    <#if generatePrimaryKey == 'selectKey'>
		<selectKey keyProperty="${primaryKeyColumn.javaName}" resultType="${primaryKeyColumn.javaType}" <#if selectKeyMode?? >order="${selectKeyMode}"</#if>>
        <![CDATA[
            <#if selectKeySql?? >${selectKeySql}</#if>
        ]]>
        </selectKey>
    </#if>
    <![CDATA[
        ${insert}
    ]]>
    </insert>

    <!-- 批量新增 -->
    <insert id="batchInsert" parameterType="java.util.List" useGeneratedKeys="false">
    <#if !batchInsertMode?? || batchInsertMode == 'VALUES'>
        ${batchInsertIntoSql} values
		<foreach collection="list" item="item" index="index" separator="," >
            ${batchInsertValuesSql}
        </foreach>
    </#if>
    <#if batchInsertMode ?? && batchInsertMode == 'UNION'>
        ${batchInsertIntoSql}
        <#if batchInsertSelectKeySql?? >${batchInsertSelectKeySql}</#if> as ${primaryKeyColumn.name}, t.*
        FROM(
        <foreach collection="list" item="item" index="index" separator="UNION ALL">
            SELECT
                ${batchInsertValuesSql}
        </foreach>
        ) t
    </#if>
    </insert>

    <!-- 更新 -->
    <update id="update" parameterType="${entityClassParameterName}">
    <![CDATA[
	    ${update}
    ]]>
    </update>

    <!-- 批量更新 -->
    <update id="batchUpdate" parameterType="java.util.List">
        update ${name}
        <trim prefix="set" suffixOverrides=",">
            <#list  columns as  column>
            <#if !column.isPrimaryKey>
            <trim prefix="${column.name} = case" suffix="end,">
                <foreach collection="list" item="item" index="index">
                    <if test="item.${column.javaName} != null">
                        when ${name}.${primaryKeyColumn.name} = #\{item.${primaryKeyColumn.javaName}} then #\{item.${column.javaName}<#if column.mapperJdbcType??>,jdbcType=${column.mapperJdbcType}</#if>}
                    </if>
                </foreach>
            </trim>
            </#if>
            </#list>
        </trim>
        where ${primaryKeyColumn.name} in
        <foreach collection="list" index="index" item="item" open="(" separator="," close=")">
        	#\{item.${primaryKeyColumn.javaName},jdbcType=${primaryKeyColumn.mapperJdbcType}}
        </foreach>
    </update>

    <!-- 删除 -->
    <delete id="deleteById" parameterType="${primaryKeyColumn.javaType}">
    <![CDATA[
        delete from ${name} where ${primaryKeyColumn.name} = #\{${primaryKeyColumn.javaName}<#if primaryKeyColumn.mapperJdbcType??>,jdbcType=${primaryKeyColumn.mapperJdbcType}</#if>}
    ]]>
    </delete>

    <!-- 根据对象删除 -->
    <delete id="delete" parameterType="${entityClassParameterName}">
        delete from ${name}
        <include refid="whereCondition"/>
    </delete>

    <!-- 根据id查询 -->
    <select id="findById" parameterType="${primaryKeyColumn.javaType}" resultMap="${entityClassParameterName}Map" >
    <![CDATA[
	    ${selectSql}
        where ${name}.${primaryKeyColumn.name} = #\{${primaryKeyColumn.javaName}<#if primaryKeyColumn.mapperJdbcType??>,jdbcType=${primaryKeyColumn.mapperJdbcType}</#if>}
    ]]>
    </select>

    <!-- 根据对象查询 -->
    <select id="find" parameterType="${entityClassParameterName}" resultMap="${entityClassParameterName}Map" >
    <![CDATA[
	    ${selectSql}
    ]]>
        <include refid="whereCondition"/>
    </select>

    <!-- 根据对象查询对象列表 -->
    <select id="findList" parameterType="${entityClassParameterName}" resultMap="${entityClassParameterName}Map" >
    <![CDATA[
	    ${selectSql}
    ]]>
        <include refid="whereCondition"/>
    </select>

    <!-- 根据map查询对象列表 -->
    <select id="findListByMap" parameterType="map" resultMap="${entityClassParameterName}Map" >
        <![CDATA[
        ${selectSql}
        ]]>
        <include refid="whereCondition"/>
        <if test="sortColumn != null and sortColumn != ''">
            order by ${sortColumn} <if test="order != null and order != ''"> ${order}</if>
        </if>
        <if test="limit != null">
            <!--暂时只支持mysql分页-->
            limit <if test="limit != null">${offset},</if>${limit}
        </if>
    </select>

    <!-- 根据对象查询对象列表总数 -->
    <select id="findCount" parameterType="${entityClassParameterName}" resultType="long">
    <![CDATA[
        select count(*) from ${name}
    ]]>
        <include refid="whereCondition"/>
    </select>

    <!-- 公用的where条件 -->
    <sql id="whereCondition">
        <where>
	    	 <#list  columns as  column>
             <if test="${column.javaName} != null<#if column.javaType = 'java.lang.String'> and ${column.javaName} != ''</#if>">
             <![CDATA[
                 AND ${name}.${column.name} = #\{${column.javaName}<#if column.mapperJdbcType??>,jdbcType=${column.mapperJdbcType}</#if>}
             ]]>
             </if>
			 </#list>
        </where>
    </sql>
</mapper>
