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
		return addFinalPriceToProductsList(products);
	}

	public BigDecimal calculateFinalPrice(BigDecimal price, int discount) {
		return price.subtract(price.multiply(BigDecimal.valueOf(discount)).divide(new BigDecimal("100")))
				.round(new MathContext(4, RoundingMode.HALF_UP));
	}

	public int getDiscount(ProductEntity product) {
		return Optional.ofNullable(categoriesConfig.getCategories().get(product.getCategory())).orElse(0);
	}

	public void deleteProductById(Long id) {
		getProductById(id);
		productRepository.deleteById(id);
	}

    public Mono<Object> deleteCartProducts(Long id) {
		log.info("Empieza llamada asincrona a eliminar producto carrito");
        return WebClient.create(servicesUrl.getCartUrl())
				.delete()
                .uri( "/products/{id}",id)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorResume(error -> {
					log.error("Devuelve error en llamada a eliminar producto carrito");
                    if (error instanceof WebClientException && error.getCause() instanceof ConnectException) {
                        // Handle connection error
                        return Mono.error(new ConnectException("Error deleting product from carts: Error connecting to cart service."));
                    }
                    return Mono.error(error);
                })
                .filter(response -> !Objects.isNull(response.toString()));
    }

	public Mono<Object> deleteUserProducts(Long id) {
		log.info("Empieza llamada asincrona a eliminar producto favorito usuarios");
		return WebClient.create(servicesUrl.getUserUrl())
				.delete()
				.uri( "/favorite/product/{id}",id)
				.retrieve()
				.bodyToMono(Object.class)
				.onErrorResume(error -> {
					log.error("Devuelve error en llamada a eliminar producto favorito usuario");
					if (error instanceof WebClientException && error.getCause() instanceof ConnectException) {
						// Handle connection error
						return Mono.error(new ConnectException("Error deleting product from users: Error connecting to user service."));
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
		if (!categoriesConfig.getCategories().containsKey(productDTO.getCategory()))
			throw new EntityNotFoundException("Category " + productDTO.getCategory() + " not found. Categories" +
					" allowed: " + categoriesConfig.getCategories().keySet());

		ProductEntity product = modelMapper.map(productDTO, ProductEntity.class);

		return productRepository.save(product).getId();
	}

	public void updateProductsFromJson(String path) throws IOException {
		productRepository.deleteAll();
		ObjectMapper objectMapper = new ObjectMapper();
		List<ProductEntity> products = objectMapper.readValue(new File(path), new TypeReference<>() {
		});
		productRepository.saveAll(products);

	}

	public void putProductById(ProductDTO productDTO, Long id) {
		if (!categoriesConfig.getCategories().containsKey(productDTO.getCategory()))
			throw new EntityNotFoundException("Category " + productDTO.getCategory() + " not found. Categories" +
					" allowed: " + categoriesConfig.getCategories().keySet());

		if (productRepository.findById(id).isEmpty()){
			throw new EntityNotFoundException("Id " + id + " not found.");
		}

		ProductEntity product = modelMapper.map(productDTO, ProductEntity.class);
		product.setId(id);

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

        Integer newStock = product.getStock()-units;
        product.setStock(newStock);

        productRepository.save(product);
    }
}
