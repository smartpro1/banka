package com.banka.services;

import com.banka.model.User;
import com.banka.payloads.UserRegPayload;

public interface UserService {
User registerUser(UserRegPayload userRegPayload);
}
