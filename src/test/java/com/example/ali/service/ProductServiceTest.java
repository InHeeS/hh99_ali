package com.example.ali.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.ali.dto.MessageDataResponseDto;
import com.example.ali.dto.ProductRequestDto;
import com.example.ali.dto.ProductResponseDto;
import com.example.ali.entity.Product;
import com.example.ali.entity.ProductStock;
import com.example.ali.entity.Seller;
import com.example.ali.repository.ProductRepository;
import com.example.ali.repository.ProductStockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    ProductStockRepository productStockRepository;
    @Mock
    AmazonS3 amazonS3Client;


    @Test
    @DisplayName("상품 생성 성공")
    void createProduct() throws IOException {
        ProductService productService = new ProductService(productRepository, productStockRepository, amazonS3Client);

        MockMultipartFile image = new MockMultipartFile(
                "image", "image.jpg", "image/jpeg", "image".getBytes());

        //given
        ProductRequestDto requestDto = new ProductRequestDto("축구공", 100L, 10L, "둥글함");
        Seller seller = new Seller();
        Product product = new Product(requestDto, seller, "https://www.naver.com/");
        ProductStock productStock = new ProductStock(requestDto.getStock(), product);
        product.setProductStock(productStock);

        //when
        when(amazonS3Client.getUrl(isNull(), anyString())).thenReturn(new URL("https://www.naver.com/"));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        MessageDataResponseDto result = productService.createProduct(requestDto, seller, image);
        ProductResponseDto productResponseDto = (ProductResponseDto)result.getData();

        //then
        Assertions.assertThat(productResponseDto.getProductName()).isEqualTo("축구공");
        Assertions.assertThat(productResponseDto.getInfo()).isEqualTo("둥글함");
        Assertions.assertThat(productResponseDto.getPrice()).isEqualTo(100);
        Assertions.assertThat(productResponseDto.getStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("제품 수정 성공")
    void updateProduct() {
        //given
        ProductService productService = new ProductService(productRepository, productStockRepository, amazonS3Client);
        ProductRequestDto requestDto1 = new ProductRequestDto("축구공", 100L, 10L, "둥글함");
        ProductRequestDto requestDto2 = new ProductRequestDto("수정된축구공", 120L, 11L, "부드러움");
        Product product = new Product(requestDto1, new Seller(), "https://si/");
        ProductStock productStock = new ProductStock(requestDto1.getStock(), product);
        product.setProductStock(productStock);

        //when
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        MessageDataResponseDto result = productService.updateProduct(1L, requestDto2);
        ProductResponseDto productResponseDto = (ProductResponseDto) result.getData();

        //then
        Assertions.assertThat(productResponseDto.getProductName()).isEqualTo("수정된축구공");
        Assertions.assertThat(productResponseDto.getInfo()).isEqualTo("부드러움");
        Assertions.assertThat(productResponseDto.getPrice()).isEqualTo(120);
        Assertions.assertThat(productResponseDto.getStock()).isEqualTo(11);
    }

    @Test
    @DisplayName("제품 삭제 성공")
    void deleteProduct() {
        //given
        ProductService productService = new ProductService(productRepository, productStockRepository, amazonS3Client);
        ProductRequestDto requestDto1 = new ProductRequestDto("축구공", 100L, 10L, "둥글함");
        Product product = new Product(requestDto1, new Seller(), "https://si/");
        product.setProductStock(new ProductStock());

        //when
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        MessageDataResponseDto result = productService.deleteProduct(1L);

        //then
        Assertions.assertThat(result.getMsg()).isEqualTo("상품 삭제 성공");
    }

}
