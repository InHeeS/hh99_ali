package com.example.ali.jwt;

import com.example.ali.dto.TokenDto;
import com.example.ali.entity.RefreshToken;
import com.example.ali.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    //쿠키를 직접 만들어서 토큰을 담아 쿠키를 Response 객체에 담아 반환

    // JWT 데이터
    // Header KEY 값, Header name, 권환 이름 (user or admin)
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY, 권한을 구분하기 위함
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long ACCESS_TIME = 60 * 60 * 1000L; // 1시간
    private final long REFRESH_TIME  = 60 * 60 * 1000L * 24; // 1일

    public static final String ACCESS_TOKEN = "Access_Token";
    public static final String REFRESH_TOKEN = "Refresh_Token";

    private static final String AUTHORITY = "Empty";

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey; //jwt.secret.key
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    private final RefreshTokenRepository refreshTokenRepository;

    // 생성자 호출 뒤에 실행, 요청의 반복 호출 방지
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_TOKEN, accessToken);
    }

    // 리프레시 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader(REFRESH_TOKEN, refreshToken);
    }

    // header 토큰을 가져오는 기능
    public String getHeaderToken(HttpServletRequest request, String type) {
        return type.equals("Access") ? request.getHeader(ACCESS_TOKEN) :request.getHeader(REFRESH_TOKEN);
    }

    // 토큰 생성
    public TokenDto createAllToken(String username, String userType) {
        return new TokenDto(createToken(username, userType,"Access"), createToken(username, userType,"Refresh"));
    }


    public String getUserTypeFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userType", String.class);
        } catch (Exception e) {
            log.error(e.getMessage() + "getUserTypeFromToken");
            return "guest";
        }
    }

    public String getUserNameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null; // 또는 적절한 예외 처리
        }
    }

    //JWT 생성
    //토큰 생성
    public String createToken(String username,String userType,String tokenType) {
        Date date = new Date();

        long token = (tokenType.equals("Access")) ? ACCESS_TIME : REFRESH_TIME;
        // Access Token
        return Jwts.builder()
            .setSubject(username) // 사용자 식별자값(ID)
            .claim(AUTHORIZATION_KEY, AUTHORITY) // key 값으로 꺼내어 쓸 수 있다.
                .claim("userType",userType)
            .setExpiration(new Date(date.getTime() + token)) // 만료 시간
            .setIssuedAt(date) // 발급일
            .signWith(key, signatureAlgorithm) // 암호화 알고리즘
            .compact();
    }

    // refreshToken 토큰 검증
    // db에 저장되어 있는 token과 비교
    // db에 저장한다는 것이 jwt token을 사용한다는 강점을 상쇄시킨다.
    // db 보다는 redis를 사용하는 것이 더욱 좋다. (in-memory db기 때문에 조회속도가 빠르고 주기적으로 삭제하는 기능이 기본적으로 존재합니다.)
    public Boolean refreshTokenValidation(String token) {

        // 1차 토큰 검증
        if(!validateToken(token)) return false;

        // DB에 저장한 토큰 비교
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUsername(getUsernameFromToken(token));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }


    // 토큰에서 email 가져오는 기능
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
    public String getUserRoleFromToken(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get(AUTHORIZATION_KEY).toString();

    }

    // 토큰 검증, JWT 위변조 확인
    // parseBuilder() : 구성 성분을 분해하고 분석
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    // Payload 부분에는 토큰에 담긴 정보
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

}
