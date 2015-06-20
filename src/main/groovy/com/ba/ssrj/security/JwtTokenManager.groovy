package com.ba.ssrj.security

import groovy.json.JsonBuilder
import io.jsonwebtoken.Claims
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
public class JwtTokenManager {
    
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
        UserDetails userDetails = ((UserDetails) authentication.getPrincipal()) 
        
        String userName = userDetails.getUsername()
        String authorities = userDetails.getAuthorities().collect { it.authority }.join(",")

        String jwtToken = Jwts.builder()
                .setSubject(userName)
                .claim("authorities", authorities)
                .signWith(SignatureAlgorithm.HS512, jwtKey)
                .compact()

        def builder = new JsonBuilder()
        builder(token: jwtToken)

        return builder.toPrettyString()
    }

    /**
     * TODO: Error handling IMPORTANT
     * @param jwtToken
     * @return
     */
    public Authentication createAuthenticationFrom(String jwtToken) {
        try {
            Jwt jwt = Jwts.parser().setSigningKey(jwtKey).parse(jwtToken)

            DefaultClaims claims = (DefaultClaims) jwt.getBody()

            String userName = claims.getSubject()
            List<GrantedAuthority> authorities = claims.get(KEY_CLAIM_AUTHORITIES).toString().split(",").collect {
                return new SimpleGrantedAuthority(it.toString())
            }

            User principal = new User(userName, "", authorities)

            return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities())
            
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }

    
}
