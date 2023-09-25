//package com.example.ali.service;
//
//import com.example.ali.dto.MessageResponseDto;
//import com.example.ali.dto.ReviewRequestDto;
//import com.example.ali.dto.ReviewResponseDto;
//import com.example.ali.entity.*;
//import com.example.ali.repository.OrdersRepository;
//import com.example.ali.repository.ProductRepository;
//import com.example.ali.repository.ReviewRepository;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ReviewServiceTest {
//    @InjectMocks
//    ReviewService service;
//    @Mock
//    ReviewRepository reviewRepository;
//
//    @Mock
//    OrdersRepository ordersRepository;
//
//    @Mock
//    ProductRepository productRepository;
//
//    @Nested
//    @DisplayName("댓글 조회")
//    class getProductsReview {
//        @Test
//        @DisplayName("[200] 댓글 조회")
//        void getProductReviews_Success() {
//            //given
//
//            //when
//
//            //then
//        }
//    }
//
//    @Nested
//    @DisplayName("댓글 생성")
//    class CreateReview {
//        @Mock
//        Orders orders;
//        private String comment;
//        private Integer rating;
//        @BeforeEach
//        void setup() {
//            comment = "여기에 댓글 내용.";
//            rating = 3;
//        }
//
//        @Test
//        @DisplayName("[201] 성공 케이스")
//        void createReview() {
//            ReviewService reviewService = new ReviewService(reviewRepository, ordersRepository, productRepository);
//
//            //given
//            ReviewRequestDto requestDto = new ReviewRequestDto(1L, comment, rating);
//
//            Review review = new Review(comment, rating);
//            User user = new User();
//            Optional<Orders> optionalOrders = Optional.of(orders);
//
//            // when
//            when(reviewRepository.save(any(Review.class))).thenReturn(review);
//            when(ordersRepository.findById(any(Long.class))).thenReturn(optionalOrders);
//            when(orders.getUser()).thenReturn(user);
//
//            //then
//            ResponseEntity<ReviewResponseDto> result = reviewService.createReview(requestDto, user);
//            assertThat(result.getBody().getComment()).isEqualTo(review.getComment());
//            assertThat(result.getBody().getRating()).isEqualTo(review.getRating());
//            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        }
//
//        @Test
//        @DisplayName("실패 케이스 - 주문 없음")
//        void createReview_fail1() {
//            ReviewService reviewService = new ReviewService(reviewRepository, ordersRepository, productRepository);
//            //given
//            ReviewRequestDto requestDto = new ReviewRequestDto(1L, comment, rating);
//            //when
//            when(ordersRepository.findById(any(Long.class))).thenReturn(Optional.empty());
//            //then
//            assertThrows(NullPointerException.class, () -> {
//                reviewService.createReview(requestDto, new User());
//            });
//        }
//
//        @Test
//        @DisplayName("실패 케이스 - 작성 권한 없음")
//        void createReview_fail2() {
//            ReviewService reviewService = new ReviewService(reviewRepository, ordersRepository, productRepository);
//
//            //given
//            User originUser =  new User();
//            User unAuthorizedUser =  new User();
//            ReviewRequestDto requestDto = new ReviewRequestDto(1L, comment, rating);
//            Optional<Orders> optionalOrders = Optional.of(orders);
//
//            //when
//            when(ordersRepository.findById(any(Long.class))).thenReturn(optionalOrders);
//            when(orders.getUser()).thenReturn(originUser);
//
//            //then
//            assertThrows(IllegalArgumentException.class, () -> {
//                reviewService.createReview(requestDto, unAuthorizedUser);
//            });
//        }
//    }
//
//    @Nested()
//    @DisplayName("리뷰 업데이트")
//    class UpdateReview {
//        String originComment = "comment";
//        Integer originRating = 3;
//
//        @Mock
//        Orders order;
//        @Test
//        @DisplayName("리뷰 내용 변경 성공")
//        void update_success () {
//            // given
//            String changedComment = "changedComment";
//            Integer changedRating = 3;
//
//            User user = new User();
//            Review review = new Review(1L, originComment, originRating, null, order); // 일부 데이터 Mock 주입 가능
//            ReviewRequestDto requestDto = new ReviewRequestDto(1L, changedComment, changedRating);
//
//            //when
//            when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
//            when(order.getUser()).thenReturn(user);
//
//            ResponseEntity<ReviewResponseDto> result = service.updateReview(requestDto, 1L, user);
//            ReviewResponseDto body = result.getBody();
//
//            //then
//            assertThat(body.getComment()).isEqualTo(changedComment);
//            assertThat(body.getRating()).isEqualTo(changedRating);
//        }
//        @Test
//        @DisplayName("싪패 - 리뷰 존재 X")
//        void update_fail1() {
//            //given
//            String changedComment = "changedComment";
//            Integer changedRating = 3;
//            ReviewRequestDto requestDto = new ReviewRequestDto(1L, changedComment, changedRating);
//
//            //when
//            when(reviewRepository.findById(1L)).thenReturn(Optional.empty());
//
//            //then
//            assertThrows(NullPointerException.class, () -> {
//                service.updateReview(requestDto, 1L, new User());
//            });
//        }
//
//        @Test
//        @DisplayName("실패 - 권한 없는 유저")
//        void update_fail2() {
//            //given
//            User owner = new User();
//            User notOwner = new User();
//            Review review = new Review(1L, originComment, originRating, null, order); // 일부 데이터 Mock 주입 가능
//            ReviewRequestDto requestDto = new ReviewRequestDto(1L, "아무거나", 1);
//
//            //when
//            when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
//            when(order.getUser()).thenReturn(owner);
//
//            //then
//            assertThrows(IllegalArgumentException.class, () -> {
//                service.updateReview(requestDto, 1L, notOwner);
//            });
//        }
//    }
//    @Nested
//    @DisplayName("리뷰 삭제")
//    class DeleteReview {
//        String comment = "comment";
//        Integer rating = 3;
//        @Mock
//        Orders order;
//        @Test
//        @DisplayName("[200] 성공 케이스")
//        void deleteReview() {
//            //given
//            Review review = new Review(1L, comment, rating, null, order); // 일부 데이터 Mock 주입 가능
//            User user = new User();
//
//            //when
//            when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
//            when(order.getUser()).thenReturn(user);
//
//            var result = service.deleteReview(1L, user);
//            MessageResponseDto body = result.getBody();
//
//            //then
//            assertThat(body.getMsg()).isEqualTo("삭제 성공");
//            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("리뷰 없음 -> 실패")
//        void delete_fail1() {
//            //given
//            Review review = new Review(1L, comment, rating, null, order); // 일부 데이터 Mock 주입 가능
//            User user = new User();
//
//            //when
//            when(reviewRepository.findById(1L)).thenReturn(Optional.empty());
//
//            //then
//            assertThrows(NullPointerException.class, () -> {
//                service.deleteReview(1L, user);
//            });
//        }
//
//        @Test
//        @DisplayName("권한 없음 -> 실패")
//        void delete_fail2() {
//            //given
//            Review review = new Review(1L, comment, rating, null, order); // 일부 데이터 Mock 주입 가능
//            User owner = new User();
//            User notOwner = new User();
//
//            //when
//            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
//            when(order.getUser()).thenReturn(owner);
//
//            //then
//            assertThrows(IllegalArgumentException.class, () -> {
//                service.deleteReview(1L, notOwner);
//            });
//        }
//    }
//
//}
