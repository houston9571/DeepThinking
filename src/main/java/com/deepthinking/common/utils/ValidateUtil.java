package com.deepthinking.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

	private static final Logger logger = LoggerFactory.getLogger(ValidateUtil.class);

	private static String xssCode = "javascript:|:expression\\(|<script|<iframe|<frame"
			+ "|<img|%3cscript|%3ciframe|%3cframe|%3cimg|onmouseover%3d|"
			+ "onmouseover=|3c%2fiframe|3c%2fscript|%3C%2Fa%3E|script>|alert.|INSERT INTO |UPDATE |DELETE |SELECT |GRANT |DROP ";

	private static Pattern xssCodePattern = Pattern.compile(xssCode, Pattern.CASE_INSENSITIVE);

	// private static Pattern pwdValiatorPattern =
	// Pattern.compile("((?=.*\\d)(?=.*[a-zA-Z]).{6,20})");
	private static Pattern pwdValiatorPattern = Pattern.compile("([\\d|(a-zA-Z)]){6,20}");


	//强密码 至少10位，须包含：字母、数字、特殊字符
	private static Pattern pwdStrongValiatorPattern = Pattern.compile("((?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{10,20})");

	//支持中文、英文、数字，不能为纯数字
//	private static Pattern usernameValiatorPattern = Pattern.compile("(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}");
	private static Pattern usernameValiatorPattern = Pattern.compile("(?!\\d+$)[\\dA-Za-z\\u4e00-\\u9fa5]{1,20}");

	private static Pattern aliasNameValiatorPattern = Pattern.compile("[_0-9a-zA-Z\u4e00-\u9fa5]{2,10}");

	private static Pattern numberPattern = Pattern.compile("[\\d]{6,20}");

	private static Pattern wordPattern = Pattern.compile("[a-zA-Z]{6,20}");

	private static Pattern encodedPwdValiatorPattern = Pattern.compile("[?+=.*/,0-9a-zA-Z]{6,80}");

	private static Pattern mobileValiatorPattern = Pattern.compile("1[0-9]{10}");
	
	private static Pattern isNumberPattern = Pattern.compile("^[\\d]+$");

	public static final int VERFIY_FAILED = 0; // 验证次数到期

	public static final int VERFIY_SUCCESS = -1; // 验证成功

	public static final int VERFIY_RETRY = -2; // 没有完成验证操作，没有消耗当天验证次数限制

	public static void main(String[] args) {

		 ValidateUtil.testXssCode();
	}

	/**
	 * 判断字符串中是否包含Xss攻击代码
	 *
	 * @param strVal
	 * 需要
	 * @return
	 */
	public static boolean hasXssCode(String strVal) {
		if (StringUtil.isEmpty(strVal)) {
			return false;
		}

		Matcher matcher = xssCodePattern.matcher(strVal);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static void testXssCode() {
		List<String> params = new ArrayList<String>();

		// js
		params.add("TEST <script type='text/javascript'>window.alert('Test');</script>");
		params.add("TEST <SCRIPT type='text/javascript'>window.alert('Test');</script>");
		params.add("TEST window.alert('Test');");
		params.add("TEST </script>");
		params.add("TEST %3Cscript src='http://google.com/.... ");
		params.add("TEST script%3E");
		params.add("<a href='javascript:'");

		// html
		params.add("TEST <p> <div /> </p>");
		params.add("TEST <img src='http://www.google.com.hk/'>");
		params.add("TEST :expression(document.body.offsetWidth?-?110?+?px);");

		// frame
		params.add("TEST <iframe src='http://google.com/'");
		params.add("TEST </iframe>");

		// SQL
		params.add("TEST delete");
		params.add("TEST DeLete");
		params.add("TEST delete |");
		params.add("TEST DeLete |");
		params.add("TEST SELECT ");
		params.add("TEST DROP ");
		params.add("TEST GRANT ");

		for (String val : params) {
			System.out.println("-----------------------------------");
			System.out.print(val);
			if (hasXssCode(val)) {
				System.out.print(" 含有Xss攻击代码");
			}
			else {
				System.out.println(" 安全");
			}
			System.out.println();
		}
	}

	/**
	 * 验证手机号码
	 * 
	 * @param val
	 * @return
	 */
	public static boolean isValidateMobile(String val) {
		// 为空则直接返回false
		if (isBlank(val)) {
			return false;
		}
		// 若匹配密码正则，则返回true
		if (mobileValiatorPattern.matcher(val.trim()).matches()) {
			return true;
		}
		return false;
	}

	public static boolean isValidateEncodedPassword(String val) {

		// 为空则直接返回false
		if (isBlank(val)) {
			return false;
		}
		// 若匹配密码正则，则返回true
		if (encodedPwdValiatorPattern.matcher(val.trim()).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 验证是否是合格的密码
	 * 
	 * @param val
	 * 带验证的字符串
	 * @return
	 */
	public static boolean isValidatePassword(String val) {

		// 为空则直接返回false
		if (isBlank(val)) {
			return false;
		}

//		// 纯数字
//		if (numberPattern.matcher(val.trim()).matches()) {
//			return false;
//		}
//		// 纯字母
//		if (wordPattern.matcher(val.trim()).matches()) {
//			return false;
//		}

		// 若匹配密码正则，则返回true
		if (!pwdValiatorPattern.matcher(val.trim()).matches()) {
			return false;
		}
		return true;
	}


	/**
	 * 强密码
	 * @param val
	 * @return
	 */
	public static boolean isValidateStrongPassword(String val) {

		// 为空则直接返回false
		if (isBlank(val)) {
			return false;
		}

		// 若匹配密码正则，则返回true
		if (!pwdStrongValiatorPattern.matcher(val.trim()).matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 验证是否是合格的用户名
	 *
	 * @param val
	 * @return
	 */
	public static boolean isValidateUsername(String val) {

		// 为空则直接返回false
		if (isBlank(val)) {
			return false;
		}

		// 若匹配密码正则，则返回true
		if (!usernameValiatorPattern.matcher(val.trim()).matches()) {
			return false;
		}
		return true;
	}


	/**
	 * 验证是否是合格的昵称（6-50位 中英文）
	 *
	 * @param val
	 * @return
	 */
	public static boolean isValidateAliasName(String val) {

		// 为空则直接返回false
		if (isBlank(val)) {
			return false;
		}

		if (!aliasNameValiatorPattern.matcher(val.trim()).matches()) {
			return false;
		}
		return true;
	}


	/**
	 * 判断字符串是否为null或空字符串
	 * 
	 * @param val
	 * 需要判断的字符串
	 * @return
	 */
	public static boolean isBlank(String val) {
		if (val == null || "".equals(val.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 8位~20位的数字加字母组合
	 * @param certNo
	 * @return
	 */
	public static boolean isValiDriverLicense(String certNo){
		if (isBlank(certNo)) {
			return false;
		}
		String regex = "^[0-9A-Za-z]{8,20}$";
		return certNo.matches(regex );
	}
	/**
	 * 验证身份证号码
	 * 
	 * @param certNo
	 * @return
	 */
	public static boolean isValidateCertNo(String certNo) {
		// 不能为空
		if (isBlank(certNo)) {
			return false;
		}

		// 验证长度
		int len = certNo.length();
		/*
		 * if(len != 15 && len != 18){ return false; }
		 */
		if (len != 18) {
			return false;
		}

		// 验证中间生日号码
		int year = 0;
		int month = 0;
		int day = 0;

		try {
			/*
			 * if(len == 15){ // 长度为15 year =
			 * Integer.parseInt(certNo.substring(6,8)) + 1900; month =
			 * Integer.parseInt(certNo.substring(8, 10)); day =
			 * Integer.parseInt(certNo.substring(10, 12)); }else{
			 */// 长度为18
			year = Integer.parseInt(certNo.substring(6, 10));
			month = Integer.parseInt(certNo.substring(10, 12));
			day = Integer.parseInt(certNo.substring(12, 14));
			// }
		}
		catch (NumberFormatException e) {
			return false;
		}

		if (year < 1900 || year > 2100) {
			return false;
		}
		if (month > 12 || month < 0) {
			return false;
		}
		if (day > 31 || day < 0) {
			return false;
		}
		return true;
	}

	
	public static boolean isNumber(String val){
		// 纯数字
		return isNumberPattern.matcher(val.trim()).matches();
	}

	/**
	 * 校验银行卡卡号
	 * @param cardId
	 * @return
	 */
	public static boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
		if(bit == 'N'){
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;
	}


	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * @param nonCheckCodeCardId
	 * @return
	 */
	public static char getBankCardCheckCode(String nonCheckCodeCardId){
		if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			//如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if(j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
	}
}
