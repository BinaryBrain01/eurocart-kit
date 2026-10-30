package nl.group9.eurocart.cart;

import nl.group9.eurocart.catalogue.Product;
import nl.group9.eurocart.catalogue.ProductCatalogue;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private final ProductCatalogue catalogue;

    private final Map<String, Integer> quantitiesByProductId = new LinkedHashMap<>();

    public CartService(ProductCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    /**
     * Adds {@code quantity} of a product. Adding an existing product increases its quantity.
     */
    public synchronized CartView add(String productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        if (catalogue.find(productId).isEmpty()) {
            throw new UnknownProductException(productId);
        }
        quantitiesByProductId.merge(productId, quantity, Integer::sum);
        return view();
    }

    /**
     * Removes a product entirely, whatever its quantity. Unknown ids are ignored.
     */
    public synchronized CartView remove(String productId) {
        quantitiesByProductId.remove(productId);
        return view();
    }

    public synchronized CartView clear() {
        quantitiesByProductId.clear();
        return view();
    }

    public synchronized CartView view() {
        List<CartLine> lines = quantitiesByProductId.entrySet().stream()
                .map(entry -> CartLine.of(product(entry.getKey()), entry.getValue()))
                .toList();
        BigDecimal subtotal = lines.stream()
                .map(CartLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final var subtotalInclVAT = addPolishVat(subtotal);
        return new CartView(lines, subtotal, subtotalInclVAT);
    }

    private BigDecimal addPolishVat(BigDecimal subtotal) {
        BigDecimal withVAT = subtotal.multiply(BigDecimal.valueOf(1.23));
        return withVAT.setScale(2, RoundingMode.HALF_UP);
    }

    private Product product(String productId) {
        return catalogue.find(productId).orElseThrow(() -> new UnknownProductException(productId));
    }

}
