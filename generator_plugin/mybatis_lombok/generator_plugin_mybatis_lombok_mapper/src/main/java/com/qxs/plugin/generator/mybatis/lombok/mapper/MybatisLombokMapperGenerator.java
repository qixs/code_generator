package com.qxs.plugin.generator.mybatis.lombok.mapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.reader.UnicodeReader;

import com.google.common.collect.Lists;
import com.qxs.base.database.config.MybatisJdbcTypeConfig;
import com.qxs.base.formatter.jsqlparser.CustomJdbcNamedParameter;
import com.qxs.base.formatter.jsqlparser.InsertFormatter;
import com.qxs.base.formatter.jsqlparser.JSQLParserFormatter;
import com.qxs.base.model.Column;
import com.qxs.base.model.Table;
import com.qxs.plugin.factory.exception.CodeGenerateException;
import com.qxs.plugin.factory.exception.PluginNotFoundException;
import com.qxs.plugin.factory.generator.AbstractGenerator;
import com.qxs.plugin.factory.generator.IGenerator;
import com.qxs.plugin.factory.model.PluginConfig;

import freemarker.core.InvalidReferenceException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import jodd.io.UnicodeInputStream;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;

/**
 * mapper代码生成器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-21
 * @version Revision: 1.0
 */
public class MybatisLombokMapperGenerator extends AbstractGenerator implements IGenerator {

    private static final String ENTITY_PLUGIN_NAME = "entity";
    private static final String DAO_PLUGIN_NAME = "dao";

    @Override
    protected ByteArrayOutputStream generator(InputStream templateStream, Object dataModel) throws IOException, TemplateException {
        ByteArrayOutputStream outputStream = super.generator(templateStream, dataModel);
        //替换所有的#\{为#{
        String code = new String(outputStream.toByteArray(),"UTF-8");
        logger.debug("生成的代码:[{}]",code);
        code = code.replaceAll("#\\\\","#");
        logger.debug("替换之后的代码:[{}]",code);
        byte[] bytes = code.getBytes("UTF-8");
        outputStream = new ByteArrayOutputStream();
        outputStream.write(bytes);
        return outputStream;
    }

	@Override
	protected String formatFileName(String fileName) {
		return fileName.substring(0, 1).toLowerCase() + fileName.substring(1);
	}

