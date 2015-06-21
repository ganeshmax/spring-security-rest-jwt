package com.ba.ssrj.security

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.security.web.savedrequest.SavedRequest

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Control is transferred here after successful authentication so that the user is redirected to a success URL. The
 * success URL maybe a default URL or a URL in the query parameter or in the saved Request. But, this is applicable
 * only for form-login scenario.
 * For REST scenario, we just return a 200 so that the client can continue to access their protected resource with a
 * new REST call.
 *
 * @author Ganeshji Marwaha
 * @since 8/24/14
 */
@Slf4j
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache()
    
    @Autowired
    private TokenManager tokenManager

    /**
     * Create a JWT token and return in response. This way, the client can capture the token and store it in some way
     * local to the client. In subsequent requests, the client will send this token in the X-AUTH-TOKEN header. 
     * 
     * The SecurityContextPersistenceFilter (the filter that comes very early in the filter chain), will retrieve 
     * this TOKEN from the request header, create a corresponding authentication object and set it in the 
     * SecurityContextHolder. All filters, web methods and method invocations from that point will consider the 
     * session to be authenticated with the correct principal.
     * 
     * TODO: Consider using Authorization HTTP header and send the JWT as Bearer token. This is more standard. 
     *
     * @param request
     * @param response
     * @param authentication
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        log.debug("<<<<<" + "RestAuthenticationSuccessHandler.onAuthenticationSuccess" + ">>>>>");

        // SavedRequest is only applicable for regular form-login. Remove it for REST login
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            requestCache.removeRequest(request, response);
        }

        // Do what a typical SuccessHandler will do to clean-up
        clearAuthenticationAttributes(request);

        // Send the token as response header
        response.addHeader(TokenManager.KEY_HEADER_TOKEN, tokenManager.createTokenFrom(authentication))
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}
