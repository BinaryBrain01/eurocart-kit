package nl.group9.eurocart.cart;

public class UnknownProductException extends RuntimeException {

	public UnknownProductException(String productId) {
		super("Unknown product: " + productId);
	}

}
