package com.ba.ssrj.security

import org.springframework.security.core.Authentication

/**
 * Manages all token related activities in a stateless token based authentication environment. 
 * Concrete implementations can choose to create, store and load tokens in any number of different ways as long as they
 * can confirm to the methods in this interface. 
 * 
 * Eg: 
 *  For JWT:
 *      Create stateless JWT (with expiry) from authentication and send it back in response header during login
 *      Read token from request header during subsequent requests and reconstruct the authentication without any other state
 *      Update the JWT for every request with new expiry date and sending back in the response header for client to store      
 *  For Bearer:
 *      Create an opaque token from authenticaation and store it in the database
 *      Read token from request header and use that to retrieve authentication (user) from database
 *      Update database with new expiry date after every request
 *      The storage can again be customized (cache, in-memory, database or session)
 *
 * @author Ganeshji Marwaha
 * @since 6/20/15
 */
public interface TokenManager {
    public static final String KEY_HEADER_TOKEN = "X-AUTH-TOKEN"
    
    public String createTokenFrom(Authentication authentication)
    public Authentication createAuthenticationFrom(String token)
}
