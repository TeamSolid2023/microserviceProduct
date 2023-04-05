package com.gftraining.microservice_product.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.configuration.Categories;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;


@Service
public class ProductService {
	private ProductRepository productRepository;
	private Categories yaml;
	
	public ProductService(ProductRepository productRepository, Categories yaml) {
		super();
		this.productRepository = productRepository;
		this.yaml = yaml;
	}
	
	public List<ProductEntity> getAll() {
		List<ProductEntity> products = productRepository.findAll();
		for (ProductEntity product : products) {
			product.setFinalPrice(calculateFinalPrice(product.getPrice(), getDiscount(product)));
		}
		return products;
	}
	
	public BigDecimal calculateFinalPrice(BigDecimal price, int discount) {
		return price.subtract(price.multiply(BigDecimal.valueOf(discount)).divide(new BigDecimal("100")))
				.round(new MathContext(4, RoundingMode.HALF_UP));
	}
	
	public int getDiscount(ProductEntity product) {
		if (yaml.getCategory().get(product.getCategory()) == null) {
			return 0;
		} else {
			return yaml.getCategory().get(product.getCategory());
		}
	}
	
	public void deleteProductById(Long id) {
		productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product with id: " + id + " not found."));
		productRepository.deleteById(id);
		deleteCartProducts(id);
	}
	
	private void deleteCartProducts(Long id) {
		CartWebClient webClient = new CartWebClient();
		webClient.deleteResource(id)
				.block();
	}
	
	public ProductEntity getProductById(Long id) {
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product with id: " + id + " not found."));
		product.setFinalPrice(calculateFinalPrice(product.getPrice(), getDiscount(product)));
		return product;
	}
	
	public List<ProductEntity> getProductByName(String name) {
		List<ProductEntity> products = productRepository.findAllByName(name);
		if (products.isEmpty()) throw new EntityNotFoundException("Products with name: " + name + " not found.");
		
		for (ProductEntity product : products) {
			product.setFinalPrice(calculateFinalPrice(product.getPrice(), getDiscount(product)));
		}
		return products;
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
		if (!yaml.getCategory().containsKey(newProduct.getCategory()))
			throw new EntityNotFoundException("Category " + newProduct.getCategory() + " not found. Categories" +
					" allowed: " + yaml.getCategory().keySet());
		
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product with id: " + id + " not found."));
		
		product.setName(newProduct.getName());
		product.setId(id);
		product.setCategory(newProduct.getCategory());
		product.setDescription(newProduct.getDescription());
		product.setPrice(newProduct.getPrice());
		product.setStock(newProduct.getStock());
		
		productRepository.save(product);
	}
}
