package com.banka.services;



import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.banka.model.Transaction;
import com.banka.model.User;
import com.banka.model.UserProfile;
import com.banka.payloads.AccountInfoResponse;
import com.banka.payloads.ChangePinRequest;
import com.banka.payloads.MakeDepositPayload;
import com.banka.payloads.Operation;
import com.banka.payloads.PasswordResetRequest;
import com.banka.payloads.RegistrationSuccessResponse;
import com.banka.payloads.TransactionDto;
import com.banka.payloads.TransferRequestPayload;
import com.banka.payloads.TransferSuccessResponse;
import com.banka.payloads.UserRegPayload;
import com.banka.payloads.WithdrawalRequestPayload;

public interface UserService {
User registerUser(UserRegPayload userRegPayload, HttpServletRequest httpServletRequest);

TransferSuccessResponse makeTransfer(TransferRequestPayload transferRequestPayload, String name);

//BigDecimal getTransferCharges();

User deactivateUser(String username);

User activateUser(String username);

User deactivateCashier(String username);

User activateCashier(String username);

User getUserByUsernameOrEmail(String usernameOrEmail);

void makeWithdrawal(WithdrawalRequestPayload withdrawalRequestPayload, String string);

void makeDeposit(MakeDepositPayload makeDepositPayload, String string);

//BigDecimal getWithdrawalCharges();

void processForgotPassword(PasswordResetRequest passwordResetRequest, HttpServletRequest httpServletRequest);

void resetPassword(String password, String token);

void changePin(@Valid ChangePinRequest changePinRequest, String username);

RegistrationSuccessResponse confirmRegistration(String confirmationToken);

AccountInfoResponse getAccountInfo(String username);

List<UserProfile> getDummyAccounts();
List<Transaction> getTransactionByTransId(String transactionId);

List<TransactionDto> getTransactionsByUserId(String userId);

String userStatusOperation(Operation operation, String username);



Page<Transaction> findTransactionsByDateRange(String start, String end, Pageable pageable);
}
