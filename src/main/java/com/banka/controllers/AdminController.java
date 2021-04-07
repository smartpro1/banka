package com.banka.controllers;

import java.security.Principal;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.banka.model.Transaction;
import com.banka.payloads.Operation;
import com.banka.payloads.TransactionDateRangeDto;
import com.banka.services.FieldsValidationService;
import com.banka.services.UserService;

@RestController
@RequestMapping("/api/v1/admins")
@CrossOrigin
public class AdminController {
	Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FieldsValidationService validateFields;
	
	@PostMapping("/operation")
	public ResponseEntity<?> userStatusOperation(@Valid @RequestBody Operation operation, BindingResult result, Principal principal) {
		
		// validate input fields
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		
		if(errorMap != null) {
			return errorMap;
		}
		
	  String message = userService.userStatusOperation(operation, principal.getName());
	  return new ResponseEntity<String>(message, HttpStatus.OK);
	}
	
	@GetMapping("/track-transactions")
    public Page<Transaction> getTransactionsByDateRange(@RequestBody TransactionDateRangeDto transDateRangeDto, Pageable pageable){
    	Page<Transaction> dateRangeTransactions = userService.findTransactionsByDateRange(transDateRangeDto.getStart(), 
    			transDateRangeDto.getEnd(), pageable);
    	return dateRangeTransactions;
    }
	
//	@GetMapping("/get-transaction/{transactionId}")
//    public Page<Transaction> getTransactionsById(@RequestBody TransactionDateRangeDto transDateRangeDto, Pageable pageable){
//    	Page<Transaction> dateRangeTransactions = userService.findTransactionsByDateRange(transDateRangeDto.getStart(), 
//    			transDateRangeDto.getEnd(), pageable);
//    	return dateRangeTransactions;
//    }

}
