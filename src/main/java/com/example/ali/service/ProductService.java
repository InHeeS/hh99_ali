package com.example.ali.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.ali.dto.MessageDataResponseDto;
import com.example.ali.dto.MessageResponseDto;
import com.example.ali.dto.ProductRequestDto;
import com.example.ali.dto.ProductResponseDto;
import com.example.ali.entity.Product;
import com.example.ali.entity.ProductStock;
import com.example.ali.entity.Seller;
import com.example.ali.repository.ProductRepository;
import com.example.ali.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String S3Bucket;

    @Transactional
    public ResponseEntity<?> createProduct(ProductRequestDto requestDto, Seller seller, MultipartFile image) throws IOException{

        String imageUrl = getImage(image);
        Product product = new Product(requestDto, seller, imageUrl);
        ProductStock productStock = new ProductStock(requestDto.getStock(), product);
        product.setProductStock(productStock);

        Product newProduct = productRepository.save(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageDataResponseDto("상품 등록 성공", new ProductResponseDto(newProduct)));
    }

    @Transactional
    public ResponseEntity<?> updateProduct(Long productId, ProductRequestDto requestDto) {
        Product product = findProductById(productId);
        product.update(requestDto);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new MessageDataResponseDto("상품 수정 성공", new ProductResponseDto(product)));
    }

    @Transactional
    public ResponseEntity<?> deleteProduct(Long productId) {
        Product product = findProductById(productId);
        productRepository.delete(product);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponseDto("상품 삭제 성공"));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getSearchProduct(String keyword) {

        List<Product> productList = new ArrayList<>();

        productList = productRepository.findAllByProductNameLike(keyword);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productList.stream().map(ProductResponseDto::new).toList());
    }

    public ResponseEntity<?> getProducts(){

        List<Product> productList = productRepository.findAll();

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(productList.stream().map(ProductResponseDto::new).toList());
    }


    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product 찾을 수 없음"));
    }

    private String getImage(MultipartFile image) throws IOException {
        String originName = UUID.randomUUID().toString();
        long size = image.getSize();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(image.getContentType());
        objectMetadata.setContentLength(size);

        amazonS3Client.putObject(
                new PutObjectRequest(S3Bucket, originName, image.getInputStream(), objectMetadata )
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3Client.getUrl(S3Bucket, originName).toString();
    }

}
