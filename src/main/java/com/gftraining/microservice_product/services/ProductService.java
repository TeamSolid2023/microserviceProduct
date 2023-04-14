package com.gftraining.microservice_product.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.configuration.Categories;
import com.gftraining.microservice_product.configuration.ServicesUrl;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ProductService{
	private ProductRepository productRepository;
	private Categories categories;
	private ServicesUrl servicesUrl;

	public ProductService(ProductRepository productRepository, Categories categories, ServicesUrl servicesUrl) {
		super();
		this.productRepository = productRepository;
		this.categories = categories;
        this.servicesUrl = servicesUrl;
	}

	public List<ProductEntity> getAll() {
		List<ProductEntity> products = productRepository.findAll();
		return addFinalPriceToProductsList(products);
	}

	public BigDecimal calculateFinalPrice(BigDecimal price, int discount) {
		return price.subtract(price.multiply(BigDecimal.valueOf(discount)).divide(new BigDecimal("100")))
				.round(new MathContext(4, RoundingMode.HALF_UP));
	}

	public int getDiscount(ProductEntity product) {
		return Optional.ofNullable(categories.getCategories().get(product.getCategory())).orElse(0);
	}

	public void deleteProductById(Long id) {
		getProductById(id);
		productRepository.deleteById(id);
        deleteProductFromCarts(id).subscribe(result -> log.info(result.toString()));
	}

    public Mono<Object> deleteProductFromCarts(Long id) {
		log.info("Empieza llamada asincrona a carrito");
        return WebClient.create(servicesUrl.getCartUrl())
				.delete()
                .uri( "/products/{id}",id)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorResume(error -> {
					log.info("Devuelve error en llamada carrito");
                    if (error instanceof WebClientException && error.getCause() instanceof ConnectException) {
                        // Handle connection error
                        return Mono.error(new ConnectException("Error deleting product from carts: Error connecting to cart service."));
                    }
                    return Mono.error(error);
                })
                .filter(response -> !Objects.isNull(response.toString()));
    }

    public ProductEntity getProductById(Long id) {
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product with id: " + id + " not found."));
		return addFinalPriceToProductsList(Arrays.asList(product)).get(0);
	}

	public List<ProductEntity> getProductByName(String name) {
		List<ProductEntity> products = productRepository.findAllByName(name);
		if (products.isEmpty()) throw new EntityNotFoundException("Products with name: " + name + " not found.");

		return addFinalPriceToProductsList(products);
	}

	public Long saveProduct(ProductDTO productDTO) {
		ProductEntity product = new ProductEntity();

		product.setName(productDTO.getName());
		product.setDescription(productDTO.getDescription());
		product.setPrice(productDTO.getPrice());
		product.setStock(productDTO.getStock());
		product.setCategory(productDTO.getCategory());

		return productRepository.save(product).getId();
	}

	public void updateProductsFromJson(String path) throws IOException {
		productRepository.deleteAll();
		ObjectMapper objectMapper = new ObjectMapper();
		List<ProductEntity> products = objectMapper.readValue(new File(path), new TypeReference<>() {
		});
		productRepository.saveAll(products);

	}

	public void putProductById(ProductDTO newProduct, Long id) {
		if (!categories.getCategories().containsKey(newProduct.getCategory()))
			throw new EntityNotFoundException("Category " + newProduct.getCategory() + " not found. Categories" +
					" allowed: " + categories.getCategories().keySet());

		ProductEntity product = getProductById(id);

		product.setName(newProduct.getName());
		product.setId(id);
		product.setCategory(newProduct.getCategory());
		product.setDescription(newProduct.getDescription());
		product.setPrice(newProduct.getPrice());
		product.setStock(newProduct.getStock());

		productRepository.save(product);
	}

	private List<ProductEntity> addFinalPriceToProductsList(List<ProductEntity> products) {
		return products.stream()
				.map(product -> {
					product.setFinalPrice(calculateFinalPrice(product.getPrice(), getDiscount(product)));
					return product;
				})
				.collect(Collectors.toList());
	}

    public void updateStock(Integer units, Long id) {
        ProductEntity product = getProductById(id);

        Integer newStock = product.getStock()-units;
        product.setStock(newStock);

        productRepository.save(product);
    }
}
