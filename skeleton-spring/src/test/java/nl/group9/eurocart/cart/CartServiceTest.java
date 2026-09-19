package nl.group9.eurocart.cart;

import java.math.BigDecimal;

import nl.group9.eurocart.catalogue.ProductCatalogue;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartServiceTest {

	private final CartService cart = new CartService(new ProductCatalogue(new ObjectMapper()));

	@Test
	void startsEmpty() {
		CartView view = cart.view();

		assertThat(view.lines()).isEmpty();
		assertThat(view.subtotal()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	void addsProductsAndSumsPricesExcludingVat() {
		cart.add("BOK-001", 2); // 2 x 12.50
		CartView view = cart.add("FOD-002", 1); // 1 x 1.29

		assertThat(view.lines()).hasSize(2);
		assertThat(view.subtotal()).isEqualByComparingTo("26.29");
	}

	@Test
	void addingSameProductAgainIncreasesQuantity() {
		cart.add("MED-001", 1);
		CartView view = cart.add("MED-001", 3);

		assertThat(view.lines()).singleElement().satisfies(line -> {
			assertThat(line.quantity()).isEqualTo(4);
			assertThat(line.lineTotal()).isEqualByComparingTo("9.96");
		});
	}

	@Test
	void removesProductRegardlessOfQuantity() {
		cart.add("LAP-001", 5);
		cart.add("BOK-010", 1);

		CartView view = cart.remove("LAP-001");

		assertThat(view.lines()).extracting(line -> line.product().id()).containsExactly("BOK-010");
	}

	@Test
	void clearEmptiesTheCart() {
		cart.add("LAP-001", 1);

		CartView view = cart.clear();

		assertThat(view.lines()).isEmpty();
	}

	@Test
	void rejectsUnknownProduct() {
		assertThatThrownBy(() -> cart.add("NOPE-999", 1))
				.isInstanceOf(UnknownProductException.class)
				.hasMessageContaining("NOPE-999");
	}

	@Test
	void rejectsQuantityBelowOne() {
		assertThatThrownBy(() -> cart.add("LAP-001", 0))
				.isInstanceOf(IllegalArgumentException.class);
	}

}