	@Override
    protected TemplateModel wrap(PluginConfig[] allPluginConfigs,PluginConfig pluginConfig, Table table, String[] removePrefixs)
            throws TemplateModelException {
        Assert.notNull(table.getPrimaryKeyColumn(),table.getName() + "表必须有主键");

        BeansWrapper wrapper = beansWrapper();
        Map<String, Object> map = beanToMap(table);
        
        //数据库类型
        String databaseType = getDatabaseType();

        List<Column> columns = table.getColumns();
        for(Column column : columns) {
            //设置java字段名
            column.setJavaName(formatName(column.getName(), removePrefixs));
            
            column.setMapperJdbcType(MybatisJdbcTypeConfig.getJavaType(databaseType, column.getJdbcType()));
        }

        String className = formatName(table.getName(), removePrefixs);
        className = className.substring(0, 1).toUpperCase() + className.substring(1);

        //dao插件配置
        String daoClassName = className;
        
        PluginConfig daoPluginConfig = getPluginConfig(allPluginConfigs, pluginConfig.getGroupName(), DAO_PLUGIN_NAME);
        if(null == daoPluginConfig) {
			throw new PluginNotFoundException("插件["+DAO_PLUGIN_NAME+"]未找到");
		}
        daoClassName = daoPluginConfig.getPrefix() + daoClassName + daoPluginConfig.getSuffix();
        
        map.put("daoClassName", daoClassName);

        // entity插件配置
        String entityClassName = className;
        
        PluginConfig entityPluginConfig = getPluginConfig(allPluginConfigs, pluginConfig.getGroupName(), ENTITY_PLUGIN_NAME);
        if(null == entityPluginConfig) {
			throw new PluginNotFoundException("插件["+ENTITY_PLUGIN_NAME+"]未找到");
		}
        entityClassName = entityPluginConfig.getPrefix() + entityClassName + entityPluginConfig.getSuffix();
        
        map.put("entityClassParameterName", entityClassName.substring(0, 1).toLowerCase() + entityClassName.substring(1));

        boolean ignorePrimaryKeyColumn = getIsIgnorePrimaryKeyColumn(table,databaseType);
        map.put("ignorePrimaryKeyColumn", ignorePrimaryKeyColumn);
        //insert
        Insert insert = createInsert(table,ignorePrimaryKeyColumn);
        String insertSql = JSQLParserFormatter.format(insert);
        map.put("insert", formatInsertSql(insertSql));

        //insert主键生成方式,selectKey或useGeneratedKeys
        //如果生成insert时忽略主键(包括oracle和postgresql必然使用selectKey方式,mysql如果不自动生成主键则也使用selectKey方式)则使用selectKey方式,否则使用useGeneratedKeys方式
        map.put("generatePrimaryKey", ignorePrimaryKeyColumn ? "useGeneratedKeys" : "selectKey");

        InputStream inputStream = readFile(pluginConfig,"dialect/" + databaseType + "/insert.xml");
        if(inputStream != null){
            logger.debug("读取[dialect/{}/insert.xml]文件",databaseType);

            ByteArrayOutputStream insertTemplateOutputStream = readTemplate(inputStream,table);
            ByteArrayInputStream insertTemplateInputStream = new ByteArrayInputStream(insertTemplateOutputStream.toByteArray());
            Document document = getDocument(insertTemplateInputStream);
            XPath xPath = XPathFactory.newInstance().newXPath();
            try{
                Node selectKeyModeNode = ((NodeList) xPath.evaluate("/insert/selectKeyMode", document, XPathConstants.NODESET)).item(0);

                Node selectKeySqlNode = ((NodeList) xPath.evaluate("/insert/selectKeySql", document, XPathConstants.NODESET)).item(0);

                Node batchInsertModeNode = ((NodeList) xPath.evaluate("/insert/batchInsertMode", document, XPathConstants.NODESET)).item(0);

                Node batchInsertSelectKeySqlNode = ((NodeList) xPath.evaluate("/insert/batchInsertSelectKeySql", document, XPathConstants.NODESET)).item(0);
                
                //selectKey方式生成主键方式
                if(selectKeyModeNode != null) {
                	map.put("selectKeyMode",selectKeyModeNode.getTextContent().toUpperCase());
                }
                
                if(selectKeySqlNode != null) {
                	 //sql
                    String sql = selectKeySqlNode.getTextContent();
                    map.put("selectKeySql",sql);
                }
               
                if(batchInsertModeNode != null) {
                	//批量插入配置   VALUES 或 UNION
                    map.put("batchInsertMode",batchInsertModeNode.getTextContent().toUpperCase());
                }
                
                if(batchInsertSelectKeySqlNode != null) {
                	map.put("batchInsertSelectKeySql",batchInsertSelectKeySqlNode.getTextContent());
                }
            }catch (XPathExpressionException e) {
                logger.error("[dialect/{}/insert.xml]文件读取失败",databaseType,e);
                throw new RuntimeException(e);
            }finally{
                try {
                    if(insertTemplateInputStream != null){
                        insertTemplateInputStream.close();
                    }
                    if(insertTemplateOutputStream != null){
                        insertTemplateOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        //批量插入sql
        Insert batchIntoInsert = createInsert(table,ignorePrimaryKeyColumn);
        batchIntoInsert.setItemsList(null);
        batchIntoInsert.setUseValues(false);
        batchIntoInsert.setUseSelectBrackets(false);
        String batchInsertIntoSql = JSQLParserFormatter.format(batchIntoInsert);
        String[] batchInsertIntoSqls = batchInsertIntoSql.split("\r\n");
        StringBuilder batchInsertIntoSqlSb = new StringBuilder();
        for(int i = 0, length = batchInsertIntoSqls.length ; i < length ; i ++) {
        	if(i > 0) {
        		batchInsertIntoSqlSb.append("\r\n\t\t");
        	}
        	batchInsertIntoSqlSb.append(batchInsertIntoSqls[i]);
        }
        StringBuilder batchInsertValuesSql = new StringBuilder();

        int  maxTransverseColumnSize = InsertFormatter.MAX_TRANSVERSE_COLUMN_SIZE;
        String rn = "";
        int columnSize = columns.size();
        columnSize = ignorePrimaryKeyColumn && table.getPrimaryKeyColumn() != null ? columnSize - 1 : columnSize;
        if(columnSize > maxTransverseColumnSize){
            rn = "\r\n";
        }

        if("VALUES".equals(map.get("batchInsertMode"))){
            batchInsertValuesSql.append("(\r\n");
            for(int i = 0 , length = columns.size() ; i < length ; i ++){
                Column column = columns.get(i);
                //忽略主键字段
                if(ignorePrimaryKeyColumn && column.getIsPrimaryKey()){
                    continue;
                }
                //如果是主键字段则value的值为batchInsertSelectKeySql
                if(column.getIsPrimaryKey() && map.get("batchInsertSelectKeySql") != null && StringUtils.hasLength(map.get("batchInsertSelectKeySql").toString().trim())){
                    batchInsertValuesSql.append("\t\t\t").append(map.get("batchInsertSelectKeySql").toString().trim());
                }else{
                    batchInsertValuesSql.append("\t\t\t#{item." + column.getJavaName() + (StringUtils.hasLength(column.getMapperJdbcType()) ? ",jdbcType=" + column.getMapperJdbcType() : "") + "}");
                }

                if(i < length - 1){
                    batchInsertValuesSql.append(",");
                }
                batchInsertValuesSql.append(rn);
                if(i == length - 1){
                    batchInsertValuesSql.append("\t\t\t)");
                }
            }
        }else{
            for(int i = 0 , length = columns.size() ; i < length ; i ++){
                Column column = columns.get(i);
                //忽略主键
                if(column.getIsPrimaryKey()) {
                	continue;
                }
                if(i > 0) {
                	batchInsertValuesSql.append("\t\t\t\t");
                }
                batchInsertValuesSql.append("#{item." + column.getJavaName() + (StringUtils.hasLength(column.getMapperJdbcType()) ? ",jdbcType=" + column.getMapperJdbcType() : "") + "} " + column.getName());
                if(i < length - 1){
                    batchInsertValuesSql.append(",");
                }
                batchInsertValuesSql.append(rn);
            }

            //要判断数据库类型
            //TODO
            if("oracle".equals(databaseType)){
                batchInsertValuesSql.append("\r\n\t\t\tfrom dual");
            }
        }

        map.put("batchInsertIntoSql",batchInsertIntoSqlSb.toString());
        map.put("batchInsertValuesSql",batchInsertValuesSql.toString());

        Update update = createUpdate(table);
        String updateSql = JSQLParserFormatter.format(update);
        String[] updateSqls = updateSql.split("\r\n");
        StringBuilder updateSqlSb = new StringBuilder();
        for(int i = 0, length = updateSqls.length ; i < length ; i ++) {
//            String tmp = updateSqls[i].toLowerCase().trim();
        	if(i > 0) {
                updateSqlSb.append("\r\n\t\t");
        	}
        	updateSqlSb.append(updateSqls[i]);
        }
        map.put("update",updateSqlSb.toString());

        //查询sql
        Select select = createSelect(table);
        String selectSql = JSQLParserFormatter.format(select);
        String[] selectSqls = selectSql.split("\r\n");
        StringBuilder selectSb = new StringBuilder();
        for(int i = 0 , length = selectSqls.length ; i < length ; i ++){
            if(i > 0){
                selectSb.append("\r\n\t\t");
            }
            selectSb.append(selectSqls[i]);
        }
        map.put("selectSql",selectSb.toString());

        map.put("sortColumn", "${sortColumn}");
        map.put("order", "${order}");
        map.put("offset", "${offset}");
        map.put("limit", "${limit}");

        logger.debug("生成代码的数据:[{}]", map);

        return wrapper.wrap(map);
    }

    private ByteArrayOutputStream readTemplate(InputStream inputStream,Table table){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Configuration cfg = new Configuration(FREEMARKER_VERSION);

        UnicodeReader unicodeReader = new UnicodeReader(new UnicodeInputStream(inputStream, "utf-8"));
        BufferedReader reader = new BufferedReader(unicodeReader);
        try {
            Template template = new Template("", reader, cfg);
            Writer out = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
            template.process(table, out);
        }catch(InvalidReferenceException e) {
            logger.error(e.getMessage(),e);
        } catch (TemplateException e) {
            logger.error(e.getMessage(),e);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } finally {
            try {
                if(reader != null){
                    reader.close();
                }
                if(unicodeReader != null){
                    unicodeReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayOutputStream;
    }

    private String formatInsertSql(String sql){
        String s = sql;
        String[] sqls = s.split("\r\n");
        StringBuilder sb = new StringBuilder();
        for(int i = 0 , length = sqls.length ; i < length ; i ++){
            if(i > 0){
                sb.append("\r\n\t\t");
            }
            sb.append(sqls[i]);
        }
        return sb.toString();
    }

    private Document getDocument(InputStream inputStream) {
        Document document = null;
        try {
            DocumentBuilder dbd = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = dbd.parse(inputStream);
        } catch (ParserConfigurationException e) {
            logger.error("字段数据类型配置文件读取失败",e);
        } catch (SAXException e) {
            logger.error("字段数据类型配置文件读取失败",e);
        } catch (IOException e) {
            logger.error("字段数据类型配置文件读取失败",e);
        }

        return document;
    }

    /***
     * 读取插件里的文件
     * @param  pluginConfig 插件配置信息
     * @param  path 文件路径(不能以正斜杠(/)开头)
     * @return InputStream 文件流
     * **/
    @SuppressWarnings("resource")
	private InputStream readFile(PluginConfig pluginConfig,String path){
        Assert.isTrue(!path.startsWith("/"),"path参数不能以正斜杠(/)开头");

        try {
            ZipFile zipFile = new ZipFile(pluginConfig.getPluginPath());
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(pluginConfig.getPluginPath()));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while(zipEntry != null) {
                if(logger.isDebugEnabled()){
                    logger.debug("zipEntry name:[{}]",zipEntry.getName());
                }
                if(!zipEntry.isDirectory() && zipEntry.getName().equals(path)) {
                    break;
                }
                zipEntry = zipInputStream.getNextEntry();
            }

            return zipFile.getInputStream(zipEntry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param table
     * @param databaseType 数据库类型
     * **/
    private boolean getIsIgnorePrimaryKeyColumn(Table table,String databaseType){
        Assert.hasLength(databaseType,"databaseType参数不能为空");

        logger.debug("数据库类型:[{}],表信息:[{}]",databaseType,table);

        //oracle和postgresql字段不能设置自增属性，必须通过序列实现自增
        if("oracle".equals(databaseType) || "postgresql".equals(databaseType)){
            return false;
        }

        //无主键字段
        if(table.getPrimaryKeyColumn() == null){
            return false;
        }
        //是否自动生成
        return table.getPrimaryKeyColumn().getAutoIncrement();
    }
    /**
     * 创建update语句
     *
     * @param table 数据库表信息
     * @return Update update语句
     *
     * **/
    private Update createUpdate(Table table){
        Update update = new Update();
        //table
        update.setTables(Lists.newArrayList(new net.sf.jsqlparser.schema.Table(table.getName())));
        //where
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new net.sf.jsqlparser.schema.Column(table.getPrimaryKeyColumn().getName()));
        CustomJdbcNamedParameter primaryKeyParameter = new CustomJdbcNamedParameter();
        primaryKeyParameter.setName("#{"+table.getPrimaryKeyColumn().getJavaName() + (StringUtils.hasLength(table.getPrimaryKeyColumn().getMapperJdbcType()) ?  ",jdbcType=" + table.getPrimaryKeyColumn().getMapperJdbcType(): "") +"}");
        equalsTo.setRightExpression(primaryKeyParameter);
        update.setWhere(equalsTo);

        //columns
        List<net.sf.jsqlparser.schema.Column> columns = new ArrayList<>();
        List<Expression> expressions = new ArrayList<>();

        for(Column column : table.getColumns()){
            //忽略主键字段
            if(column.getIsPrimaryKey()){
                continue;
            }
            columns.add(new net.sf.jsqlparser.schema.Column(column.getName()));
            CustomJdbcNamedParameter parameter = new CustomJdbcNamedParameter();
            parameter.setName("#{"+column.getJavaName() + (StringUtils.hasLength(column.getMapperJdbcType()) ? ",jdbcType=" + column.getMapperJdbcType() : "") + "}");
            expressions.add(parameter);
        }
        update.setColumns(columns);
        update.setExpressions(expressions);

        return update;
    }
    /**
     * 创建insert语句
     *
     * @param table 数据库表信息
     * @param  ignorePrimaryKeyColumn 是否忽略主键字段
     *
     * @return Insert insert语句
     *
     * **/
    private Insert createInsert(Table table,boolean ignorePrimaryKeyColumn){
        Insert insert = new Insert();
        insert.setTable(new net.sf.jsqlparser.schema.Table(table.getName()));

        List<net.sf.jsqlparser.schema.Column> columns = new ArrayList<>();
        List<Expression> expressions = new ArrayList<>();

        for(Column column : table.getColumns()){
            //忽略主键字段
            if(ignorePrimaryKeyColumn && column.getIsPrimaryKey()){
                continue;
            }
            columns.add(new net.sf.jsqlparser.schema.Column(column.getName()));
            CustomJdbcNamedParameter parameter = new CustomJdbcNamedParameter();
            parameter.setName("#{"+column.getJavaName() + (StringUtils.hasLength(column.getMapperJdbcType()) ? ",jdbcType=" + column.getMapperJdbcType() : "") +"}");
            expressions.add(parameter);
        }

        ExpressionList itemsList = new ExpressionList();
        itemsList.setExpressions(expressions);

        insert.setColumns(columns);
        insert.setItemsList(itemsList);
        return insert;
    }

    private Select createSelect(Table table){
        Select select = new Select();
        PlainSelect selectBody = new PlainSelect();
        net.sf.jsqlparser.schema.Table t = new net.sf.jsqlparser.schema.Table(table.getName());
        selectBody.setFromItem(t);

        List<SelectItem> selectItems = new ArrayList<>(table.getColumns().size());
        for(Column column : table.getColumns()){
            net.sf.jsqlparser.schema.Column col = new net.sf.jsqlparser.schema.Column();
            col.setTable(t);
            col.setColumnName(column.getName());

            SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
            selectExpressionItem.setExpression(col);
            selectItems.add(selectExpressionItem);
        }
        selectBody.setSelectItems(selectItems);

        select.setSelectBody(selectBody);
        return select;
    }
    private String getDatabaseType(){
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            //根据数据库连接匹配数据库类型
            DatabaseMetaData metaData = connection.getMetaData();

            String databaseType = metaData.getDatabaseProductName();
            String databaseVersion = metaData.getDatabaseProductVersion();

            //数据库连接
            String databaseUrl = metaData.getURL();

            logger.debug("数据库类型:{} 数据库版本:{} 数据库连接:{}",databaseType,databaseVersion,databaseUrl);

            //获取数据库方言
            //sqlserver获取到的数据库类型是[Microsoft SQL Server],需要删除所有的空格
            return databaseType.toLowerCase().replaceAll("\\s", "");
        }catch(SQLException e) {
            logger.error("数据库表抽取失败:{}",e);
            throw new CodeGenerateException(e);
        }finally {
            try {
                if(connection != null && !connection.isClosed()) {
                    connection.close();
                    connection = null;
                }
            } catch (SQLException e) {
                logger.error("数据库连接释放失败:{}",connection);
            }
        }
    }
}
