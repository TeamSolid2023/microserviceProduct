package com.gftraining.microservice_product.unit_test.services;

import com.gftraining.microservice_product.configuration.CategoriesConfig;
import com.gftraining.microservice_product.configuration.FeatureFlagsConfig;
import com.gftraining.microservice_product.configuration.ServicesUrl;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.model.ResponseHandler;
import com.gftraining.microservice_product.repositories.ProductRepository;
import com.gftraining.microservice_product.services.ProductService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
	
	@InjectMocks
	@Spy
	ProductService service;
	@Mock
	ProductRepository repository;
	@Mock
	CategoriesConfig categoriesConfig;
	@Mock
	ModelMapper modelMapper;

	@Mock
	private ServicesUrl servicesUrl;

	@Mock
	FeatureFlagsConfig featureFlags;
  
  public static MockWebServer mockWebServer;

	List<ProductEntity> productList = Arrays.asList(
			new ProductEntity(1L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100),
			new ProductEntity(2L, "Espaguetis", "Comida", "pasta italiana elaborada con harina de grano duro y agua", new BigDecimal(20.00), 220)
	);
	List<ProductEntity> productListSameName = Arrays.asList(
			new ProductEntity(1L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100),
			new ProductEntity(2L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100)
	);
	ProductEntity productEntity = new ProductEntity(1L, "Pelota", "Juguetes", "pelota futbol", new BigDecimal(19.99), 24);
	ProductDTO productDTO = new ProductDTO(productEntity.getName(), productEntity.getCategory(), productEntity.getDescription(), productEntity.getPrice(), productEntity.getStock());
	Map<String, Integer> cartsChanged = new HashMap<>() {{
		put("cartsChanged", 1);
	}};

	
	@BeforeAll
	static void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}
	
	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}
	
	@Order(1)
	@Test
	@DisplayName("given a product id, when calling cart api to update products, then returns Ok and number of carts affected.")
	void patchCartProducts_returnCartsChanged() throws InterruptedException {
		when(servicesUrl.getCartUrl()).thenReturn("htpp://localhost:" + mockWebServer.getPort());
		
		mockWebServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setBody(String.valueOf(new JSONObject(cartsChanged)))
				.addHeader("Content-Type", "application/json"));
		
		Mono<Object> cartsMono = service.patchCartProducts(productDTO, productEntity.getId());
		
		StepVerifier.create(cartsMono)
				.expectNext(cartsChanged)
				.verifyComplete();
		
		RecordedRequest request = mockWebServer.takeRequest();
		assertThat(request.getMethod()).isEqualTo("PATCH");
		
	}
	
	@Test
	@DisplayName("given a product id, when calling cart api to update product, then returns error 500.")
	void patchCartProducts_returnSError500() {
		when(servicesUrl.getCartUrl()).thenReturn("htpp://localhost:" + mockWebServer.getPort());
		
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		
		Mono<Object> cartsMono = service.patchCartProducts(productDTO, 1L);
		
		StepVerifier.create(cartsMono)
				.expectError()
				.verify();
		
	}
	
	@Test
	@DisplayName("When calling getAll, Then a list of Products is returned")
	void testGetAll() {
		given(repository.findAll()).willReturn(productList);
		
		assertThat(service.getAll()).isEqualTo(productList);
	}
	
	@Test
	@DisplayName("Given a product id, When finding a product on the repository, Then the product is returned")
	void getProductById() {
		given(repository.findById(anyLong())).willReturn(Optional.of(productEntity));
		
		assertThat(service.getProductById(1L)).isEqualToComparingFieldByFieldRecursively(productEntity);
	}
	
	@Test
	@DisplayName("Given a product name, When finding products on the repository by name, Then a list of products with that name is returned")
	void getProductByName() {
		given(repository.findAllByName(anyString())).willReturn(productListSameName);
		
		assertThat(service.getProductByName("Playmobil")).isEqualTo(productListSameName);
	}
	
	@Test
	@DisplayName("Given a Product with a wrong category, When the product is saved, Then throw an error")
	void putProductById_returnsCategoryError() {
		
		Assertions.assertThrows(EntityNotFoundException.class, () -> service.putProductById(productDTO, 1L));
	}
	
	@Test
	@DisplayName("Given a Product with a wrong id, When the product is saved, Then throw an error")
	void putProductById_returnsIdNotFoundError() {
		given(categoriesConfig.getCategories()).willReturn(Map.of("Juguetes", 20));
		
		Assertions.assertThrows(EntityNotFoundException.class, () -> service.putProductById(productDTO, 1L));
	}
	
	@Test
	@DisplayName("Given a product id, When finding a product on the repository, Then the product is returned")
	void saveProduct() {
		given(categoriesConfig.getCategories()).willReturn(Map.of("Juguetes", 20));
		given(modelMapper.map(productDTO, ProductEntity.class)).willReturn(productEntity);
		given(repository.save(any())).willReturn(productEntity);

		ResponseEntity result = service.saveProduct(productDTO);

		verify(repository).save(any());
		assertThat(result).isEqualTo(ResponseHandler.generateResponse("DDBB updated",HttpStatus.CREATED, productEntity.getId()));
	}
	
	@Test
	@DisplayName("Given a Product with a wrong category, When the product is saved, Then throw an error")
	void saveProduct_returnsCategoryError() {
		Assertions.assertThrows(EntityNotFoundException.class, () -> service.saveProduct(productDTO));
	}
	
	@Test
	@DisplayName("Given a path, When calling updateProductsFromJson, Then verify if repository is called")
	void updateDatabase() throws IOException {
		//Put your own path
		service.updateProductsFromJson("C:\\Files\\data.json");
		
		verify(repository).deleteAll();
		verify(repository).saveAll(any());
	}
	
	@Test
	@DisplayName("Given an id and an units, When calling updateStock, Then verify if repository is called")
	void updateStock() throws Exception {
		given(repository.findById(1L)).willReturn(Optional.of(productEntity));
		
		service.updateStock(5, 1L);
		
		verify(repository, times(1)).findById(anyLong());
		verify(repository, times(1)).save(any());
	}

	@Test
	@DisplayName("Given an id and an units, When calling updateStock, Then verify if exception jumps")
	void updateStock_StockLessThan0() throws Exception{
		given(repository.findById(1L)).willReturn(Optional.of(productEntity));

		Assertions.assertThrows(Exception.class, () -> service.updateStock(500, 1L));
	}
	
	@Test
	@DisplayName("Given a BigDecimal and a discount, When calling calculateFinalPrice, Then the result has to be equal to expectedParam")
	void calculateFinalPrice() {
		BigDecimal realParam = new BigDecimal("23.85");
		BigDecimal expectedParam = new BigDecimal("21.47");
		
		assertThat(service.calculateFinalPrice(realParam, 10)).isEqualByComparingTo(expectedParam);
	}
	
	@Test
	@DisplayName("Given a Product, When getting the discount, Then discount has to be 0")
	void getDiscount() {
		assertThat(service.getDiscount(productEntity)).isZero();
	}

	@Test
	@DisplayName("given a product id, when delete product by id, then the product is not found")
	void deleteProductById_NotFoundException() {
		Assertions.assertThrows(EntityNotFoundException.class, () -> service.deleteProductById(9999L));
	}
	
	@Test
	@DisplayName("given a product id, when calling cart api to delete product, then returns Ok and number of carts affected.")
	void deleteCartProducts_returnCartsChanged() throws InterruptedException {
		Long productId = 7L;
		when(servicesUrl.getCartUrl()).thenReturn("htpp://localhost:" + mockWebServer.getPort());
		
		mockWebServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setBody(String.valueOf(new JSONObject(cartsChanged)))
				.addHeader("Content-Type", "application/json"));
		
		Mono<Object> cartsMono = service.deleteCartProducts(productId);
		
		StepVerifier.create(cartsMono)
				.expectNext(cartsChanged)
				.verifyComplete();

		RecordedRequest request = mockWebServer.takeRequest();
		assertThat(request.getMethod()).isEqualTo("DELETE");
	}
	
	@Test
	@DisplayName("given a product id, when calling cart api to delete product, then returns error 500.")
	void deleteCartProducts_returnSError500() {
		Long productId = 7L;
		when(servicesUrl.getCartUrl()).thenReturn("htpp://localhost:" + mockWebServer.getPort());
		
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		
		Mono<Object> cartsMono = service.deleteCartProducts(productId);
		
		StepVerifier.create(cartsMono)
				.expectError()
				.verify();
		
	}
	
	@Test
	@DisplayName("given a product id, when calling user api to delete favorite product, then returns 204 No Content.")
	void deleteUserProducts_returns204NoContent() throws InterruptedException {
		Long productId = 7L;
		when(servicesUrl.getUserUrl()).thenReturn("htpp://localhost:" + mockWebServer.getPort());
		
		mockWebServer.enqueue(new MockResponse()
				.setResponseCode(204));
		
		Mono<ResponseEntity<Void>> userDeleteMono = service.deleteUserProducts(productId);
		
		StepVerifier.create(userDeleteMono)
				.assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT) )
				.expectComplete()
				.verify();
		
		RecordedRequest request = mockWebServer.takeRequest();
		assertThat(request.getMethod()).isEqualTo("DELETE");
	}
	
	@Test
	@DisplayName("given a product id, when calling user api to delete product, then returns error 500.")
	void deleteUserProducts_returnsError500() {
		Long productId = 7L;
		when(servicesUrl.getUserUrl()).thenReturn("htpp://localhost:" + mockWebServer.getPort());
		
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		
		Mono<ResponseEntity<Void>> userDeleteMono = service.deleteUserProducts(productId);
		
		StepVerifier.create(userDeleteMono)
				.expectError()
				.verify();
	}

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    @DisplayName("Given an id, When perform the delete request /products/{id} and callcart and calluser flags are disabled and enabled, " +
            "Then verify if the repository is called and if it returns a specific message.")
    void deleteProductById_CallUserAndCallCart(boolean flag) throws Exception {
        given(featureFlags.isCallCartEnabled()).willReturn(flag);
        given(featureFlags.isCallUserEnabled()).willReturn(flag);
        given(repository.findById(anyLong())).willReturn(Optional.of(productEntity));

		ResponseEntity<Object> resultTrue = ResponseHandler.generateResponse("Product with id " + 1 + " deleted successfully.", HttpStatus.OK, 1L);
		ResponseEntity<Object> resultFalse = ResponseHandler.generateResponse("Product with id 1 deleted successfully." +
				" Feature flag to call CART is DISABLED. Feature flag to call USER is DISABLED.", HttpStatus.OK, 1L);

        if (flag)
            assertThat(service.deleteProductById(1L)).isEqualTo(resultTrue);
        else
            assertThat(service.deleteProductById(1L)).isEqualTo(resultFalse);

        verify(repository).deleteById(anyLong());
    }

	@Test
	@DisplayName("Given an id, When perform the delete request /products/{id} and callcart flag is enabled, " +
			"Then verify call to delete our product and call to delete carts product and product is deleted")
	void deleteProductById_CallCartEnabled() {
		given(featureFlags.isCallCartEnabled()).willReturn(true);
		given(featureFlags.isCallUserEnabled()).willReturn(false);
		given(repository.findById(anyLong())).willReturn(Optional.of(productEntity));

		ResponseEntity<Object> result = ResponseHandler.generateResponse("Product with id " + 1 + " deleted successfully." +
				" Feature flag to call USER is DISABLED.", HttpStatus.OK, 1L);

		assertThat(service.deleteProductById(1L)).isEqualTo(result);

		verify(repository).deleteById(anyLong());
	}

	@Test
	@DisplayName("Given an id, When perform the delete request /products/{id} and calluser flag is enabled, " +
			"Then verify call to delete our product and call to delete users favorite product and product is deleted")
	void deleteProductById_CallUserEnabled() {
		given(featureFlags.isCallCartEnabled()).willReturn(false);
		given(featureFlags.isCallUserEnabled()).willReturn(true);
		given(repository.findById(anyLong())).willReturn(Optional.of(productEntity));

		ResponseEntity<Object> result = ResponseHandler.generateResponse("Product with id " + 1 + " deleted successfully." +
				" Feature flag to call CART is DISABLED.", HttpStatus.OK, 1L);

		assertThat(service.deleteProductById(1L)).isEqualTo(result);

		verify(repository).deleteById(anyLong());
	}

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    @DisplayName("Given an id, When perform the delete request /products/{id} and callcart flag are disabled and enabled, " +
            "Then verify if the repository is called and has updated")
    void putProductById_CallCart(boolean flag) throws Exception {
        given(featureFlags.isCallCartEnabled()).willReturn(flag);
        given(categoriesConfig.getCategories()).willReturn(Map.of("Juguetes", 20));
        given(repository.findById(anyLong())).willReturn(Optional.of(productEntity));
        given(modelMapper.map(productDTO, ProductEntity.class)).willReturn(productEntity);

        if (flag)
            assertThat(service.putProductById(productDTO,1L))
					.isEqualTo(ResponseHandler.generateResponse("Product with id " + 1 + " updated successfully.",HttpStatus.OK,1L));
        else
            assertThat(service.putProductById(productDTO,1L))
					.isEqualTo(ResponseHandler.generateResponse("Product with id 1 updated successfully. Feature flag to call CART is DISABLED.",HttpStatus.OK,1L));

        verify(repository).save(any());
    }
}
