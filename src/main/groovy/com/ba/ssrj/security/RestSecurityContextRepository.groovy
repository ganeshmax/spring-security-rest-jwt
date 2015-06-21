package com.ba.ssrj.security

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpRequestResponseHolder
import org.springframework.security.web.context.SecurityContextRepository

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Typically, a rest client would login to the the app using the /rest/login URL. In this setup, we are using the 
 * UsernamePasswordAuthenticationFilter to perform authentication using regular authentication managers. 
 * We have overridden the configuration of the UPAFilter to return a JWT token in the response when the user has 
 * logged in. The client is expected to save the token and send them in subsequent requests. 
 * 
 * This repository is supposed to save the SecurityContext to/from session to SecurityContextHolder. In typical form-login
 * scenario, this would save the SecurityContext to session when the request completes and retrieve it from session and 
 * place it into SCHolder on new request. 
 * 
 * In our case, we don't have a session (stateless). We plan to use the JWT token itself to re-create the SC. So, the 
 * saveContext method becomes a no-op. The loadContext method loads the details from JWT token (after verifying its 
 * signature) and populates an authentication and subsequently, a SecurityContext. This will happen on every request. 
 * Then it will place this authentication into SCHolder. 
 * 
 * Let's say the user supplies a valid token. Then, the correct authentication token will be placed in SCH and all other 
 * filters know that a authenticated token is present in the SCHolder and would allow the request to go through. 
 * 
 * Let's say the user supplies an invalid token. Then, the SCHolder will be set with an empty context. So, all other 
 * filters (especially AbstractSecurityInterceptor with AccessDecisionManager) will not allow the request to go through 
 * if the protected resource requires authentication or some authorities. It will throw an exception, which will be 
 * caught by the ExceptionTransactionFilter, which will invoke the authenticationEntryPoint, which will send an 
 * "unauthorized" response back to the user. 
 * This scenario could happen when
 * - user was never logged in, so doesn't have a token
 * - user is trying to login with an expired token (NOT IMPLEMENTED YET)
 * - user is trying to login with wrong token
 *
 * @author Ganeshji Marwaha
 * @since 6/20/15
 */
@Slf4j
public class RestSecurityContextRepository implements SecurityContextRepository {
    
    @Autowired
    private TokenManager tokenManager

    /**
     * On every request entry, load the token from request, construct an authentication from it, 
     * construct a security context from it and place it in the SecurityContextHolder
     *  
     * @param requestResponseHolder
     * @return
     */
    @Override
    SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        log.debug("<<<<<" + "RestSecurityContextRepository.loadContext" + ">>>>>")
        
        SecurityContext context = SecurityContextHolder.createEmptyContext()
        
        // If token is not in the request, set an empty context
        String jwtToken = requestResponseHolder.getRequest().getHeader(TokenManager.KEY_HEADER_TOKEN)
        if(jwtToken == null) {
            return context 
        }
        
        // If token was there in the request, but couldn't decode, set an empty context
        Authentication authentication = tokenManager.createAuthenticationFrom(jwtToken)
        if(authentication == null) {
            return context
        }
        
        // If token was there in the request and could decode to authentication, set context with authentication
        context.setAuthentication(authentication)
        return context
    }

    /**
     * On every request exit, send the JWT token back in the response header. 
     * This is useful if we update the expiry date of the token every time a request is made
     * TODO: currently, the expiry date is neither updated nor verified. implement that. 
     * 
     * @param context
     * @param request
     * @param response
     */
    @Override
    void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        log.debug("<<<<<" + "RestSecurityContextRepository.saveContext" + ">>>>>")
        
        // Response header could already have the token if this is the login processing URL
        if(response.getHeader(TokenManager.KEY_HEADER_TOKEN) == null) {
            response.addHeader(TokenManager.KEY_HEADER_TOKEN, tokenManager.createTokenFrom(context.authentication))
        }
    }

    /**
     * This method is called by spring to check if the repository contains a context.
     * TODO: verify exactly how this is being used. I spotted a situation that is not very right if i return true, 
     * from a JWT based auth perspective; so i am returning false always. Verify again and correct this
     * @param request
     * @return
     */
    @Override
    boolean containsContext(HttpServletRequest request) {
        log.debug("<<<<<" + "RestSecurityContextRepository.containsContext" + ">>>>>")
        return false
    }
}
