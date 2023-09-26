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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrdersRepository ordersRepository;

    @InjectMocks
    ReviewService reviewService;

    @Nested
    @DisplayName("리뷰 성공 케이스")
    class SuccessCase {
        @Mock
        Orders order;

        @Mock
        User user;

        ReviewRequestDto originReviewDto = new ReviewRequestDto(1L, "this is comment", 1);
        Review review;
        @BeforeEach()
        void  beforeEach() {
            review = new Review(originReviewDto, order);
        }
        @Test
        void 생성_성공() {
            //given

            //when
            when(ordersRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
            when(order.getUser()).thenReturn(user);
            when(reviewRepository.findByOrders_Id(anyLong())).thenReturn(Optional.empty());
            when(reviewRepository.save(any(Review.class))).thenReturn(review);

            ReviewResponseDto result = reviewService.createReview(originReviewDto,user);
            //then
            verify(reviewRepository, times(1)).save(any(Review.class));// save가 1번 실행되어야 한다.

            assertThat(result.getComment()).isEqualTo(originReviewDto.getComment());
            assertThat(result.getRating()).isEqualTo(originReviewDto.getRating());
        }

        @Test
        void 변경_성공() {
            //given
            String editedText = "edited";
            Integer editedRating  = 2;
            ReviewRequestDto dto = new ReviewRequestDto(1L, editedText, editedRating);
            Review originReview = new Review(originReviewDto, order);

            //when
            when(reviewRepository.findByOrders_Id(1L)).thenReturn(Optional.of(originReview));
            when(order.getUser()).thenReturn(user);

            //then
            ReviewResponseDto result = reviewService.updateReview(dto, user);

            assertThat(result.getComment()).isEqualTo(editedText);
            assertThat(result.getRating()).isEqualTo(editedRating);
        }

        @Test
        void 삭제_성공() {
            //given
            String successMessage = "삭제 성공";

            //when
            when(reviewRepository.findByOrders_Id(anyLong())).thenReturn(Optional.of(review));
            when(order.getUser()).thenReturn(user);

            //then
            MessageResponseDto result = reviewService.deleteReview(anyLong(), user);

            verify(reviewRepository, times(1)).delete(any(Review.class));
            assertThat(result.getMsg()).isEqualTo(successMessage);
        }
    }

    @Nested
    @DisplayName("리뷰 실패 케이스")
    class FailCase {
        @Mock
        Orders order;
        @Mock
        User user;
        ReviewRequestDto requestDto = new ReviewRequestDto(1L, "this is comment", 1);
        Review review;
        @BeforeEach()
        void beforeEach() {
            review = new Review(requestDto, order);
        }

        @Test
        void 생성_주문건없음() {
            //given

            //when
            when(ordersRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            assertThrows(NullPointerException.class, () -> {
                reviewService.createReview(requestDto, user);
            });
            verify(ordersRepository, times(1)).findById(anyLong());
        }

        @Test
        void 생성_작성권한없음() {
            //given
            User unAuthorizedUser = new User();
            //when
            when(ordersRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
            when(order.getUser()).thenReturn(user);

            // then
            assertThrows(IllegalArgumentException.class, ()-> {
                reviewService.createReview(requestDto, unAuthorizedUser);
            });
        }

        @Test
        void 생성_작성한리뷰존재() {
            //given

            //when
            when(ordersRepository.findById(anyLong())).thenReturn(Optional.of(order));
            when(order.getUser()).thenReturn(user);
            when(reviewRepository.findByOrders_Id(anyLong())).thenReturn(Optional.of(review));

            //then
            assertThrows(IllegalArgumentException.class, () -> {
                reviewService.createReview(requestDto, user);
            });
        }

        @Test
        void 수정_리뷰존재X() {
            //given

            //when
            when(reviewRepository.findByOrders_Id(anyLong())).thenReturn(Optional.empty());
            //then
            assertThrows(NullPointerException.class,()-> {
                reviewService.updateReview(requestDto, user);
            });
        }

        @Test
        void 수정_권한없음() {
            //given
            User unAuthorizedUser = new User();
            //when
            when(reviewRepository.findByOrders_Id(anyLong())).thenReturn(Optional.of(review));
            when(review.getOrders().getUser()).thenReturn(user);

            //then
            assertThrows(IllegalArgumentException.class,()-> {
                reviewService.updateReview(requestDto, unAuthorizedUser);
            });
        }
        @Test
        void 삭제_리뷰없음() {
            //given
            //when
            when(reviewRepository.findByOrders_Id(anyLong())).thenReturn(Optional.empty());
            //then
            assertThrows(NullPointerException.class, () -> {
                reviewService.deleteReview(1L, user);
            });
            verify(reviewRepository, times(0)).delete(any(Review.class));
        }

        @Test
        void 삭제_권한없음() {
            //given
            User unAuthUser = new User();
            //when
            when(reviewRepository.findByOrders_Id(anyLong())).thenReturn(Optional.of(review));
            when(review.getOrders().getUser()).thenReturn(user);
            //then
            assertThrows(IllegalArgumentException.class, () -> {
                reviewService.deleteReview(anyLong(), unAuthUser);
            });
            verify(reviewRepository, times(0)).delete(any(Review.class));
        }
    }
    
    @Nested
    @DisplayName("리뷰 조회 테스트")
    class Read_Reviews {

        @Test
        @DisplayName("조회 - 상품 존재 X")
        void 조회_실패_상품없음() {
            //given
            //when
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            //then
            assertThrows(IllegalArgumentException.class, () -> {
                reviewService.getProductReviewList(anyLong());
            });
            verify(ordersRepository, times(0)).findAllByProduct(any(Product.class));
            verify(reviewRepository, times(0)).findAllByOrdersIn(anyList());
        }
    }
}