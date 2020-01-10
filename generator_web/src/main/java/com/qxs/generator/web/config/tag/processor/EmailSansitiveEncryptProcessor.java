package com.qxs.generator.web.config.tag.processor;

import org.springframework.util.StringUtils;

/**
 * <td risk:sansiEncrypt="card:${data.payerCardNo}"></td>
 * **/
public class EmailSansitiveEncryptProcessor extends SansitiveEncryptProcessor{
	/**
	 * 前后保留字符位数
	 * **/
	private static final int RETAINS_LENGTH = 3;
	
	public EmailSansitiveEncryptProcessor(String dialectPrefix, String attributeName, int precedence) {
		super(dialectPrefix, attributeName, precedence);
	}

	@Override
	protected String encrypt(Object value) {
		if(value == null) {
			return null;
		}
		String val = value.toString();
		
		if(StringUtils.isEmpty(val)) {
			return val;
		}
		//获取最后一个@之前的全部字符
		String email = val.substring(0,val.lastIndexOf("@"));
		
		//如果邮箱长度小于前后保留位数+1则无中间字符,无法隐藏
		if(email.length() < RETAINS_LENGTH * 2 + 1) {
			return val;
		}
		
		return String.format("%s***%s@%s", 
				email.substring(0, 3),
				email.substring(email.length() - 3),
				val.substring(val.lastIndexOf("@") + 1));
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
