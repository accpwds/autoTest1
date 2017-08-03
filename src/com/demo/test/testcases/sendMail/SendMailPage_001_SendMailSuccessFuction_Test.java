package com.demo.test.testcases.sendMail;

import org.testng.annotations.Test;
import com.demo.test.base.BaseParpare;
import com.demo.test.utils.SuperAction;

public class SendMailPage_001_SendMailSuccessFuction_Test extends BaseParpare {
	
	@Test(priority=1)
	public void sendMailSuccessFuction() {
		SuperAction.parseExcel("SendMail", "001_SendMailSuccessFuction",
				seleniumUtil);
	}
	
	@Test(priority=2)
	public void sendMailSuccessFuction2() {
		SuperAction.parseExcel("SendMail", "002_SendMailSuccessFuction",
				seleniumUtil);
	}
}