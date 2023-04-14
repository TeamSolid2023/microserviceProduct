package com.gftraining.microservice_product.unit_test.services;


import com.gftraining.microservice_product.configuration.Categories;
import com.gftraining.microservice_product.configuration.ServicesUrl;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import com.gftraining.microservice_product.services.ProductService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    Categories categories;
    @Mock
    private ServicesUrl servicesUrl;
   /* @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;
    @Mock
    private WebClient.Builder webClientBuilderMock;*/

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
    String cartsChanged = "{cartsChanged=1}";
    public static MockWebServer mockWebServer;

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
    void testGetAll() {
        given(repository.findAll()).willReturn(productList);
        assertThat(service.getAll()).isEqualTo(productList);
    }

    @Test
    @DisplayName("given a product id, when delete product by id, then the product is deleted")
    void deleteProductById() {
        //given
        given(repository.findById(anyLong())).willReturn(Optional.of(productEntity));
        //spy
        //when
       service.deleteProductById(1L);

       //then
       verify(repository).findById(anyLong());
       verify(repository).deleteById(anyLong());
    }

    @Test
    @DisplayName("given a product id, when calling cart api to delete product, then returns Ok and number of carts affected.")
    void deleteProductFromCarts_returnCartsChanged(){
        Long productId = 7L;
        when(servicesUrl.getCartUrl()).thenReturn("htpp://localhost:" + mockWebServer.getPort());

        /*when(webClientBuilderMock.baseUrl(anyString()).build()).thenReturn(webClientMock);

        when(webClientMock.delete()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriMock.uri("/products/{productId}", productId))
                .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve())
                .thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Object.class))
                .thenReturn(Mono.just(cartsChanged));*/
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(cartsChanged)
                .addHeader("Content-Type", "application/json"));


        Mono<Object> cartsMono = service.deleteProductFromCarts(productId);

        StepVerifier.create(cartsMono)
                .expectNextMatches(response -> response
                        .equals(cartsChanged))
                .verifyComplete();
    }

    @Test
    void saveProduct() {
        given(repository.save(productEntity)).willReturn(productEntity);
        Long id = repository.save(productEntity).getId();
        verify(repository).save(any());
        assertEquals(1L, id);
    }

    @Test
    @DisplayName("given a product id, when finding a product on the repository, then the product is returned")
    void getProductById() {
        given(repository.findById(anyLong())).willReturn(Optional.of(productEntity));

        assertThat(service.getProductById(1L)).isEqualToComparingFieldByFieldRecursively(productEntity);
    }

    @Test
    @DisplayName("given a product name, when finding products on the repository by name, then a list of products with that name is returned")
    void getProductByName() {
        given(repository.findAllByName(anyString())).willReturn(productListSameName);

        assertThat(service.getProductByName("Playmobil")).isEqualTo(productListSameName);
    }

    @Test
    void updateDatabase() throws IOException {
        //Put your own path
        service.updateProductsFromJson("C:\\Files\\data.json");

        verify(repository).deleteAll();
        verify(repository).saveAll(any());
    }

    @Test
    void putProductById() {
        Map<String, Integer> cat = new HashMap<>();
        cat.put("Juguetes", 20);

        given(repository.findById(1L)).willReturn(Optional.of(productEntity));
        given(categories.getCategories()).willReturn(cat);

        service.putProductById(productDTO, 1L);

        verify(repository).findById(anyLong());
        verify(repository).save(any());
    }

    @Test
    void updateStock() {
        given(repository.findById(1L)).willReturn(Optional.of(productEntity));

        service.updateStock(100, 1L);

        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(1)).save(any());
    }

    @Test
    void calculateFinalPrice(){
        BigDecimal realParam = new BigDecimal("23.85");
        BigDecimal expectedParam = new BigDecimal("21.47");

        assertThat(service.calculateFinalPrice(realParam, 10)).isEqualByComparingTo(expectedParam);
    }

    @Test
    void getDiscount(){
        assertThat(service.getDiscount(productEntity)).isZero();
    }
}
