package com.ba.ssrj.security

import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * - AccessDecisionManager raises an exception if authorization fails.
 * - ExceptionTranslationFilter catches the exception and checks if the principal is authenticated or not. 
 *  - If principal is authenticated, but still cannot be authorized, forwards to configured AccessDeniedHandler
 *  - If principal is unauthenticated, forwards to configured AuthenticationEntryPoint
 *
 * Control will be transferred here if the client requests a resource that is supposed to be authenticated, but is not.
 * In form-login scenario, we will be redirected to a login URL so that the user can enter login credentials.
 * But, in REST scenario, that does not make sense. So, we just return 401 and a message so that the rest client can
 * login before accessing the protected resource.
 *
 * @author Ganeshji Marwaha
 * @since 8/24/14
 */
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.debug("<<<<<" + "RestAuthenticationEntryPoint.commence" + ">>>>>");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.contentType = 'application/json';

        def builder = new JsonBuilder();
        builder.error {
            code (HttpServletResponse.SC_UNAUTHORIZED)
            message (authException.getMessage())
        };

        PrintWriter writer = response.getWriter();
        writer.println(builder.toPrettyString());
    }
}
