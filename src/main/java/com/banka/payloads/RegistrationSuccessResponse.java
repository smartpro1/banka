package com.banka.payloads;

public class RegistrationSuccessResponse {
   public String fullname;
   
   public RegistrationSuccessResponse(String fullname) {
	   this.fullname = fullname;
   }

   public String getFullname() {
		return fullname;
   }
     
}
