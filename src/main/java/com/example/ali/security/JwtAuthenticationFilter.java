package com.example.ali.security;

import com.example.ali.dto.LoginRequestDto;
import com.example.ali.dto.MessageResponseDto;
import com.example.ali.dto.TokenDto;
import com.example.ali.entity.RefreshToken;
import com.example.ali.jwt.JwtUtil;
import com.example.ali.repository.RefreshTokenRepository;
import com.example.ali.service.SellerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j(topic = "로그인 및 JWT 생성")
//authfilter,loggingfilter 대신 편리하게 사용
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private CustomLoginService customLoginService;
//    private UserDetailsServiceImpl userDetailsService;
//    @Autowired
//    private SellerDetailsServiceImpl sellerDetailsService;


    public JwtAuthenticationFilter(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;

        List<RequestMatcher> requestMatchers = new ArrayList<>();
        requestMatchers.add(new AntPathRequestMatcher("/auth/user/login"));
        requestMatchers.add(new AntPathRequestMatcher("/auth/seller/login"));

        this.setRequiresAuthenticationRequestMatcher(new OrRequestMatcher(requestMatchers));
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");

        try {
            // 요청 본문이 비어 있는지 확인
//            if (request.getContentLength() == 0) {
//                return null;
////                throw new RuntimeException("요청 본문이 비어 있습니다.");
//            }
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            String userType = requestDto.getUserType();

//             userType 검증
            if ("SELLER".equals(userType)) {
                UserDetails sellerDetails = customLoginService.loadUserByUsername(requestDto.getUsername());
                return getAuthenticationManager().authenticate(
                        new UsernamePasswordAuthenticationToken(
                                requestDto.getUsername(),
                                requestDto.getPassword(),
                                null
                        )
                );
            } else {
                UserDetails userDetails = customLoginService.loadUserByUsername(requestDto.getUsername());
                return getAuthenticationManager().authenticate( // AuthenticationManager로 인증 시도 후 성공하면 Authentication 객체 리턴
                        new UsernamePasswordAuthenticationToken( // UsernamePasswordAuthenticationToken 객체를 Authentication 객체로 인증 시도
                                requestDto.getUsername(), // principal
                                requestDto.getPassword(), // credentials
                                null
                        )
                );
            }
        } catch (IOException e) {
            log.error("예외 발생: ", e);
            throw new RuntimeException("요청 처리 중 오류가 발생했습니다.");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");

        Object principal = authResult.getPrincipal();
        String username;
        String userType;

        if (principal instanceof UserDetailsImpl userDetails) {
            username = userDetails.getUsername();
            userType = "USER"; // 또는 UserDetailsImpl 클래스에 userType 필드를 추가하여 userType = userDetails.getUserType(); 로 설정
        } else if (principal instanceof SellerDetailsImpl sellerDetails) {
            username = sellerDetails.getUsername();
            userType = "SELLER"; // 또는 SellerDetailsImpl 클래스에 userType 필드를 추가하여 userType = sellerDetails.getUserType(); 로 설정
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }

        // 아이디 정보로 token 생성
        TokenDto tokenDto = jwtUtil.createAllToken(username, userType);

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUsername(username);

        System.out.println("값 가져오는지?" + refreshToken);

        if(refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        } else {
            RefreshToken newRefreshToken = new RefreshToken(tokenDto.getRefreshToken(),username);
            refreshTokenRepository.save(new RefreshToken(tokenDto.getRefreshToken(),username));
        }
        setHeader(response, tokenDto);

//        String token = jwtUtil.createToken(username, role);
//        jwtUtil.addJwtToCookie(token, response);

        // 로그인 성공 메세지 전달
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        MessageResponseDto message = new MessageResponseDto( "로그인 성공");
        response.getWriter().write(new ObjectMapper().writeValueAsString(message));
    }
    private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);

        // 오류 메세지 전달
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        MessageResponseDto message = new MessageResponseDto("로그인 실패");
        response.getWriter().write(new ObjectMapper().writeValueAsString(message));
        // 추가
//        throw new UsernameNotFoundException("회원을 찾을 수 없습니다.");
    }
}
