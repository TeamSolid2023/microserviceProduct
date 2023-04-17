package com.gftraining.microservice_product.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.configuration.CategoriesConfig;
import com.gftraining.microservice_product.configuration.ServicesUrl;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

@Slf4j
@Service
public class ProductService{
	private ProductRepository productRepository;
	private CategoriesConfig categoriesConfig;
	private ModelMapper modelMapper;
	private ServicesUrl servicesUrl;


	public ProductService(ProductRepository productRepository, CategoriesConfig categoriesConfig,
						  ModelMapper modelMapper, ServicesUrl servicesUrl) {
		super();
		this.productRepository = productRepository;
		this.categoriesConfig = categoriesConfig;
		this.modelMapper = modelMapper;
        this.servicesUrl = servicesUrl;
	}

	public List<ProductEntity> getAll() {
		List<ProductEntity> products = productRepository.findAll();
		log.info("Found all products");

		log.info("Adding final price to the current list");
		return addFinalPriceToProductsList(products);
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

	public void deleteProductById(Long id) {
		getProductById(id);
		log.info("Get products with id " + id + "to be deleted");

		productRepository.deleteById(id);
	}

    public Mono<Object> deleteCartProducts(Long id) {
		log.info("Starting asynchronous call to cart");

        return WebClient.create(servicesUrl.getCartUrl())
				.delete()
                .uri( "/products/{id}",id)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorResume(error -> {
					log.error("Returning error when cart is called");
                    if (error instanceof WebClientException && error.getCause() instanceof ConnectException) {
                        // Handle connection error
                        return Mono.error(new ConnectException("Error deleting product from carts: Error connecting to cart service."));
                    }
                    return Mono.error(error);
                })
                .filter(response -> !Objects.isNull(response.toString()));
    }

	public void deleteUserProducts(Long id) {
	}

	public ProductEntity getProductById(Long id) {
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product with id: " + id + " not found."));
		log.info("Found product with id " + id);

		log.info("Adding final price to the current product");
		return addFinalPriceToProductsList(Arrays.asList(product)).get(0);
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

	public void putProductById(ProductDTO productDTO, Long id) {
		if (!categoriesConfig.getCategories().containsKey(productDTO.getCategory()))
			throw new EntityNotFoundException("Category " + productDTO.getCategory() + " not found. Categories" +
					" allowed: " + categoriesConfig.getCategories().keySet());
		log.info("Category verified");

		if (productRepository.findById(id).isEmpty()){
			throw new EntityNotFoundException("Id " + id + " not found.");
		}
		log.info("Id verified");

		ProductEntity product = modelMapper.map(productDTO, ProductEntity.class);
		product.setId(id);
		log.info("Copied productDTO to a new ProductEntity to update product with id " + id);

		productRepository.save(product);
	}

	public void putCartProducts(Long id) {
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
		log.info("Copied product with id " + id + "to a new ProductEntity");

        Integer newStock = product.getStock()-units;
        product.setStock(newStock);
		log.info("Updated stock in the new ProductEntity to replace current product with id " + id);

        productRepository.save(product);
    }
}
