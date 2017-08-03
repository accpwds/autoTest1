package com.demo.test.testcases.createdAddressBook;

import org.testng.annotations.Test;
import com.demo.test.base.BaseParpare;
import com.demo.test.utils.SuperAction;

public class CreatedAddressBookPage_001_CreateAddressSuccessFuction_Test extends
		BaseParpare {
	@Test
	public void createAddressSuccessFuction() {
		SuperAction.parseExcel("CreatedAddressBook",
				"001_CreateAddressSuccessFuction", seleniumUtil);
	}
}