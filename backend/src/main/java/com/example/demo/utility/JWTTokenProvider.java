package com.example.demo.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.domain.UserPrincipal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
public class JWTTokenProvider {
    private final String authorizationHeader = HttpHeaders.AUTHORIZATION;
    private final String secretKey;
    public final String tokenPrefix;
    private final Integer tokenExpirationAfterMilliseconds;
    private final Integer refreshTokenExpirationAfterDays;

    public JWTTokenProvider(String secretKey, String tokenPrefix, Integer tokenExpirationAfterMilliseconds, Integer refreshTokenExpirationAfterDays) {
        this.secretKey = secretKey;
        this.tokenPrefix = tokenPrefix;
        this.tokenExpirationAfterMilliseconds = tokenExpirationAfterMilliseconds;
        this.refreshTokenExpirationAfterDays = refreshTokenExpirationAfterDays;
    }

    public String generateJwtToken(UserPrincipal userPrincipal, boolean refreshToken){
        // get claims from authenticated user
        List<String> claims = getClaimsFromUser(userPrincipal);
        // generate JWT token
        return JWT.create()
                .withIssuer("AppName")
                .withAudience("AppName Administration")
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim("authorities", claims.toArray(new String[0]))  // convert List< String> to String[]
                .withExpiresAt(new Date(System.currentTimeMillis() +
                        (refreshToken ? (refreshTokenExpirationAfterDays * 60 * 60 * 24 * 1000) : (tokenExpirationAfterMilliseconds))
                ))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String refreshToken(String token){
        try{
            getJWTVerifier().verify(token);
        }catch (TokenExpiredException e){
            // JWT is expired as expected
        }catch (Exception e){
            throw new JWTVerificationException("Malformed JWT. Please login");
        }
        DecodedJWT decodedRefreshToken = JWT.decode(token);
        Map<String, Claim> claims = decodedRefreshToken.getClaims();

        // return new access token
        return JWT.create()
                .withIssuer(claims.get("iss").asString())
                .withAudience(claims.get("aud").asString())
                .withSubject(decodedRefreshToken.getSubject())
                .withArrayClaim("authorities", claims.get("authorities").asArray(String.class))

                // set new issuedAt & expiration
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + (tokenExpirationAfterMilliseconds)))//(refreshTokenExpirationAfterDays * 60 * 60 * 24 * 1000)))

                .sign(Algorithm.HMAC512(secretKey));
    }

    public Set<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
        return Stream.of(getClaims(token).get("authorities"))
                .map(claims -> claims.asArray(String.class))
                .flatMap(strings -> Stream.of(strings))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public Authentication getAuthentication(String username, List< ? extends GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities); // credentials - null (already verified)
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return token;
    }

    public boolean isTokenValid(String token){
        String username;
        try{
            username = getJWTVerifier().verify(token).getSubject();
        }catch (TokenExpiredException | JWTDecodeException | SignatureVerificationException e){
            return false;
        }
        return ((username != null) && (username.trim() != "") && (!isTokenExpired(token)));
    }

    public String getSubject(String token){
        return getJWTVerifier().verify(token).getSubject();
    }

    public boolean isTokenExpired(String token){
        Date expiration = getJWTVerifier().verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private List< String> getClaimsFromUser(UserPrincipal userPrincipal) {
        List< String> authorities = new ArrayList<>();
        for(GrantedAuthority authority: userPrincipal.getAuthorities()){
            authorities.add(authority.getAuthority());
        }
        return authorities;
    }

    private Map<String, Claim> getClaims(String token){
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaims();
    }

    private JWTVerifier getJWTVerifier(){
        JWTVerifier verifier;
        try{
            Algorithm algo = Algorithm.HMAC512(secretKey);
            verifier = JWT.require(algo).withIssuer("AppName").build();
        }catch (JWTVerificationException e){
            throw new JWTVerificationException("Token cannot be verified");
        }
        return verifier;
    }
}