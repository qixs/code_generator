package com.qxs.generator.web.config.tag.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

/**
 * <td risk:sansiEncrypt="card:${data.payerCardNo}"></td>
 **/
public abstract class SansitiveEncryptProcessor extends AbstractAttributeTagProcessor {

	public SansitiveEncryptProcessor(String dialectPrefix, String attributeName, int precedence) {
		super(TemplateMode.HTML, dialectPrefix, null, false, attributeName, true, precedence, true);
	}

	/**
	 * @param value 要加密的字符串
	 **/
	protected abstract String encrypt(Object value);

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, IElementTagStructureHandler structureHandler) {
		// 获取标签内容表达式
		final String expression = tag.getAttributeValue(attributeName).trim(); 

		// 通过IStandardExpression 解析器 解析表达式获取参数
		final IStandardExpressionParser parser = new StandardExpressionParser();

		final IStandardExpression evaluableExpression = parser.parseExpression(context, expression);

		Object value = evaluableExpression.execute(context);
		
		// 标签名
		final String elementCompleteName = tag.getElementCompleteName(); 
		// 创建模型
		final IModelFactory modelFactory = context.getModelFactory();
		final IModel model = modelFactory.createModel();
		// 添加模型 标签
		model.add(modelFactory.createOpenElementTag(elementCompleteName));
		model.add(modelFactory.createText(HtmlEscape.escapeHtml5(encrypt(value))));
		// 添加模型 标签
		model.add(modelFactory.createCloseElementTag(elementCompleteName));
		// 替换页面标签
		structureHandler.replaceWith(model, false);
	}

}
