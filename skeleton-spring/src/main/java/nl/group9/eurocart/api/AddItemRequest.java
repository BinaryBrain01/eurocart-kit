package nl.group9.eurocart.api;

public record AddItemRequest(String productId, Integer quantity) {

	public int quantityOrOne() {
		return quantity == null ? 1 : quantity;
	}

}
