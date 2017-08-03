package com.demo.test.plugins.arrow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public class TestReport implements IReporter {
	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
			String outputDirectory) {
		List<ITestResult> list = new ArrayList<ITestResult>();
		for (ISuite suite : suites) {
			Map<String, ISuiteResult> suiteResults = suite.getResults();
			for (ISuiteResult suiteResult : suiteResults.values()) {
				ITestContext testContext = suiteResult.getTestContext();
				IResultMap passedTests = testContext.getPassedTests();
				IResultMap failedTests = testContext.getFailedTests();
				IResultMap skippedTests = testContext.getSkippedTests();
				IResultMap failedConfig = testContext.getFailedConfigurations();
				list.addAll(this.listTestResult(passedTests));
				list.addAll(this.listTestResult(failedTests));
				list.addAll(this.listTestResult(skippedTests));
				list.addAll(this.listTestResult(failedConfig));
			}
		}
		this.sort(list);
		this.outputResult(list, outputDirectory + "/test.txt");
		this.outputResult(list, outputDirectory + "/report_autoTest.html");
	}

	private ArrayList<ITestResult> listTestResult(IResultMap resultMap) {
		Set<ITestResult> results = resultMap.getAllResults();
		return new ArrayList<ITestResult>(results);
	}

	private void sort(List<ITestResult> list) {
		Collections.sort(list, new Comparator<ITestResult>() {
			@Override
			public int compare(ITestResult r1, ITestResult r2) {
				if (r1.getStartMillis() > r2.getStartMillis()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	private void outputResult(List<ITestResult> fullResults, String path) {
		ArrayList<ITestResult> passArrayList = new ArrayList<ITestResult>();
		ArrayList<ITestResult> failedArrayList = new ArrayList<ITestResult>();
		ArrayList<ITestResult> skipArrayList = new ArrayList<ITestResult>();
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(new File(
					path)));
			StringBuffer sb = new StringBuffer();
			for (ITestResult result : fullResults) {
				if (result.getStatus() == 1) {
					passArrayList.add(result);
				}
				if (result.getStatus() == 2) {
					failedArrayList.add(result);
				}
				if (result.getStatus() == 3) {
					skipArrayList.add(result);
				}
			}

			sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n");
			sb.append("<meta http-equiv=\"Content-Type\"content=\"text/html; charset=GBK\" />\n");
			sb.append("<title>测试报告</title>\n");
			sb.append("<script type=\"text/javascript\" src=\"js\\jquery-1.7.1.min.js\"></script>\n ");
			sb.append("<script type=\"text/javascript\" src=\"js\\loadxmldoc.js\"></script>\n");
			sb.append("<link href=\"css\\report.css\" rel=\"stylesheet\" type=\"text/css\"/>");
			sb.append("<script type=\"text/javascript\" src=\"js\\report.js\"></script>");
			sb.append("<script type=\"text/javascript\" src=\"js\\Chart.js\"></script>");
			sb.append("</head>\n");
			sb.append("<body>\n");
			sb.append("<div id=\"report_title\">\n");
			sb.append("<div align=\"center\" style=\"font-size:20px;font-weight:bold\">测试报告("
					+ this.formatDate(fullResults.get(0).getStartMillis())
					+ "至"
					+ this.formatDate(fullResults.get(fullResults.size() - 1)
							.getEndMillis()) + ")" + "</div>\n");
			sb.append("</div>\n");
			sb.append("<div id=\"report_total\">\n");
			sb.append("<div style=\"font-size:14px;font-weight:bold\">测试结果汇总</div>\n");
			sb.append("<canvas id=\"canvas\" height=\"80\" width=\"80\"></canvas>\n");
			sb.append("<table width=\"900\" border=\"1\" cellspacing=\"0\"  cellpadding=\"0\" bordercolor=\"#000000\">\n");
			sb.append("<tr>\n<td width=\"84\"><div align=\"center\">用例总数</div></td>\n");
			sb.append("<td width=\"152\" align=\"center\"><div align=\"center\">通过数（pass）</div></td>\n");
			sb.append("<td width=\"125\" align=\"center\"><div align=\"center\">失败数(failed)</div></td>\n");
			sb.append("<td width=\"96\" align=\"center\"><div align=\"center\">跳过数(skip)</div></td>\n");
			sb.append("<td width=\"91\" align=\"center\"><div align=\"center\">通过率</div></td>\n</tr>\n");
			sb.append(" <tr>\n<td><div align=\"center\">");
			sb.append("<button id=\"total_num\">");
			sb.append(passArrayList.size() + failedArrayList.size()
					+ skipArrayList.size());
			sb.append("</button>");
			sb.append("</div></td>\n<td><div align=\"center\">");
			sb.append("<button id=\"passed_num\"" + "value=\""
					+ passArrayList.size() + "\">");
			sb.append(passArrayList.size());
			sb.append("</button>");
			sb.append("</div></td>\n <td><div align=\"center\">");
			sb.append("<button id=\"failed_num\"" + "value=\""
					+ failedArrayList.size() + "\">");
			sb.append(failedArrayList.size());
			sb.append("</button>");
			sb.append("</div></td>\n<td><div align=\"center\">");
			sb.append("<button id=\"skiped_num\"" + "value=\""
					+ skipArrayList.size() + "\">");
			sb.append(skipArrayList.size());
			sb.append("</button>");
			sb.append("</div></td>\n<td><div align=\"center\">");
			float total = passArrayList.size() + failedArrayList.size()
					+ skipArrayList.size();
			float s = passArrayList.size() / total;
			sb.append(s * 100);
			sb.append("%");
			sb.append("</div></td>\n</tr>\n</table>\n");
			sb.append("<script type=\"text/javascript\">\n"
					+ "var x= $('#failed_num').attr('value');\n"
					+ "var y=$(\"#passed_num\").attr(\'value\');\n"
					+ "var z=$(\"#skiped_num\").attr(\'value\');\n"
					+ "var passed_num=Number(y);\n"
					+ "var failed_num=Number(x);\n"
					+ "var skiped_num=Number(z);\n"
					+ "var pieData = [\n"
					+ "{value: passed_num,\n"
					+ "color:\"green\"},\n"
					+ "{value : failed_num,\n"
					+ "color : \"red\"}\n,{\n"
					+ "value : skiped_num,\n"
					+ "color : \"yellow\"}\n"
					+ "];\nvar myPie = new Chart(document.getElementById(\"canvas\").getContext(\"2d\")).Pie(pieData);\n"
					+ "</script>\n");
			// sb.append("<div style=\"font-size:20px;font-weight:bold\">详细测试结果</div>\n");
			// --所有测试结果展示--start
			sb.append("<table id=\"total\" width=\"900\" border=\"1\" cellspacing=\"0\"  cellpadding=\"0\" bordercolor=\"#000000\" style=\"color:green;\">\n<tr>\n");
			sb.append("<tr>\n<td colspan=5 align=\"left\" style=\"color:green;font-size:14px;font-weight:bold\">详细测试结果</td>\n</tr>\n");
			sb.append("<td width=\"300\" class=\"case_name_td\">用例名称</td>\n");
			sb.append("<td width=\"513\" colspan=2 align=\"left\">测试结果</td>\n");
			// sb.append("<td width=\"483\" class=\"case_result_info_td\">结果详情</td>\n");
			sb.append("<td width=\"87\" class=\"case_time\">耗时</td>\n");
			sb.append("<tr>\n");
			for (ITestResult fullITestResult : fullResults) {
				if (sb.length() != 0) {
					sb.append("\r\n");
				}
				float t = fullITestResult.getEndMillis()
						- fullITestResult.getStartMillis();
				String testMethodFullName = fullITestResult.getTestClass()
						.getName()
						+ "."
						+ fullITestResult.getMethod().getMethodName();
				sb.append("<tr id=\"" + testMethodFullName + "\">\n")
						.append("<td class=\"case_name_td\" width=\"300\">"
								+ fullITestResult.getMethod().getDescription()
								+ "</td>\n")
						.append(" ")
						/*
						 * .append("<td width=\"155\">"+testMethodFullName+"</td>\n"
						 * ) .append(" ")
						 * .append("<td width=\"134\">"+this.formatDate
						 * (passresult.getStartMillis())+"</td>\n") .append(" ")
						 */
						.append("<td class=\"case_result_td\" width=\"30\" align=\"center\">");
				if (this.getStatus(fullITestResult.getStatus()).equals("p")) {
					sb.append("<button class=\"passed_button\" id=\"fullresult_"
							+ fullITestResult.getMethod().getMethodName()
							+ "\">"
							+ this.getStatus(fullITestResult.getStatus())
							+ "</button><br />" + "\n");
				} else if (this.getStatus(fullITestResult.getStatus()).equals(
						"f")) {
					sb.append("<button class=\"failed_button\" id=\"fullresult_"
							+ fullITestResult.getMethod().getMethodName()
							+ "\">"
							+ this.getStatus(fullITestResult.getStatus())
							+ "</button><br />" + "\n");
				}
				sb.append("</td>\n").append(
						"<td  class=\"case_result_info_td\" width=\"483\">");
				sb.append(
						"<span class=\"full_info\"id=\"fullresult_"
								+ fullITestResult.getMethod().getMethodName()
								+ "_info\"style=\"display:none\" " + ">")
						.append("用例基本信息：<br />\n")
						.append("method:" + testMethodFullName + "<br />\n")
						.append("start_time:"
								+ this.formatDate(fullITestResult
										.getStartMillis()) + "<br />\n")
						.append("time:" + Float.toString(t) + "毫秒" + "<br />\n");
				sb.append("验证点结果：<br />\n");
				if (this.getStatus(fullITestResult.getStatus()).equals("p")) {
					sb.append("<script type=\"text/javascript\">\n")
							.append("xml=loadXMLDoc(\"http://127.0.0.1/ResultXml/passed.xml\");\n")
							.append("path2=\"//pass_assert_info[@method='"
									+ testMethodFullName + "']\"\n")
							.append("if (window.ActiveXObject)\n")
							.append("{\nvar nodes2=xml.selectNodes(path2);\n")
							// .append("document.write(\"验证点结果：<br />\")")
							.append("for (i=0;i<nodes2.length;i++)\n"
									+ "{document.write((i+1)+\".\"+nodes2[i].childNodes[0].nodeValue+\"<br />\");"
									+ "}" + "}\n").append("</script>");
				} else if (this.getStatus(fullITestResult.getStatus()).equals(
						"f")) {
					sb.append("<script type=\"text/javascript\">\n")
							.append("xml=loadXMLDoc(\"http://127.0.0.1/ResultXml/failed.xml\");\n")
							.append("path1=\"//err[@method='"
									+ testMethodFullName + "']\"\n")
							.append("path2=\"//err_assert_info[@method='"
									+ testMethodFullName + "']\"\n")
							.append("if (window.ActiveXObject)\n")
							.append("{\nvar nodes=xml.selectNodes(path1);\n"
									+ "var nodes2=xml.selectNodes(path2);\n")
							// .append("document.write(\"验证点结果：<br />\")")
							.append("for (i=0;i<nodes2.length;i++)\n"
									+ "{document.write(nodes2[i].childNodes[0].nodeValue+\"<br />\");"
									+ "document.write(\"<br />\");}"
									+ "document.write(\"<br>\");}\n")
							// .append("document.write(\"异常信息：<br />\")")
							.append("for (i=0;i<nodes.length;i++)\n"
									+ "{document.write(nodes[i].childNodes[0].nodeValue+\"<br />\");"
									+ "}\n").append("</script>");
				}

				sb.append("</span></td>\n")
						.append("<td width=\"87\" class=\"case_time\">"
								+ Float.toString(t) + "毫秒" + "</td>\n")

						// 点击结果标志按钮，弹出失败信息
						.append("<script type=\"text/javascript\">\n")
						.append("$(document).ready(function(){")
						.append(" $(\"#" + "fullresult_"
								+ fullITestResult.getMethod().getMethodName()
								+ "\").click(function()"
								+ "{\nif($(\"#fullresult_"
								+ fullITestResult.getMethod().getMethodName()
								+ "_info" + "\").css(\"display\")==\"none\")"
								+ "{$(\"#fullresult_"
								+ fullITestResult.getMethod().getMethodName()
								+ "_info\")" + ".show();\n}\n"
								+ "else\n{\n$(\"#fullresult_"
								+ fullITestResult.getMethod().getMethodName()
								+ "_info\")" + ".hide();" + "}\n}\n );\n")
						.append("});\n</script>")

						.append("<td colspan=\"3\" style=\"display:none\">"
								+ "" + "</td>\n").append("</tr>\n");

			}
			if (sb.length() != 0) {
				sb.append("\r\n");
			}

			sb.append("</table>\n");
			// --详细测试结果--end

			// --跳过结果展示--start
			sb.append("<table id=\"skiped\" width=\"900\" border=\"1\" cellspacing=\"0\"  cellpadding=\"0\" bordercolor=\"#000000\" style=\"color:green;\">\n<tr>\n");
			sb.append("<tr>\n<td colspan=5 align=\"left\" style=\"color:green;font-size:14px;font-weight:bold\">详细测试结果</td>\n</tr>\n");
			sb.append("<td width=\"300\" class=\"case_name_td\">用例名称</td>\n");
			sb.append("<td width=\"513\" colspan=2 align=\"left\">测试结果</td>\n");
			sb.append("<td width=\"87\" class=\"case_time\">耗时</td>\n");
			sb.append("<tr>\n");
			for (ITestResult skipresult : skipArrayList) {
				if (sb.length() != 0) {
					sb.append("\r\n");
				}
				float t = skipresult.getEndMillis()
						- skipresult.getStartMillis();
				String testMethodFullName = skipresult.getTestClass().getName()
						+ "." + skipresult.getMethod().getMethodName();
				sb.append("<tr id=\"" + testMethodFullName + "\">\n")
						.append("<td width=\"300\">"
								+ skipresult.getMethod().getDescription()
								+ "</td>\n")
						.append(" ")
						/*
						 * .append("<td width=\"155\">"+testMethodFullName+"</td>\n"
						 * ) .append(" ")
						 * .append("<td width=\"134\">"+this.formatDate
						 * (passresult.getStartMillis())+"</td>\n") .append(" ")
						 */
						.append("<td width=\"87\">" + Float.toString(t) + "毫秒"
								+ "</td>\n")
						.append("<td width=\"513\">"
								+ this.getStatus(skipresult.getStatus())
								+ "</td>\n")

						// 点击跳过按钮，弹出失败信息
						.append("<script type=\"text/javascript\">\n")
						.append("$(document).ready(function(){")
						.append(" $(\"#" + "skipresult"
								+ skipresult.getMethod().getMethodName()
								+ "\").click(function()"
								+ "{\nif($(\"#skipresult_"
								+ skipresult.getMethod().getMethodName()
								+ "_info" + "\").css(\"display\")==\"none\")"
								+ "{$(\"#skipresult_"
								+ skipresult.getMethod().getMethodName()
								+ "_info\")" + ".show();\n}\n"
								+ "else\n{\n$(\"#skipresult_"
								+ skipresult.getMethod().getMethodName()
								+ "_info\")" + ".hide();" + "}\n}\n );\n")
						.append("});\n</script>")

						.append("<td colspan=\"3\" style=\"display:none\">"
								+ "" + "</td>\n").append("</tr>\n");

			}
			if (sb.length() != 0) {
				sb.append("\r\n");
			}

			sb.append("</table>\n");
			// --跳过结果--end
			// 成功结果展示--start
			sb.append("<table id=\"passed\" width=\"900\" border=\"1\" cellspacing=\"0\"  cellpadding=\"0\" bordercolor=\"#000000\" style=\"color:green;\">\n<tr>\n");
			sb.append("<tr>\n<td colspan=5 align=\"left\" style=\"color:green;font-size:14px;font-weight:bold\">成功测试用例</td>\n</tr>\n");
			sb.append("<td width=\"300\" class=\"case_name_td\">用例名称</td>\n");
			sb.append("<td width=\"513\" colspan=2 align=\"left\">测试结果</td>\n");
			sb.append("<td width=\"87\" class=\"case_time\">耗时</td>\n");
			sb.append("<tr>\n");

			for (ITestResult passresult : passArrayList) {
				if (sb.length() != 0) {
					sb.append("\r\n");
				}
				float t = passresult.getEndMillis()
						- passresult.getStartMillis();
				String testMethodFullName = passresult.getTestClass().getName()
						+ "." + passresult.getMethod().getMethodName();
				sb.append("<tr id=\"" + testMethodFullName + "\">\n")
						.append("<td width=\"300\">"
								+ passresult.getMethod().getDescription()
								+ "</td>\n")
						.append(" ")
						/*
						 * .append("<td width=\"155\">"+testMethodFullName+"</td>\n"
						 * ) .append(" ")
						 * .append("<td width=\"134\">"+this.formatDate
						 * (passresult.getStartMillis())+"</td>\n") .append(" ")
						 */
						// .append("<td width=\"513\">")
						.append("<td class=\"case_result_td\" width=\"30\" align=\"center\"><button class=\"passed_button\" id=\"passedresult_"
								+ passresult.getMethod().getMethodName()
								+ "\">"
								+ this.getStatus(passresult.getStatus())
								+ "</button><br /></td>" + "\n")
						.append("<td  class=\"case_result_info_td\" width=\"483\">")
						.append("<span class=\"passed_info\" id=\"passedresult_"
								+ passresult.getMethod().getMethodName()
								+ "_info\" style=\"display:none\" " + ">")
						.append("method:" + testMethodFullName + "<br />\n")
						.append("start_time:"
								+ this.formatDate(passresult.getStartMillis())
								+ "<br />\n")
						.append("time:" + Float.toString(t) + "毫秒" + "<br />\n")
						.append("<script type=\"text/javascript\">\n")
						.append("xml=loadXMLDoc(\"http://127.0.0.1/ResultXml/passed.xml\");\n")
						.append("path2=\"//pass_assert_info[@method='"
								+ testMethodFullName + "']\"\n")
						.append("if (window.ActiveXObject)\n")
						.append("{\nvar nodes2=xml.selectNodes(path2);\n")
						// .append("document.write(\"验证点结果：<br />\")")
						.append("for (i=0;i<nodes2.length;i++)\n"
								+ "{document.write(nodes2[i].childNodes[0].nodeValue+\"<br />\");"
								+ "document.write(\"<br />\");}"
								+ "document.write(\"<br>\");}\n")
						.append("</script>")
						.append("</span></td>\n")
						.append("<td width=\"87\">" + Float.toString(t) + "毫秒"
								+ "</td>\n")

						// 点击成功按钮，弹出失败信息
						.append("<script type=\"text/javascript\">\n")
						.append("$(document).ready(function(){")
						.append(" $(\"#" + "passedresult_"
								+ passresult.getMethod().getMethodName()
								+ "\").click(function()"
								+ "{\nif($(\"#passedresult_"
								+ passresult.getMethod().getMethodName()
								+ "_info" + "\").css(\"display\")==\"none\")"
								+ "{$(\"#passedresult_"
								+ passresult.getMethod().getMethodName()
								+ "_info\")" + ".show();\n}\n"
								+ "else\n{\n$(\"#passedresult_"
								+ passresult.getMethod().getMethodName()
								+ "_info\")" + ".hide();" + "}\n}\n );\n")
						.append("});\n</script>")

						.append("<td colspan=\"3\" style=\"display:none\">"
								+ "" + "</td>\n").append("</tr>\n");

			}
			if (sb.length() != 0) {
				sb.append("\r\n");
			}
			sb.append("</table>\n");
			// 成功结果展示--end
			// 失败结果展示--start
			sb.append("<table  id=\"failed\" width=\"900\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" bordercolor=\"#000000\" style=\"color:red;margin-top:10px;\" >\n");
			sb.append("<tr>\n<td colspan=5 align=\"left\" style=\"color:red;font-size:14px;font-weight:bold\">失败测试用例</td>\n</tr>\n");
			sb.append("<tr>");
			sb.append("<td width=\"300\" class=\"case_name_td\">用例名称</td>\n");
			sb.append("<td width=\"513\" colspan=2 align=\"left\">测试结果</td>\n");
			// sb.append("<td width=\"483\" class=\"case_result_info_td\">结果详情</td>\n");
			sb.append("<td width=\"87\" class=\"case_time\">耗时</td>\n");
			sb.append("</tr>\n");

			for (ITestResult failedresult : failedArrayList) {
				if (sb.length() != 0) {
					sb.append("\r\n");
				}
				float t = failedresult.getEndMillis()
						- failedresult.getStartMillis();
				String testMethodFullName = failedresult.getTestClass()
						.getName()
						+ "."
						+ failedresult.getMethod().getMethodName();
				sb.append("<tr id=\"" + testMethodFullName + "\">\n")
						.append("<td width=\"300\">"
								+ failedresult.getMethod().getDescription()
								+ "</td>\n")
						.append(" ")
						/*
						 * .append("<td width=\"155\">"+testMethodFullName+"</td>\n"
						 * ) .append(" ")
						 * .append("<td width=\"134\">"+this.formatDate
						 * (failedresult.getStartMillis())+"</td>\n")
						 * .append(" ")
						 */
						// .append("<td width=\"513\">")
						.append("<td class=\"case_result_td\" width=\"30\" align=\"center\"><button class=\"failed_button\" id=\"failedresult_"
								+ failedresult.getMethod().getMethodName()
								+ "\">"
								+ this.getStatus(failedresult.getStatus())
								+ "</button><br /></td>" + "\n")
						.append("<td  class=\"case_result_info_td\" width=\"483\">")
						.append("<span class=\"failed_info\" id=\"failedresult_"
								+ failedresult.getMethod().getMethodName()
								+ "_info\" style=\"display:none\" " + ">")
						.append("method:" + testMethodFullName + "<br />\n")
						.append("start_time:"
								+ this.formatDate(failedresult.getStartMillis())
								+ "<br />\n")
						.append("time:" + Float.toString(t) + "毫秒" + "<br />\n")
						.append("<script type=\"text/javascript\">\n")
						.append("xml=loadXMLDoc(\"http://127.0.0.1/ResultXml/failed.xml\");\n")
						.append("path1=\"//err[@method='" + testMethodFullName
								+ "']\"\n")
						.append("path2=\"//err_assert_info[@method='"
								+ testMethodFullName + "']\"\n")
						.append("if (window.ActiveXObject)\n")
						.append("{\nvar nodes=xml.selectNodes(path1);\n"
								+ "var nodes2=xml.selectNodes(path2);\n")
						// .append("document.write(\"验证点结果：<br />\")")
						.append("for (i=0;i<nodes2.length;i++)\n"
								+ "{document.write(nodes2[i].childNodes[0].nodeValue+\"<br />\");"
								+ "document.write(\"<br />\");}"
								+ "document.write(\"<br>\");}\n")
						// .append("document.write(\"异常信息：<br />\")")
						.append("for (i=0;i<nodes.length;i++)\n"
								+ "{document.write(nodes[i].childNodes[0].nodeValue+\"<br />\");"
								+ "document.write(\"<br />\");}\n")
						.append("</script>")
						.append("</span></td>\n")
						.append("<td width=\"87\">" + Float.toString(t) + "毫秒"
								+ "</td>\n")
						// 点击失败按钮，弹出失败信息
						.append("<script type=\"text/javascript\">\n")
						.append("$(document).ready(function(){")
						.append(" $(\"#" + "failedresult_"
								+ failedresult.getMethod().getMethodName()
								+ "\").click(function()"
								+ "{\nif($(\"#failedresult_"
								+ failedresult.getMethod().getMethodName()
								+ "_info" + "\").css(\"display\")==\"none\")"
								+ "{$(\"#failedresult_"
								+ failedresult.getMethod().getMethodName()
								+ "_info\")" + ".show();\n}\n"
								+ "else\n{\n$(\"#failedresult_"
								+ failedresult.getMethod().getMethodName()
								+ "_info\")" + ".hide();" + "}\n}\n );\n")
						.append("});\n</script>")
						.append("<td colspan=\"3\" style=\"display:none\">")
						.append("</td>\n").append("</tr>\n");

			}
			// 失败结果展示--end
			sb.append("</table>\n</div>\n</body>\n</html>\n");

			output.write(sb.toString());
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String getStatus(int status) {
		String statusString = null;
		switch (status) {
		case 1:
			statusString = "p";
			break;
		case 2:
			statusString = "f";
			break;
		case 3:
			statusString = "s";
			break;
		default:
			break;
		}
		return statusString;
	}

	private String formatDate(long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(date);
	}

}