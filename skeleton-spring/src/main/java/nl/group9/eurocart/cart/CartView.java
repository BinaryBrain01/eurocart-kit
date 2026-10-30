package nl.group9.eurocart.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartView(List<CartLine> lines, BigDecimal subtotal, BigDecimal subtotalInclVAT) {
}
