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

	@Test
	void roundsVatOnlyOnceOnTheTotal() {
		cart.add("BOK-001", 1); // NL book 12.50 at 9% = 1.125
		CartView view = cart.add("FOD-002", 1); // NL food 1.29 at 9% = 0.1161

		// Displayed line BTW is rounded per line: 1.13 + 0.12 = 1.25
		assertThat(view.lines().get(0).vat()).isEqualByComparingTo("1.13");
		assertThat(view.lines().get(1).vat()).isEqualByComparingTo("0.12");
		// The real BTW is computed on the unrounded total: 1.2411 -> 1.24
		assertThat(view.vat()).isEqualByComparingTo("1.24");
		assertThat(view.subtotalInclVAT()).isEqualByComparingTo("15.03");
	}

	@Test
	void appliesReducedRateForBooksAndStandardRateForLaptops() {
		CartView view = cart.add("LAP-003", 1); // DE laptop at 19%

		assertThat(view.lines()).singleElement().satisfies(line -> {
			assertThat(line.vatRate()).isEqualByComparingTo("19");
			assertThat(line.vat()).isEqualByComparingTo("227.81");
		});
	}

}