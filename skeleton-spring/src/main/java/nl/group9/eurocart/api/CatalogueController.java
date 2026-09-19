package nl.group9.eurocart.api;

import java.util.List;

import nl.group9.eurocart.catalogue.Product;
import nl.group9.eurocart.catalogue.ProductCatalogue;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class CatalogueController {

	private final ProductCatalogue catalogue;

	public CatalogueController(ProductCatalogue catalogue) {
		this.catalogue = catalogue;
	}

	@GetMapping
	public List<Product> all() {
		return catalogue.all();
	}

}
