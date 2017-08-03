package com.demo.test.testcases.login;

import org.testng.annotations.Test;
import com.demo.test.base.BaseParpare;
import com.demo.test.utils.SuperAction;

public class LoginPage_001_loginSuccessFuction_Test extends BaseParpare {
	
	@Test
	public void loginSuccessFuction() {
		SuperAction
				.parseExcel("Login", "001_loginSuccessFuction", seleniumUtil);
	}
}