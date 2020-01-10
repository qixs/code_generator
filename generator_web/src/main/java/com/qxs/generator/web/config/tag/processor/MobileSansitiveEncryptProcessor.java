package com.qxs.generator.web.config.tag.processor;

import org.springframework.util.StringUtils;

/**
 * <td risk:sansiEncrypt="card:${data.payerCardNo}"></td>
 * **/
public class MobileSansitiveEncryptProcessor extends SansitiveEncryptProcessor{
	
    public MobileSansitiveEncryptProcessor(String dialectPrefix, String attributeName, int precedence) {
		super(dialectPrefix, attributeName, precedence);
	}

	/**
	 * 前四后四显示
	 * 
	 * @param val
	 * @return
	 */
	@Override
	protected String encrypt(Object value) {
		if(value == null) {
			return null;
		}
		String val = value.toString();
		if (StringUtils.isEmpty(val) || val.length() < 11) {
			return val;
		}
		
		//隐藏倒数倒数第九位至倒数第五位,因为前面可能会存在国家代码+86
		return String.format("%s****%s", 
				val.substring(0, val.length() - 8),
				val.substring(val.length() - 4));
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
