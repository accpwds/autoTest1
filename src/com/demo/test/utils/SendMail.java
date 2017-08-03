package com.demo.test.utils;

/**
 * @author wudsh
 * @version 创建时间：2017年5月15日 下午5:39:56
 * 类说明
 */

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

public class SendMail {
	private static String username="wudsh@lebaonline.cn";
	private static String password="xxx";
	private static Logger logger = Logger.getLogger(SendMail.class.getName());
	
	public static void main(String[] args) throws Exception{
		String sub="邮件并发测试";
        String from = "897530015@qq.com";
        String to1 = "fangw@suncco.com";
        String to2 = "fgwvip123@163.com";
        String to3 = "18250862112@163.com";
        String path = "D:\\emailable-report.html";
        String file="D:\\test";
        sendEmail(sub, from, to1, to2, to3, path, file);
    }
	
	
	public static void sendEmail(String subject, String aFromEmailAddr, String aToEmailAddr, 
			String aToEmailAddr2, String aToEmailAddr3, String attach, String filePath) throws Exception {
		Properties props = new Properties();
	    props.put("mail.smtp.auth", true);
	    props.put("mail.smtp.starttls.enable", true);
	    props.put("mail.smtp.host", "smtp.exmail.qq.com");
	    props.put("mail.smtp.port", "465");

	    Session session = Session.getInstance(props,
	            new javax.mail.Authenticator() {
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(username, password);
	                }
	            });
		session.setDebug(false);
		MimeMessage message = new MimeMessage(session);
		try{
			message.setFrom(new InternetAddress(aFromEmailAddr));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(aToEmailAddr));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(aToEmailAddr2));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(aToEmailAddr3));
			message.setSubject(subject);
			MimeBodyPart attachFilePart = new MimeBodyPart();
			FileDataSource fds = new FileDataSource(attach);
			attachFilePart.setDataHandler(new DataHandler(fds));
			attachFilePart.setFileName(MimeUtility.encodeWord(fds.getName()));//解决附件名称乱码
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setContent(attachFilePart, "text/html;charset=UTF-8");
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(attachFilePart);
			File dir = new File(filePath);
			String[] children = dir.list();
			if(children==null){
				System.out.println("dir does not exist");
				logger.info("dir " + filePath + " does not exist");
			}else{
				for(int i=0;i<children.length;i++){
					String filename = children[i];
					System.out.println("Adding: " + filename);
					attachFilePart = new MimeBodyPart();
					fds = new FileDataSource(filePath+"\\"+filename);
					attachFilePart.setDataHandler(new DataHandler(fds));
					attachFilePart.setFileName(MimeUtility.encodeWord(fds.getName()));
					mp.addBodyPart(attachFilePart);
				}
			}
			message.setContent(mp);
			message.saveChanges();
			Transport.send(message, message.getAllRecipients());
			System.out.println("Mail was sent to: " + aToEmailAddr + ", " + aToEmailAddr2 + ", " + aToEmailAddr3);
			logger.info("Mail was sent to: " + aToEmailAddr + ", " + aToEmailAddr2 + ", " + aToEmailAddr3);
		}catch(Exception ex){
			System.err.println("Cannot send email. " + ex);
			logger.error("Cannot send email. " + ex);
		}
	}
}

