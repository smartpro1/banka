package com.banka.utils;

import java.math.BigDecimal;

public class Constants {
    private static String transferFee = "25.00";
    private static String withdrawalFee = "50.00";
    private static String minWithdrawal = "500";
	public static final BigDecimal TRANSFER_CHARGE = new BigDecimal(transferFee);
	public static final BigDecimal WITHDRAWAL_CHARGE = new BigDecimal(withdrawalFee);
	public static final BigDecimal MINIMUM_WITHDRAWAL = new BigDecimal(minWithdrawal);
}
