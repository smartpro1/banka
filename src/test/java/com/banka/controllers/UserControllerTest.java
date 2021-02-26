package com.banka.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.banka.model.RoleName.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import com.banka.controllers.UserController;
import com.banka.model.Role;
import com.banka.model.RoleName;
import com.banka.model.User;
import com.banka.payloads.RegistrationSuccessResponse;
import com.banka.payloads.UserRegPayload;
import com.banka.security.JwtTokenProvider;
import com.banka.security.SecurityAuthenticationEntryPoint;
import com.banka.services.CustomUserDetailsService;
import com.banka.services.FieldsValidationService;
import com.banka.services.UserService;
import com.banka.validators.AppValidator;
import com.banka.validators.ChangePasswordValidator;
import com.banka.validators.ChangePinValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
//	@Autowired
//	private WebApplicationContext context;
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	@MockBean
	private ChangePasswordValidator changePasswordValidator;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private FieldsValidationService validateFields;
	
	@MockBean
	private ChangePinValidator changePinValidator;
	
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
	
	@MockBean
	HttpServletRequest httpServletRequest;
	
	@MockBean
	BindingResult result;
	
	@Autowired
	private UserController userController;
	
//    @Before
//    private void setUp() {
//    	mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
//    }
	
	
	@Test
	@DisplayName("Register User Test")
	public void testRegisterUser() throws  Exception {
		
		UserRegPayload userRegPayload = new UserRegPayload();
		userRegPayload.setFullname("Akeni Promise");
		userRegPayload.setSex("M");
		userRegPayload.setPhoneNumber("0706291611");
		userRegPayload.setEmail("promise@yahoo.com");
		userRegPayload.setUsername("username");
		userRegPayload.setPassword("password");
		
		User user = new User(userRegPayload.getFullname(), userRegPayload.getSex(), userRegPayload.getUsername(), 
				              userRegPayload.getEmail(), userRegPayload.getPassword(), "1234");
		
		Set<Role> roles = new HashSet<>();
		Role role = new Role(ROLE_USER);
		roles.add(role);
		user.setRoles(roles);
		user.setIsActive("REGISTRATION_NOT_CONFIRMED");
		
		Mockito.when(userService.registerUser(userRegPayload, httpServletRequest)).thenReturn(user);
		
		mockMvc.perform(post("/api/v1/users/signup")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isCreated());
		
		Mockito.when(userService.registerUser(userRegPayload, httpServletRequest)).thenReturn(user);
		ResponseEntity<?> mockedUser = userController.registerUser(userRegPayload, result, httpServletRequest);
		Object mockedUserEntity = mockedUser.getBody();
		assertTrue(mockedUserEntity.equals(user));
		assertTrue(user.getRoles().contains(role));
		assertEquals("REGISTRATION_NOT_CONFIRMED",user.getIsActive());
	}
	
	@Test
	@DisplayName("Registration Confirmation Test")
	public void testConfirmRegistration() throws  Exception {
	
		RegistrationSuccessResponse res = new RegistrationSuccessResponse("Akeni Promise");
	    String token = UUID.randomUUID().toString();
		
	    Mockito.when(userService.confirmRegistration(token)).thenReturn(res);
		mockMvc.perform(post("/api/v1/users/confirm-registration")
				.param("token", token)
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(res)))
                  .andExpect(status().isOk());
		
		Mockito.when(userService.confirmRegistration(token)).thenReturn(res);
		ResponseEntity<RegistrationSuccessResponse> resEntity = userController.confirmRegistration(token);
		Object mockedEntity = resEntity.getBody();
		
		assertTrue(mockedEntity.equals(res));
		assertEquals("Akeni Promise", res.getFullname());
		
	}
	
	@Test
	@DisplayName("Activate User Test")
	public void testDeactivateUser() throws  Exception {
		User user = new User("akeni", "male", "username", "akeni@yahoo.com", "password", "1234");
		
		Set<Role> roles = new HashSet<>();
		Role role = new Role(ROLE_USER);
		roles.add(role);
		user.setRoles(roles);
		user.setIsActive("DEACTIVATED");
		
		Mockito.when(userService.deactivateUser("username")).thenReturn(user);
		mockMvc.perform(post("/api/v1/users/deactivate-user/{username}", "username")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isOk());
		
		ResponseEntity<?> mockedUser = userController.deactivateUser("username");
		Object mockedUserEntity = mockedUser.getBody();
		assertTrue(mockedUserEntity.equals(user));
		assertTrue(user.getRoles().contains(role));
		assertEquals("DEACTIVATED",user.getIsActive());
	}
	

	@Test
	@DisplayName("Deactivate User Test")
	public void testActivateUser() throws  Exception {
		User user = new User("akeni", "male", "username", "akeni@yahoo.com", "password", "1234");
		
		Set<Role> roles = new HashSet<>();
		Role role = new Role(ROLE_USER);
		roles.add(role);
		user.setRoles(roles);
		user.setIsActive("ACTIVE");
		
		Mockito.when(userService.activateUser("username")).thenReturn(user);
		mockMvc.perform(post("/api/v1/users/activate-user/{username}", "username")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isOk());
		
		ResponseEntity<?> mockedUser = userController.activateUser("username");
		Object mockedUserEntity = mockedUser.getBody();
		assertTrue( mockedUserEntity .equals(user));
		assertTrue(user.getRoles().contains(role));
		assertEquals("ACTIVE",user.getIsActive());
	}
	
	
	@Test
	@DisplayName("Deactivate Cashier Test")
	public void testDeactivateCashier() throws  Exception {
		User user = new User("akeni", "male", "cashier-username", "akeni@yahoo.com", "password", "1234");
		
		Set<Role> roles = new HashSet<>();
		Role role = new Role(ROLE_CASHIER);
		roles.add(role);
		user.setRoles(roles);
		user.setIsActive("DEACTIVATED");
		
		Mockito.when(userService.deactivateCashier("cashier-username")).thenReturn(user);
		mockMvc.perform(post("/api/v1/users/deactivate-cashier/{username}", "cashier-username")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isOk());
		
		ResponseEntity<?> mockedCashier = userController.deactivateCashier("cashier-username");
		Object mockedCashierEntity = mockedCashier.getBody();
		assertTrue(mockedCashierEntity.equals(user));
		assertTrue(user.getRoles().contains(role));
		assertEquals("DEACTIVATED",user.getIsActive());
	}
	
	
	@Test
	@DisplayName("Activate Cashier Test")
	public void testActivateCashier() throws  Exception {
		User user = new User("akeni", "male", "cashier-username", "akeni@yahoo.com", "password", "1234");
		
		Set<Role> roles = new HashSet<>();
		Role role = new Role(ROLE_CASHIER);
		roles.add(role);
		user.setRoles(roles);
		user.setIsActive("ACTIVE");
		
		Mockito.when(userService.activateCashier("cashier-username")).thenReturn(user);
		mockMvc.perform(post("/api/v1/users/activate-cashier/{username}", "myUsername")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isOk());
		
		ResponseEntity<?> mockedCashier = userController.activateCashier("cashier-username");
		Object mockedCashierEntity = mockedCashier.getBody();
		assertTrue(mockedCashierEntity.equals(user));
		assertTrue(user.getRoles().contains(role));
		assertEquals("ACTIVE",user.getIsActive());
	}
	
	
	@Test
	@DisplayName("Get User By Username Or Email Test")
	public void testGetUserByUsernameOrEmail() throws  Exception {
		User user = new User("akeni", "male", "username-or-email", "akeni@yahoo.com", "password", "1234");
		
		
		Mockito.when(userService.getUserByUsernameOrEmail("username-or-email")).thenReturn(user);
		mockMvc.perform(post("/api/v1/users/get-user-details/{usernameOrEmail}", "username-or-email")
				.contentType("application/json")
				 .content(objectMapper.writeValueAsString(user)))
                  .andExpect(status().isOk());
		
		ResponseEntity<?> mockedUser = userController.getUserByUsernameOrEmail("username-or-email");
		Object mockedUserEntity = mockedUser.getBody();
		assertTrue(mockedUserEntity.equals(user));
	}
	
	
	@Test
	@DisplayName("Get Transfer Charge Test")
	public void getTransferCharges() {
		final BigDecimal TRANSFER_CHARGE = new BigDecimal("25.00");
		BigDecimal result = userController.getTransferCharge();
	    assertEquals(TRANSFER_CHARGE, result);
	}
	
	

}
