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
    
    private static final String REQUEST_HEADER_TOKEN = "X-AUTH-TOKEN"
    
    @Autowired
    private JwtTokenManager tokenManager

    @Override
    SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        log.debug("RestSecurityContextRepository.loadContext")
        
        SecurityContext context = SecurityContextHolder.createEmptyContext()
        
        // If token is not in the request, set an empty context
        String jwtToken = requestResponseHolder.getRequest().getHeader(REQUEST_HEADER_TOKEN)
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

    @Override
    void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        log.debug("RestSecurityContextRepository.saveContext")
        // No need to save security context for REST scenarios using JWT.
        // This is because the JWT token was given back to the client during login and the client is expected to 
        // store the token locally and return it with every request from that point. 
        // The token itself will contain the userName and authorities that can be used to re-create the security 
        // context statelessly for every request using loadContext above.
    }

    @Override
    boolean containsContext(HttpServletRequest request) {
        log.debug("RestSecurityContextRepository.containsContext")
        return false
    }
}
