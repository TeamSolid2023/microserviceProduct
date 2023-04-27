package com.gftraining.microservice_product.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.configuration.CategoriesConfig;
import com.gftraining.microservice_product.configuration.FeatureFlagsConfig;
import com.gftraining.microservice_product.configuration.ServicesUrl;
import com.gftraining.microservice_product.model.CartProductDTO;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoriesConfig categoriesConfig;
  private final ModelMapper modelMapper;
  private final ServicesUrl servicesUrl;
	private final FeatureFlagsConfig featureFlags;
	
	public ProductService(ProductRepository productRepository, CategoriesConfig categoriesConfig,
	                      ModelMapper modelMapper, ServicesUrl servicesUrl, FeatureFlagsConfig featureFlags) {
		super();
		this.productRepository = productRepository;
		this.categoriesConfig = categoriesConfig;
		this.modelMapper = modelMapper;
		this.servicesUrl = servicesUrl;
		this.featureFlags = featureFlags;
	}
	
	public List<ProductEntity> getAll() {
		List<ProductEntity> products = productRepository.findAll();
		log.info("Found all products");
		
		log.info("Adding final price to the current list");
		return addFinalPriceToProductsList(products);
	}
	
	private List<ProductEntity> addFinalPriceToProductsList(List<ProductEntity> products) {
		return products.stream()
				.map(product -> {
					product.setFinalPrice(calculateFinalPrice(product.getPrice(), getDiscount(product)));
					return product;
				})
				.collect(Collectors.toList());
	}
	
	public BigDecimal calculateFinalPrice(BigDecimal price, int discount) {
		log.info("Calculating final price");
		return price.subtract(price.multiply(BigDecimal.valueOf(discount)).divide(new BigDecimal("100")))
				.round(new MathContext(4, RoundingMode.HALF_UP));
	}
	
	public int getDiscount(ProductEntity product) {
		log.info("Looking for discount");
		return Optional.ofNullable(categoriesConfig.getCategories().get(product.getCategory())).orElse(0);
	}
	
	public Mono<Object> patchCartProducts(ProductDTO productDTO, Long id) {
		CartProductDTO cartProductDTO = new CartProductDTO(id,productDTO.getName(),productDTO.getDescription(),productDTO.getPrice().doubleValue());
		log.info("Starting asynchronous call to cart");
		return WebClient.create(servicesUrl.getCartUrl())
				.patch()
				.uri("/products/{id}", id)
				.body(BodyInserters.fromValue(cartProductDTO))
				.retrieve()
				.bodyToMono(Object.class)
				.retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
						.doBeforeRetry(retrySignal ->
							log.info("Trying connection to cart. Retry count: {}", retrySignal.totalRetries() + 1)))
				.doOnError(error -> {
					log.error("Returning error when cart is called");
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Error updating product from carts: Error connecting to cart service.");
				});
	}
	
	public String deleteProductById(Long id) {
		log.info("Getting product with id " + id + "to be deleted");
		if (productRepository.findById(id).isEmpty()) {
			throw new EntityNotFoundException("Id " + id + " not found.");
		}
		log.info("Deleting product");
		productRepository.deleteById(id);

		String message = "Product with id " + id + " deleted successfully.";

		log.info("Checking feature flag to call CART status");
		if (featureFlags.isCallCartEnabled()) {
			log.info("Feature flag to call CART is ENABLED");
			deleteCartProducts(id).subscribe(result -> log.info("Delete product from CART result: " + result.toString()));
		} else {
			log.info("Feature flag to call CART is DISABLED");
			message += " Feature flag to call CART is DISABLED.";
		}

		log.info("Checking feature flag to call USER status");
		if (featureFlags.isCallUserEnabled()) {
			log.info("Feature flag to call USER is ENABLED");
			deleteUserProducts(id).subscribe(result -> log.info("Delete product from user result: " + result.toString()));
		} else {
			log.info("Feature flag to call USER is DISABLED");
			message += " Feature flag to call USER is DISABLED.";
		}

		return message;
	}
	
	public ProductEntity getProductById(Long id) {
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product with id: " + id + " not found."));
		log.info("Found product with id " + id);
		
		log.info("Adding final price to the current product");
		return addFinalPriceToProductsList(Arrays.asList(product)).get(0);
	}
	
	public Mono<Object> deleteCartProducts(Long id) {
		log.info("Starting asynchronous call to cart");
		
		return WebClient.create(servicesUrl.getCartUrl())
				.delete()
				.uri("/products/{id}", id)
				.retrieve()
				.bodyToMono(Object.class)
				.retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
						.doBeforeRetry(retrySignal ->
							log.info("Trying connection to cart. Retry count: {}", retrySignal.totalRetries() + 1)))
				.doOnError(error -> {
					log.error("Returning error when cart is called");
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Error deleting product from carts: Error connecting to cart service.");
				});
	}
	
	public Mono<ResponseEntity<Void>> deleteUserProducts(Long id) {
		log.info("Starting asynchronous call to user");
		return WebClient.create(servicesUrl.getUserUrl())
				.delete()
				.uri("/favorite/product/{id}", id)
				.retrieve()
				.toBodilessEntity()
				.retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
						.doBeforeRetry(retrySignal ->
								log.info("Trying connection to user. Retry count: {}", retrySignal.totalRetries() + 1)))
				.doOnError(error -> {
					log.error("Returning error when user is called");
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Error deleting product from users: Error connecting to user service.");
				});
	}
	
	public List<ProductEntity> getProductByName(String name) {
		List<ProductEntity> products = productRepository.findAllByName(name);
		if (products.isEmpty()) throw new EntityNotFoundException("Products with name: " + name + " not found.");
		log.info("Created list of product with name " + name);
		
		log.info("Adding final price to the current list");
		return addFinalPriceToProductsList(products);
	}
	
	public Long saveProduct(ProductDTO productDTO) {
		if (!categoriesConfig.getCategories().containsKey(productDTO.getCategory()))
			throw new EntityNotFoundException("Category " + productDTO.getCategory() + " not found. Categories" +
					" allowed: " + categoriesConfig.getCategories().keySet());
		log.info("Category verified");
		
		ProductEntity product = modelMapper.map(productDTO, ProductEntity.class);
		log.info("Copied productDTO to a new ProductEntity to add as new product");
		
		return productRepository.save(product).getId();
	}
	
	public void updateProductsFromJson(String path) throws IOException {
		productRepository.deleteAll();
		log.info("Deleted all products");
		
		ObjectMapper objectMapper = new ObjectMapper();
		List<ProductEntity> products = objectMapper.readValue(new File(path), new TypeReference<>() {
		});
		log.info("Created a list with all products");
		
		productRepository.saveAll(products);
	}
	
	public String putProductById(ProductDTO productDTO, Long id) {
		if (!categoriesConfig.getCategories().containsKey(productDTO.getCategory()))
			throw new EntityNotFoundException("Category " + productDTO.getCategory() + " not found. Categories" +
					" allowed: " + categoriesConfig.getCategories().keySet());
		log.info("Category verified");
		
		if (productRepository.findById(id).isEmpty()) {
			throw new EntityNotFoundException("Id " + id + " not found.");
		}
		log.info("Id verified");
		
		ProductEntity product = modelMapper.map(productDTO, ProductEntity.class);
		product.setId(id);
		log.info("Copied productDTO to a new ProductEntity to update product with id " + id);
		
		productRepository.save(product);

		String message = "Product with id " + id + " updated successfully.";

		log.info("Checking feature flag to call CART status");
		if (featureFlags.isCallCartEnabled()) {
			log.info("Feature flag to call CART is ENABLED");
			patchCartProducts(productDTO, id).subscribe(result -> log.info("Update product from CART response: " + result.toString()));
		} else {
			log.info("Feature flag to call CART is DISABLED");
			message = message + " Feature flag to call CART is DISABLED.";
		}

		return message;
	}
	
	public void updateStock(Integer units, Long id) {
		ProductEntity product = getProductById(id);
		log.info("Copied product with id " + id + "to a new ProductEntity");
		
		Integer newStock = product.getStock() - units;

		if(newStock<0 || units<0){
			log.info("If the stock is less than 0 an error jumps");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Modify the quantity. Stock can't be less than 0 and Quantity can't be negative");
		} else {
			product.setStock(newStock);
			log.info("Updated stock in the new ProductEntity to replace current product with id " + id);

			productRepository.save(product);
		}
	}
}
