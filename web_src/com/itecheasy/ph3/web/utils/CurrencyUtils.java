package com.itecheasy.ph3.web.utils;

import java.math.BigDecimal;

import com.itecheasy.ph3.system.Currency;

public class CurrencyUtils {
	/**
	 * 将美元的金额转换为其他币种的金额
	 * 
	 * @param currentCurrency
	 *            当前币种
	 * @param amountOfUSD
	 *            要转换的金额（币种为：美元）
	 * 
	 * @return 转换后币种金额
	 */
	public static BigDecimal USDToOrderCurrency(Currency currentCurrency,
			BigDecimal amountOfUSD) {
		if (currentCurrency == null || amountOfUSD == null)
			return null;

		return currentCurrency.getRate().multiply(amountOfUSD).setScale(2,
				BigDecimal.ROUND_HALF_UP);
	}
	
	
}
