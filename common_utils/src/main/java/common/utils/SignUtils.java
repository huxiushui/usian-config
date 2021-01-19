package common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 功能描述：签名工具类
 */
public class SignUtils {
	private static final Logger logger = LoggerFactory.getLogger(SignUtils.class);
	private static final String ALGORITHM = "RSA";
	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static String getAlgorithms(boolean rsa2) {
		return rsa2 ? SIGN_SHA256RSA_ALGORITHMS : SIGN_ALGORITHMS;
	}



	/**
	 * 根据渠道标识转换为核心需要的渠道标识
	 * @param channel 渠道标识
	 * @return	核心渠道标识
	 */
	public static String convertChanne(String channel){
		String reChannel="";
		switch(channel){
			case ConstantSY.PMBS:
				reChannel = "84";
				break;
			case ConstantSY.PWBS:
				reChannel = "88";
				break;
			//微信小程序渠道
			case ConstantSY.PXBS:
				reChannel = "86";
				break;
			case ConstantSY.PHBS:
				reChannel = "82";
				break;
			case ConstantSY.PHDBS:
				reChannel = "86";
                break;
			case ConstantSY.PYBS:
				reChannel = "32";
				break;
			default:
				reChannel = "84";
				break;
		}
		return reChannel;
	}

	/**
	 * 根据身份证号返回出生年月日
	 * 此法能用60年
	 * @param idNo 15位或18位身份证号
	 * @return yyyy-mm-dd格式的年月
	 */
	public static String getBirthdayById(String idNo){
		String birthDay = "";
		if(idNo.length()==15){
			int year = Integer.parseInt(idNo.substring(6, 8));
			if(year>83){
				birthDay = "19"+idNo.substring(6, 8)+"-"+idNo.substring(8, 10)+"-"+idNo.substring(10, 12);
			}else{
				birthDay = "20"+idNo.substring(6, 8)+"-"+idNo.substring(8, 10)+"-"+idNo.substring(10, 12);
			}
		}else{
			birthDay = idNo.substring(6, 10)+"-"+idNo.substring(10, 12)+"-"+idNo.substring(12, 14);
		}
		return birthDay;
	}


	/**
	 * 根据渠道卡类型转换为支付系统的卡类型
	 *	渠道： 借记卡：PDBC	信用卡：PEBC
	 *	通联： 借记卡：1 	信用卡：2
	 * @param //cardBin 卡bin
	 * @return 通联支付的银行代码
	 */
	public static String convertAllinPayCardType(String CardType){
		String convertCardType = "";
		switch(CardType){
			case "PDBC":	//借记卡
				convertCardType="0";
				break;
			case "PEBC":	//贷记卡
				convertCardType="1";
				break;
			default:
				convertCardType="0";
				break;
		}
		return convertCardType;
	}

	/**
	 * double类型的金额大写转换
	 * @param n double类型的金额
	 * @return 大写的金额
	 */
	public static String digitCapital(double n) {
	    String fraction[] = {"角", "分"};
	    String digit[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
	    String unit[][] = {{"元", "万", "亿"}, {"", "拾", "佰", "仟"}};

	    String head = n < 0 ? "负" : "";
	    // 如果是负数取绝对值
	    n = Math.abs(n);
	    String s = "";
	    BigDecimal bigDecimal = new BigDecimal(Double.valueOf(n).toString());
	    String nStr = bigDecimal.toString();
	    // 小数部分
	    String[] split = nStr.split("\\.");
	    if (split.length > 1) {
	        // 小数点为特殊符号，在分割时需进行转义
	        String decimalStr = split[1];
	        if (decimalStr.length() > 2) {
	            decimalStr = decimalStr.substring(0, 2);
	        }
	        // 将小数部分转换为整数
	        Integer integer = Integer.valueOf(decimalStr);
	        String p = "";
	        for (int i = 0; i < decimalStr.length() && i < fraction.length; i++) {
	            p = digit[integer % 10] + fraction[decimalStr.length() - i - 1] + p;
	            integer = integer / 10;
	        }
	        s = p.replaceAll("(零.)+", "") + s;
	    }
	    if (s.length() < 1) {
	        s = "整";
	    }
	    int integerPart = (int)Math.floor(n);
	    // 整数部分
	    for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
	        String p = "";
	        for (int j = 0; j < unit[1].length && n > 0; j++) {
	            p = digit[integerPart % 10] + unit[1][j] + p;
	            integerPart = integerPart / 10;
	        }
	        s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
	    }
	    return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
	}


}
