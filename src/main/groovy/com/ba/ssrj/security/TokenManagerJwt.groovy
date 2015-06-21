package com.ba.ssrj.security

import groovy.time.TimeCategory
import groovy.util.logging.Slf4j
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

/**
 * Manages a JwtToken
 *
 * @author Ganeshji Marwaha
 * @since 6/20/15
 */
@Component
@Slf4j
public class TokenManagerJwt implements TokenManager {
    
    @Value('${app.jwt.key}')
    private String jwtKey

    private static final String KEY_CLAIM_AUTHORITIES = "authorities"
    
    /**
     * Create a JWT Token based on the logged in user
     * Create a JSON string with "token" to be sent in the response and return
     * @param authentication
     * @return
     */
    public String createTokenFrom(Authentication authentication) {
        log.debug("<<<<<" + "TokenManagerJwt.createTokenFrom(authentication)" + ">>>>>")
        if(authentication == null) return null;
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal()
        
        String userName = userDetails.getUsername()
        String authorities = userDetails.getAuthorities().collect { it.authority }.join(",")

        String jwtToken = Jwts.builder()
                    .setSubject(userName)
                    .claim("authorities", authorities)
                    .setExpiration(1.minute.from.now)
                    .signWith(SignatureAlgorithm.HS512, jwtKey)
                    .compact()

        return jwtToken
    }

    /**
     * Parse JWT token and return an authentication from it. The returned authentication should be an authenticated
     * authentication object
     * @param token
     * @return
     */
    public Authentication createAuthenticationFrom(String token) {
        log.debug("<<<<<" + "TokenManagerJwt.createAuthenticationFrom(token)" + ">>>>>")
        if(token == null || token.empty) return null
        
        try {
            // Token parsing error, expiry etc are accounted for here
            Jwt jwt = Jwts.parser().setSigningKey(jwtKey).parse(token)

            DefaultClaims claims = (DefaultClaims) jwt.getBody()

            String userName = claims.getSubject()
            List<GrantedAuthority> authorities = claims.get(KEY_CLAIM_AUTHORITIES).toString().split(",").collect {
                return new SimpleGrantedAuthority(it.toString())
            }

            User principal = new User(userName, "", authorities)
            UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities())
            
            return authentication
        } catch (Exception e) {
            log.error("Error while parsing JWT token", e)
            return null
        }
    }

    
}
