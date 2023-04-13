package com.gftraining.microservice_product.unit_test.services;


import com.gftraining.microservice_product.configuration.CategoriesConfig;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import com.gftraining.microservice_product.services.ProductService;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

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
    CategoriesConfig categoriesConfig;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

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
        given(webClientMock.delete()).willReturn(requestHeadersUriSpecMock);
        given(requestHeadersUriSpecMock.uri(anyString(),anyLong())).willReturn(requestHeadersSpecMock);
        given(requestHeadersSpecMock.retrieve()).willReturn(responseSpecMock);
        given(responseSpecMock.bodyToMono(
                ArgumentMatchers.<Class<Object>>notNull())).willReturn(Mono.just(cartsChanged));
        //spy
        //when
        service.deleteProductById(1L);

        //then
        verify(repository).findById(anyLong());
        verify(repository).deleteById(anyLong());
        verify(webClientMock).delete();
    }

    @Test
    @DisplayName("given a product id, when calling cart api to delete product, then returns Ok and number of carts affected.")
    void deleteProductFromCarts_returnCartsChanged(){
        given(webClientBuilder.build()).willReturn(webClientMock);
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.header(any(), any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.body(any(), any(Class.class)))
                .thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.exchange()).thenReturn(Mono.just(cartsChanged));

        Object response = service.deleteProductFromCarts(7L);
        /*
        given(webClientMock.delete()).willReturn(requestHeadersUriSpecMock);
        given(requestHeadersUriSpecMock.uri(anyString(),anyLong())).willReturn(requestHeadersSpecMock);
        given(requestHeadersSpecMock.retrieve()).willReturn(responseSpecMock);
        given(responseSpecMock.bodyToMono(
                ArgumentMatchers.<Class<Object>>notNull())).willReturn(Mono.just(cartsChanged));

        Object response = service.deleteProductFromCarts(7L);
        assertEquals("{cartsChanged=1}", response.toString());*/
    }
}
