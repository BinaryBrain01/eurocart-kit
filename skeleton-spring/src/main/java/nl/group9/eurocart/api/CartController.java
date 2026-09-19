package nl.group9.eurocart.api;

import nl.group9.eurocart.cart.CartService;
import nl.group9.eurocart.cart.CartView;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cart;

	public CartController(CartService cart) {
		this.cart = cart;
	}

	@GetMapping
	public CartView view() {
		return cart.view();
	}

	@PostMapping("/items")
	public CartView addItem(@RequestBody AddItemRequest request) {
		return cart.add(request.productId(), request.quantityOrOne());
	}

	@DeleteMapping("/items/{productId}")
	public CartView removeItem(@PathVariable String productId) {
		return cart.remove(productId);
	}

	@DeleteMapping
	public CartView clear() {
		return cart.clear();
	}

}
