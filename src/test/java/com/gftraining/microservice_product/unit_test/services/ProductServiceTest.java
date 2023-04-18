package com.gftraining.microservice_product.unit_test.services;


import com.gftraining.microservice_product.configuration.CategoriesConfig;
import com.gftraining.microservice_product.configuration.ServicesUrl;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import com.gftraining.microservice_product.services.ProductService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks @Spy
    ProductService service;
    @Mock
    ProductRepository repository;
    @Mock
    CategoriesConfig categoriesConfig;
    @Mock
    private ServicesUrl servicesUrl;
    public static MockWebServer mockWebServer;

    List<ProductEntity> productList = Arrays.asList(
            new ProductEntity(1L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100),
            new ProductEntity(2L, "Espaguetis", "Comida", "pasta italiana elaborada con harina de grano duro y agua", new BigDecimal(20.00), 220)
    );
    List<ProductEntity> productListSameName = Arrays.asList(
            new ProductEntity(1L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100),
            new ProductEntity(2L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100)
    );
    ProductEntity productEntity = new ProductEntity(1L,"Pelota", "Juguetes","pelota futbol",new BigDecimal(19.99),24);
    ProductDTO productDTO = new ProductDTO(productEntity.getName(), productEntity.getCategory(), productEntity.getDescription(), productEntity.getPrice(), productEntity.getStock());
    Map<String,Integer> cartsChanged = new HashMap<>(){{
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
    @DisplayName("Given a Product, When the product is saved, Then verify if repository is called and if the id is 1")
    void putProductById() {
        given(repository.save(productEntity)).willReturn(productEntity);

        Long id = repository.save(productEntity).getId();

        verify(repository).save(any());
        assertThat(id).isEqualTo(1L);
    }

    @Test
    @DisplayName("given a product id, when calling cart api to update products, then returns Ok and number of carts affected.")
    void putCartProducts_returnCartsChanged() throws InterruptedException, JSONException {
        when(servicesUrl.getCartUrl()).thenReturn("htpp://localhost:" + mockWebServer.getPort());

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(String.valueOf(new JSONObject(cartsChanged)))
                .addHeader("Content-Type", "application/json"));

        Mono<Object> cartsMono = service.putCartProducts(productDTO,productEntity.getId());

        StepVerifier.create(cartsMono)
                .expectNext(cartsChanged)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        String requestBody = request.getBody().readUtf8();
        assertThat(request.getMethod()).isEqualTo("PUT");
        JSONAssert.assertEquals("{\"id\":1,\"name\":\"Pelota\",\"description\":\"pelota futbol\",\"price\":19.99}", requestBody, JSONCompareMode.LENIENT);

    }

    @Test
    @DisplayName("Given a product id, When finding a product on the repository, Then the product is returned")
    void saveProduct() {
        given(repository.save(productEntity)).willReturn(productEntity);

        Long id = repository.save(productEntity).getId();

        verify(repository).save(any());
        assertThat(id).isEqualTo(1L);
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
    void updateStock() {
        given(repository.findById(1L)).willReturn(Optional.of(productEntity));

        service.updateStock(100, 1L);

        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(1)).save(any());
    }

    @Test
    @DisplayName("Given a BigDecimal and a discount, When calling calculateFinalPrice, Then the result has to be equal to expectedParam")
    void calculateFinalPrice(){
        BigDecimal realParam = new BigDecimal("23.85");
        BigDecimal expectedParam = new BigDecimal("21.47");

        assertThat(service.calculateFinalPrice(realParam, 10)).isEqualByComparingTo(expectedParam);
    }

    @Test
    @DisplayName("Given a Product, When getting the discount, Then discount has to be 0")
    void getDiscount(){
        assertThat(service.getDiscount(productEntity)).isZero();
    }

    @Test
    @DisplayName("given a product id, when delete product by id, then the product is deleted")
    void deleteProductById() {
        //given
        given(repository.findById(anyLong())).willReturn(Optional.of(productEntity));
        //when
       service.deleteProductById(1L);
       //then
       verify(repository).findById(anyLong());
       verify(repository).deleteById(anyLong());
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
    @DisplayName("given a product id, when calling user api to delete favorite product, then returns 204 No Content.")
    void deleteUserProducts_returns204NoContent() throws InterruptedException {
        Long productId = 7L;
        when(servicesUrl.getUserUrl()).thenReturn("htpp://localhost:" + mockWebServer.getPort());

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(204));

        Mono<HttpStatus> userDeleteMono = service.deleteUserProducts(productId);

        StepVerifier.create(userDeleteMono)
                .expectComplete()
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("DELETE");
    }
}
