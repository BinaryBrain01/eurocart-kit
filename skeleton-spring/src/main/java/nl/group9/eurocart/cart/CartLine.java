package nl.group9.eurocart.cart;

import java.math.BigDecimal;
import java.math.RoundingMode;

import nl.group9.eurocart.catalogue.Product;

/**
 * @param lineTotal price times quantity, excluding BTW
 * @param vatRate BTW percentage that applies to this line
 * @param vat BTW on this line, rounded for display only; the cart BTW is computed from unrounded amounts
 */
public record CartLine(Product product, int quantity, BigDecimal lineTotal, BigDecimal vatRate, BigDecimal vat) {

	public static CartLine of(Product product, int quantity) {
		BigDecimal lineTotal = product.price().multiply(BigDecimal.valueOf(quantity));
		BigDecimal vatRate = VatRates.rate(product.countryOfOrigin(), product.category());
		BigDecimal vat = exactVat(lineTotal, vatRate).setScale(2, RoundingMode.HALF_UP);
		return new CartLine(product, quantity, lineTotal, vatRate, vat);
	}

	/**
	 * Unrounded BTW for an amount at a percentage rate.
	 */
	static BigDecimal exactVat(BigDecimal amount, BigDecimal ratePercent) {
		return amount.multiply(ratePercent).movePointLeft(2);
	}

}