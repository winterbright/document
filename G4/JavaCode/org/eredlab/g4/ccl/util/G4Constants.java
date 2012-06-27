package org.eredlab.g4.ccl.util;

/**
 * 常量表
 * 
 * @author XiongChun
 * @since 2009-07-13
 */
public interface G4Constants {
	/**
	 * XML文档风格<br>
	 * 0:节点属性值方式
	 */
	public static final String XML_Attribute = "0";

	/**
	 * XML文档风格<br>
	 * 1:节点元素值方式
	 */
	public static final String XML_Node = "1";

	/**
	 * 字符串组成类型<br>
	 * number:数字字符串
	 */
	public static final String S_STYLE_N = "number";

	/**
	 * 字符串组成类型<br>
	 * letter:字母字符串
	 */
	public static final String S_STYLE_L = "letter";

	/**
	 * 字符串组成类型<br>
	 * numberletter:数字字母混合字符串
	 */
	public static final String S_STYLE_NL = "numberletter";

	/**
	 * 格式化(24小时制)<br>
	 * FORMAT_DateTime: 日期时间
	 */
	public static final String FORMAT_DateTime = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 格式化(12小时制)<br>
	 * FORMAT_DateTime: 日期时间
	 */
	public static final String FORMAT_DateTime_12 = "yyyy-MM-dd hh:mm:ss";

	/**
	 * 格式化<br>
	 * FORMAT_DateTime: 日期
	 */
	public static final String FORMAT_Date = "yyyy-MM-dd";

	/**
	 * 格式化(24小时制)<br>
	 * FORMAT_DateTime: 时间
	 */
	public static final String FORMAT_Time = "HH:mm:ss";
	
	/**
	 * 格式化(12小时制)<br>
	 * FORMAT_DateTime: 时间
	 */
	public static final String FORMAT_Time_12 = "hh:mm:ss";

	/**
	 * 换行符<br>
	 * \n:换行
	 */
	public static final String ENTER = "\n";

	/**
	 * 异常信息统一头信息<br>
	 * 非常遗憾的通知您,程序发生了异常
	 */
	public static final String Exception_Head = "\n非常遗憾的通知您,程序发生了异常.\n" + "异常信息如下:\n";

	/**
	 * Ext表格加载模式<br>
	 * \n:非翻页排序加载模式
	 */
	public static final String EXT_GRID_FIRSTLOAD = "first";

	/**
	 * Excel模板数据类型<br>
	 * number:数字类型
	 */
	public static final String ExcelTPL_DataType_Number = "number";

	/**
	 * Excel模板数据类型<br>
	 * number:文本类型
	 */
	public static final String ExcelTPL_DataType_Label = "label";

	/**
	 * HTTP请求类型<br>
	 * 1:裸请求
	 */
	public static final String PostType_Nude = "1";

	/**
	 * HTTP请求类型<br>
	 * 0:常规请求
	 */
	public static final String PostType_Normal = "0";

	/**
	 * Ajax请求超时错误码<br>
	 * 999:Ajax请求超时错误码
	 */
	public static final int Ajax_Timeout = 999;
	
	/**
	 * Ajax请求非法错误码<br>
	 * 998:当前会话userid和登录时候的userid不一致(会话被覆盖)
	 */
	public static final int Ajax_Session_Unavaliable = 998;
	
	/**
	 * 交易状态:成功
	 */
	public static final Boolean TRUE = new Boolean(true);
	
	/**
	 * 交易状态:失败
	 */
	public static final Boolean FALSE = new Boolean(false);
	
	/**
	 * 交易状态:成功
	 */
	public static final String SUCCESS = "1";
	
	/**
	 * 交易状态:失败
	 */
	public static final String FAILURE = "0";

	/**
	 * 分页查询分页参数缺失错误信息
	 */
	public static final String ERR_MSG_QUERYFORPAGE_STRING = "您正在使用分页查询,但是你传递的分页参数缺失!如果不需要分页操作,您可以尝试使用普通查询:queryForList()方法";
	
	/**
	 * Flash图标色彩数组
	 */
	public static String[] CHART_COLORS = {"AFD8F8","F6BD0F","8BBA00","008E8E","D64646","8E468E","588526","B3AA00","008ED6","9D080D","A186BE","1EBE38"};

}