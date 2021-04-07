package com.banka.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;

import com.banka.model.Transaction;
import com.banka.model.User;
import com.banka.model.UserProfile;
import com.banka.payloads.AccountInfoResponse;
import com.banka.payloads.ChangePasswordRequest;
import com.banka.payloads.ChangePinRequest;
import com.banka.payloads.JwtLoginSuccessResponse;
import com.banka.payloads.MakeDepositPayload;
import com.banka.payloads.PasswordResetRequest;
import com.banka.payloads.RegistrationSuccessResponse;
import com.banka.payloads.TransactionDto;
import com.banka.payloads.TransferRequestPayload;
import com.banka.payloads.TransferSuccessResponse;
import com.banka.payloads.UserLoginPayload;
import com.banka.payloads.UserRegPayload;
import com.banka.payloads.WithdrawalRequestPayload;
import com.banka.security.JwtTokenProvider;
import com.banka.services.FieldsValidationService;
import com.banka.services.UserService;
import com.banka.validators.AppValidator;
import com.banka.validators.ChangePasswordValidator;
import com.banka.validators.ChangePinValidator;

import static com.banka.security.SecurityConstants.TOKEN_PREFIX;
import static com.banka.utils.Constants.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FieldsValidationService validateFields;
	
	@Autowired 
	private AppValidator appValidator;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private ChangePasswordValidator changePasswordValidator;
	
	@Autowired
	private ChangePinValidator changePinValidator;
	

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegPayload userRegPayload, BindingResult result, HttpServletRequest httpServletRequest) {
		// compare passwords
		appValidator.validate(userRegPayload, result);
		
		// validate input fields
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		if(errorMap != null) return errorMap;
		
		User newUser = userService.registerUser(userRegPayload, httpServletRequest);
		return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
	}
	
	@PostMapping("/confirm-registration")
	public ResponseEntity<RegistrationSuccessResponse> confirmRegistration(@RequestParam("token") String confirmationToken){
		RegistrationSuccessResponse message = userService.confirmRegistration(confirmationToken);
		
		if(message == null) {
			return new ResponseEntity<RegistrationSuccessResponse>(message, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<RegistrationSuccessResponse>(message, HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginPayload UserLoginRequest, BindingResult result) {
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		if(errorMap != null) return errorMap;
		
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						UserLoginRequest.getUsername(), UserLoginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = TOKEN_PREFIX + jwtTokenProvider.generateJwtToken(authentication);
		
		return ResponseEntity.ok(new JwtLoginSuccessResponse(true, jwt));
	}
	
	
	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest, BindingResult result,  
			HttpServletRequest httpServletRequest){
		
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		if(errorMap != null) return errorMap;
		userService.processForgotPassword(passwordResetRequest, httpServletRequest);
		
		return new ResponseEntity<String>("reset password mail sent to " +passwordResetRequest.getEmail(), HttpStatus.OK);		
		
	}
	
	
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, BindingResult result){
		// compare password
		changePasswordValidator.validate(changePasswordRequest, result);
		
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		if(errorMap != null) {
			return errorMap;
		}
        userService.resetPassword(changePasswordRequest.getPassword(), changePasswordRequest.getToken());
        
        return new ResponseEntity<String>("password reset successful", HttpStatus.OK);
	}
	
	
	@PostMapping("/change-pin")
	public ResponseEntity<?> changePin(@Valid @RequestBody ChangePinRequest changePinRequest , BindingResult result, Principal principal) {
		// compare newPin with confirmNewPin
		changePinValidator.validate(changePinRequest, result);
		
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		if(errorMap != null) return errorMap;
		
		userService.changePin(changePinRequest, principal.getName());
		
		return new ResponseEntity<String>("change pin successful", HttpStatus.OK);
	}
	
	@GetMapping("/get-account-info")
	public ResponseEntity<?> getAccountInfo(Principal principal){
		AccountInfoResponse acctDetails =  userService.getAccountInfo(principal.getName());
		
		return new ResponseEntity<AccountInfoResponse>(acctDetails, HttpStatus.OK);
	}
	
	@GetMapping("/get-dummy-accounts")
	public ResponseEntity<List<UserProfile>> getDummyAccounts(){
		List<UserProfile> userProfiles = userService.getDummyAccounts();
		
		return new ResponseEntity<List<UserProfile>>(userProfiles , HttpStatus.OK);
		
	}
	
	
	@PostMapping("/transfer-funds")
	public ResponseEntity<?> transferFunds(@RequestBody TransferRequestPayload transferRequestPayload,
			                          BindingResult result, Principal principal) {
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		if(errorMap != null) {
			return errorMap;
		}
		
		TransferSuccessResponse transferSuccess =  userService.makeTransfer(transferRequestPayload, principal.getName());
		return new ResponseEntity<TransferSuccessResponse>(transferSuccess, HttpStatus.OK);
	}
	
	@GetMapping("/transfer-charges")
	public BigDecimal getTransferCharge() {
		return TRANSFER_CHARGE;
	}
	
	
	 
	@PostMapping("deactivate-user/{username}")
	public ResponseEntity<User> deactivateUser(@PathVariable String username) {
		User deactivatedUser = userService.deactivateUser(username);
		return new ResponseEntity<User>(deactivatedUser, HttpStatus.OK);
		}
	
	@PutMapping("activate-user/{username}")
	public ResponseEntity<User> activateUser(@PathVariable String username) {
		User activatedUser = userService.activateUser(username);
		return new ResponseEntity<User>(activatedUser, HttpStatus.OK);
		}
	
	@PutMapping("deactivate-cashier/{username}")
	public ResponseEntity<User> deactivateCashier(@PathVariable String username) {
		User deactivatedUser = userService.deactivateCashier(username);
		return new ResponseEntity<User>(deactivatedUser, HttpStatus.OK);
		}
	
	@PutMapping("activate-cashier/{username}")
	public ResponseEntity<User> activateCashier(@PathVariable String username) {
		User activatedUser = userService.activateCashier(username);
		return new ResponseEntity<User>(activatedUser, HttpStatus.OK);
		}
	
	
	@GetMapping("get-user-details/{usernameOrEmail}")
	public ResponseEntity<?> getUserByUsernameOrEmail(@PathVariable String usernameOrEmail){
		User userDetails = userService.getUserByUsernameOrEmail(usernameOrEmail);
		return new ResponseEntity<User>(userDetails, HttpStatus.OK);
	}
	
	
	@PostMapping("/withdraw-funds")
	public ResponseEntity<?> makeWithdrawal(@Valid @RequestBody WithdrawalRequestPayload withdrawalRequestPayload,
			                     BindingResult result, Principal principal){
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		if(errorMap != null) {
			return errorMap;
		}
		userService.makeWithdrawal(withdrawalRequestPayload, principal.getName());
		return new ResponseEntity<String>("Successful", HttpStatus.OK);
	}
	
	@PostMapping("/deposit-funds")
	public ResponseEntity<?> makeDeposit(@Valid @RequestBody MakeDepositPayload makeDepositPayload,
			                     BindingResult result, Principal principal){
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		if(errorMap != null) {
			return errorMap;
		}
		userService.makeDeposit(makeDepositPayload, principal.getName());
		return new ResponseEntity<String>("Successful", HttpStatus.OK);
	}
	

	@GetMapping("/transaction-details/{transactionId}")
	public ResponseEntity<List<Transaction>> getTransactionDetails(@PathVariable String transactionId) {
		List<Transaction> transactions = userService.getTransactionByTransId(transactionId);
		return new ResponseEntity<List<Transaction>>(transactions, HttpStatus.OK);
	}
	
	@GetMapping("/user-transactions/{userId}")
	public ResponseEntity<List<TransactionDto>>  getTransactionsByUserId(@PathVariable String userId){
		List<TransactionDto> transactions = userService.getTransactionsByUserId(userId);
		return new ResponseEntity<List<TransactionDto>>(transactions, HttpStatus.OK);
	}

	
}
