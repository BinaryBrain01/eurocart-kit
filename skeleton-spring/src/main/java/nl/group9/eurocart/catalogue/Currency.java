package nl.group9.eurocart.catalogue;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Currencies with a hardcoded value in euro.
 */
public enum Currency {

	EUR("1.00"),
	GBP("1.20"),
	CHF("1.09"),
	PLN("0.23");

	/**
	 * The currency the shop sells in. We currently only operate in Poland.
	 */
	public static final Currency LOCAL = PLN;

	private final BigDecimal euroValue;

	Currency(String euroValue) {
		this.euroValue = new BigDecimal(euroValue);
	}

	/**
	 * Converts an amount in this currency to the target currency, rounded to 2 decimals.
	 * The conversion is a single multiplication and division, so it rounds only once.
	 */
	public BigDecimal convertTo(Currency target, BigDecimal amount) {
		return amount.multiply(euroValue).divide(target.euroValue, 2, RoundingMode.HALF_UP);
	}

}
