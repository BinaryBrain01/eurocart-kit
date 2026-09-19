package nl.group9.eurocart.cart;

import java.math.BigDecimal;

import nl.group9.eurocart.catalogue.Product;

public record CartLine(Product product, int quantity, BigDecimal lineTotal) {

	public static CartLine of(Product product, int quantity) {
		return new CartLine(product, quantity, product.price().multiply(BigDecimal.valueOf(quantity)));
	}

}
