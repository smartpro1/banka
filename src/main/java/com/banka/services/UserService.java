package com.banka.services;



import java.math.BigDecimal;

import com.banka.model.User;
import com.banka.payloads.TransferRequestPayload;
import com.banka.payloads.UserRegPayload;

public interface UserService {
User registerUser(UserRegPayload userRegPayload);

void makeTransfer(TransferRequestPayload transferRequestPayload, String name);

BigDecimal getTransferCharges();

User deactivateUser(String username);

User activateUser(String username);

User deactivateCashier(String username);

User activateCashier(String username);

User getUserByUsernameOrEmailOrPhone(String usernameOrEmailOrPhone);
}
