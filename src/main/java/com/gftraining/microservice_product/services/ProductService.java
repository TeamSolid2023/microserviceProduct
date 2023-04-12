package com.gftraining.microservice_product.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.configuration.CategoriesConfig;
import com.gftraining.microservice_product.configuration.MicroserviceStatusConfig;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ProductService{
	private ProductRepository productRepository;
	private CategoriesConfig categoriesConfig;
	private ModelMapper modelMapper;
	private MicroserviceStatusConfig microserviceStatus;
	
	public ProductService(ProductRepository productRepository, CategoriesConfig categoriesConfig, ModelMapper modelMapper) {
		super();
		this.productRepository = productRepository;
		this.categoriesConfig = categoriesConfig;
		this.modelMapper = modelMapper;
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
		/*if (microserviceStatus.isCart()) {
			deleteCartProducts(id);
		} else {

		}*/
		if (microserviceStatus.isUser() == true) {
			deleteUserProducts(id);
		} else {
		}
	}

	private void deleteCartProducts(Long id) {
		CartWebClient webClient = new CartWebClient();
		webClient.deleteResource(id)
				.block();
	}

	private void deleteUserProducts(Long id) {
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

		ProductEntity product = modelMapper.map(productDTO, ProductEntity.class);
		product.setId(id);

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
