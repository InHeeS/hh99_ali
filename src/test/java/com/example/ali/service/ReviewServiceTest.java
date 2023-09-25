package com.example.ali.service;

import com.example.ali.dto.*;
import com.example.ali.entity.*;
import com.example.ali.repository.OrdersRepository;
import com.example.ali.repository.ProductRepository;
import com.example.ali.repository.ReviewRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @InjectMocks
    ReviewService service;
    @Mock
    ReviewRepository reviewRepository;

    @Mock
    OrdersRepository ordersRepository;

    @Mock
    ProductRepository productRepository;

    String comment = "comment";
    Integer rating = 3;

    @Mock
    User user = new User(1L, new UserSignupRequestDto("바보", "1234",
            "123@123"), "1234", new UserWallet());
    User user2 = new User(1L, new UserSignupRequestDto("바보2", "1234",
            "123@1232"), "12342", new UserWallet());

    @Mock
    Seller seller = new Seller(
            1L, new SellerSignupRequestDto("셀러", "1234", "셀러스토어", "스토어입니다"),
            "1234", new SellerWallet());

    @Mock
    Product product = new Product(1L, new ProductRequestDto("축구공", 1000L, 999L, "축구공이다"),
            seller, "http://si");

    @Mock
    Orders order = new Orders(1L, new OrderRequestDto(1L, 10L), user, product);

    @Nested
    @DisplayName("댓글 생성")
    class CreateReview {



        @Test
        @DisplayName("[201] 성공 케이스")
        void createReview() {

            Review review = new Review(1L, comment, rating, null, order);

            //given
            ReviewRequestDto requestDto = new ReviewRequestDto(1L, comment, rating);

            // when
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewRepository.findByOrders_Id(any(Long.class))).thenReturn(Optional.empty());
            when(ordersRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(order));
            when(order.getUser()).thenReturn(user);

            //then
            ReviewResponseDto result = service.createReview(requestDto, user);
            assertThat(result.getComment()).isEqualTo(review.getComment());
            assertThat(result.getRating()).isEqualTo(review.getRating());
        }

        @Test
        @DisplayName("실패 케이스 - 주문 없음")
        void createReview_fail1() {

            //given
            ReviewRequestDto requestDto = new ReviewRequestDto(1L, comment, rating);
            //when
            when(ordersRepository.findById(any(Long.class))).thenReturn(Optional.empty());
            //then
            assertThrows(NullPointerException.class, () -> {
                service.createReview(requestDto, new User());
            });
        }

        @Test
        @DisplayName("실패 케이스 - 작성 권한 없음")
        void createReview_fail2() {

            //given
            ReviewRequestDto requestDto = new ReviewRequestDto(1L, comment, rating);
            Optional<Orders> optionalOrders = Optional.of(order);

            //when
            when(ordersRepository.findById(any(Long.class))).thenReturn(optionalOrders);
            when(order.getUser()).thenReturn(user);

            //then
            assertThrows(IllegalArgumentException.class, () -> {
                service.createReview(requestDto, user2);
            });
        }
    }

    @Nested()
    @DisplayName("리뷰 업데이트")
    class UpdateReview {

        @Test
        @DisplayName("리뷰 내용 변경 성공")
        void update_success () {
            // given
            String changedComment = "changedComment";
            Integer changedRating = 3;

            Review review = new Review(1L, comment, rating, null, order); // 일부 데이터 Mock 주입 가능
            ReviewRequestDto requestDto = new ReviewRequestDto(1L, changedComment, changedRating);

            //when
            when(reviewRepository.findByOrders_Id(anyLong())).thenReturn(Optional.of(review));
            when(order.getUser()).thenReturn(user);

            ReviewResponseDto result = service.updateReview(requestDto, user);

            //then
            assertThat(result.getComment()).isEqualTo(changedComment);
            assertThat(result.getRating()).isEqualTo(changedRating);
        }
        @Test
        @DisplayName("싪패 - 리뷰 존재 X")
        void update_fail1() {
            //given
            String changedComment = "changedComment";
            Integer changedRating = 3;
            ReviewRequestDto requestDto = new ReviewRequestDto(1L, changedComment, changedRating);

            //when
            when(reviewRepository.findByOrders_Id(1L)).thenReturn(Optional.empty());

            //then
            assertThrows(NullPointerException.class, () -> {
                service.updateReview(requestDto, user);
            });
        }

        @Test
        @DisplayName("실패 - 권한 없는 유저")
        void update_fail2() {
            //given

            Review review = new Review(1L, comment, rating, null, order); // 일부 데이터 Mock 주입 가능
            ReviewRequestDto requestDto = new ReviewRequestDto(1L, "아무거나", 1);

            //when
            when(reviewRepository.findByOrders_Id(anyLong())).thenReturn(Optional.of(review));
            when(order.getUser()).thenReturn(user);

            //then
            assertThrows(IllegalArgumentException.class, () -> {
                service.updateReview(requestDto, user2);
            });
        }
    }
    @Nested
    @DisplayName("리뷰 삭제")
    class DeleteReview {

        @Test
        @DisplayName("[200] 성공 케이스")
        void deleteReview() {
            //given
            Review review = new Review(1L, comment, rating, null, order); // 일부 데이터 Mock 주입 가능

            //when
            when(reviewRepository.findByOrders_Id(1L)).thenReturn(Optional.of(review));
            when(order.getUser()).thenReturn(user);

            MessageResponseDto result = service.deleteReview(1L, user);

            //then
            assertThat(result.getMsg()).isEqualTo("삭제 성공");
        }

        @Test
        @DisplayName("리뷰 없음 -> 실패")
        void delete_fail1() {
            //given
            Review review = new Review(1L, comment, rating, null, order); // 일부 데이터 Mock 주입 가능

            //when
            when(reviewRepository.findByOrders_Id(1L)).thenReturn(Optional.empty());

            //then
            assertThrows(NullPointerException.class, () -> {
                service.deleteReview(1L, user);
            });
        }

        @Test
        @DisplayName("권한 없음 -> 실패")
        void delete_fail2() {
            //given
            Review review = new Review(1L, comment, rating, null, order); // 일부 데이터 Mock 주입 가능

            //when
            when(reviewRepository.findByOrders_Id(1L)).thenReturn(Optional.of(review));
            when(order.getUser()).thenReturn(user);

            //then
            assertThrows(IllegalArgumentException.class, () -> {
                service.deleteReview(1L, user2);
            });
        }
    }
}