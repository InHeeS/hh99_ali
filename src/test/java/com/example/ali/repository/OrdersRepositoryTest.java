package com.example.ali.repository;

import com.example.ali.entity.OrderStatus;
import com.example.ali.entity.Orders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("OrdersRepository 테스트")
class OrdersRepositoryTest {

    @Autowired
    private OrdersRepository ordersRepository;


    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // Given

        // When
        List<Orders> orders = ordersRepository.findAll();

        // Then
        assertThat(orders)
                .isNotNull()
                .hasSize(orders.size());
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        // Given
        long previousCount = ordersRepository.count();
        Orders orders = ordersRepository.findById(1L).orElseThrow(IllegalArgumentException::new);

        // When
        ordersRepository.delete(orders);

        // Then
        assertThat(ordersRepository.count()).isEqualTo(previousCount - 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        // Given
        Orders orders = ordersRepository.findById(1L).orElseThrow(IllegalArgumentException::new);
        orders.changeDeliveryStatus(OrderStatus.DELIVERED);

        // When
        ordersRepository.save(orders);

        // Then
        assertThat(ordersRepository.findById(1L).orElseThrow(IllegalArgumentException::new).getOrderStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    // TODO : 데이터 생성 TEST 필요
}


