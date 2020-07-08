package com.banka.services;



import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.banka.model.User;
import com.banka.payloads.ChangePinRequest;
import com.banka.payloads.MakeDepositPayload;
import com.banka.payloads.PasswordResetRequest;
import com.banka.payloads.TransferRequestPayload;
import com.banka.payloads.UserRegPayload;
import com.banka.payloads.WithdrawalRequestPayload;

public interface UserService {
User registerUser(UserRegPayload userRegPayload, HttpServletRequest httpServletRequest);

void makeTransfer(TransferRequestPayload transferRequestPayload, String name);

BigDecimal getTransferCharges();

User deactivateUser(String username);

User activateUser(String username);

User deactivateCashier(String username);

User activateCashier(String username);

User getUserByUsernameOrEmail(String usernameOrEmail);

void makeWithdrawal(WithdrawalRequestPayload withdrawalRequestPayload, String string);

void makeDeposit(MakeDepositPayload makeDepositPayload, String string);

BigDecimal getWithdrawalCharges();

void processForgotPassword(PasswordResetRequest passwordResetRequest, HttpServletRequest httpServletRequest);

void resetPassword(String password, String token);

void changePin(@Valid ChangePinRequest changePinRequest, String username);
}
