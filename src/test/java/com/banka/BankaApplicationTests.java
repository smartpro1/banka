package com.banka;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.banka.controllers.UserController;
import com.banka.model.User;
import com.banka.security.JwtTokenProvider;
import com.banka.security.SecurityAuthenticationEntryPoint;
import com.banka.services.CustomUserDetailsService;
import com.banka.services.FieldsValidationService;
import com.banka.services.UserService;
import com.banka.validators.AppValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
class BankaApplicationTests {
	
	@Autowired
	private MockMvc mockMvc;
	
//	@Autowired
//	private WebApplicationContext context;
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private FieldsValidationService validateFields;
	
	@MockBean 
	private AppValidator appValidator;
	
	@MockBean
	private JwtTokenProvider jwtTokenProvider;
	
	@MockBean
	private AuthenticationManager authenticationManager;
	
	@MockBean
	private SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint;
	
	@MockBean
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private UserController userController;
	
//    @Before
//    private void setUp() {
//    	mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
//    }
	
	/*
	@Test
	public void testDeactivateUser() throws  Exception {
		User user = new User("akeni", "male", "username", "akeni@yahoo.com", "password");
		mockMvc.perform(post("/api/v1/users/deactivate-user/{username}", "myUsername")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isOk());
	}

	@Test
	public void testActivateUser() throws  Exception {
		User user = new User("akeni", "male", "username", "akeni@yahoo.com", "password");
		mockMvc.perform(post("/api/v1/users/activate-user/{username}", "myUsername")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isOk());
	}
	
	@Test
	public void testDeactivateCashier() throws  Exception {
		User user = new User("akeni", "male", "username", "akeni@yahoo.com", "password");
		mockMvc.perform(post("/api/v1/users/deactivate-cashier/{username}", "myUsername")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isOk());
	}
	
	@Test
	public void testActivateCashier() throws  Exception {
		User user = new User("akeni", "male", "username", "akeni@yahoo.com", "password");
		mockMvc.perform(post("/api/v1/users/activate-cashier/{username}", "myUsername")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isOk());
	}
	
	@Test
	public void testGetUserByUsernameOrEmail() throws  Exception {
		User user = new User("akeni", "male", "username", "akeni@yahoo.com", "password");
		mockMvc.perform(post("/api/v1/users/get-user-details/{usernameOrEmail}", "myUsername")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isOk());
	}
	
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
