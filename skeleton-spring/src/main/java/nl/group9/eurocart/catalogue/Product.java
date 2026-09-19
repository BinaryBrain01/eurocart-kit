package nl.group9.eurocart.catalogue;

import java.math.BigDecimal;

public record Product(
		String id,
		String name,
		Category category,
		BigDecimal price,
		Country countryOfOrigin) {
}
