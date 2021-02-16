package com.banka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.banka.controllers.UserController;
import com.banka.services.UserService;

@SpringBootTest
public class ControllerTest {
	
	@MockBean
	private UserService userService;
	
	@Autowired
	private UserController userController;
	
/*
	@Test
	public void getTransferCharges() {
		BigDecimal transferCharge = new BigDecimal("250.00");
		when(userService.getTransferCharges()).thenReturn(transferCharge);
		BigDecimal result = userController.getTransferCharge();
	    assertEquals(result.intValue(), transferCharge.intValue());
		//assertThat(result.intValue()).isEqualTo(transferCharge.intValue());
	}
	*/
}