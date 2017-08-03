package com.demo.test.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

/**
 * @author young
 * @decription 动态生成各个模块中的每条用例的日志，运行完成用例之后请到result/log目录下查看
 * */
public class LogConfiguration {

	public static void initLog(String fileName) {

		String src = "test-output/log";
		// 设置日期格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// 获取当前日期
		String date = dateFormat.format(new Date()).toString();
		// 设置时间格式
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
		// 获取当前时间
		String time = timeFormat.format(new Date()).toString();
		File dir = new File(src + "/" + date);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		
		
		// 获取到模块名字
		String founctionName = getFunctionName(fileName);
		// 声明日志文件存储路径以及文件名、格式
		final String logFilePath = dir.getAbsolutePath() + "/"+ founctionName + 
				"/log_" + time + ".log";
		final String filepathHtml = dir.getAbsolutePath() + "/"+ founctionName + 
				"/log_" + date + ".html";
		
		Properties prop = new Properties();
		// 配置日志输出的格式
		prop.setProperty("log4j.rootLogger", "info, toConsole, toFile, toHtml");
		prop.setProperty("log4j.appender.file.encoding", "UTF-8");
		prop.setProperty("log4j.appender.toConsole",
				"org.apache.log4j.ConsoleAppender");
		prop.setProperty("log4j.appender.toConsole.Target", "System.out");
		prop.setProperty("log4j.appender.toConsole.layout",
				"org.apache.log4j.PatternLayout ");
		prop.setProperty("log4j.appender.toConsole.layout.ConversionPattern",
				"[%d{yyyy-MM-dd HH:mm:ss}] [%p] %m%n");
		prop.setProperty("log4j.appender.toFile",
				"org.apache.log4j.DailyRollingFileAppender");
		prop.setProperty("log4j.appender.toFile.file", logFilePath);
		prop.setProperty("log4j.appender.toFile.append", "false");
		prop.setProperty("log4j.appender.toFile.Threshold", "info");
		prop.setProperty("log4j.appender.toFile.layout",
				"org.apache.log4j.PatternLayout");
		prop.setProperty("log4j.appender.toFile.layout.ConversionPattern",
				"[%d{yyyy-MM-dd HH:mm:ss}] [%p] %m%n");
		/**
		 * 设置生成HTML测试报告
		 */
		prop.setProperty("log4j.appender.toHtml",
				"org.apache.log4j.FileAppender");
		prop.setProperty("log4j.appender.toHtml.file", filepathHtml);
		prop.setProperty("log4j.appender.toHtml.layout",
				"org.apache.log4j.HTMLLayout");
		prop.setProperty("log4j.appender.toHtml.encoding", "GBK");
		// 使配置生效
		PropertyConfigurator.configure(prop);

	}

	/** 取得模块名字 */
	public static String getFunctionName(String fileName) {
		String functionName = null;
		int firstUndelineIndex = fileName.indexOf("_");
		functionName = fileName.substring(0, firstUndelineIndex - 4);
		return functionName;
	}
}
