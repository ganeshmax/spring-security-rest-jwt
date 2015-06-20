package com.ba.ssrj.security

import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * - AccessDecisionManager raises an exception if authorization fails. 
 * - ExceptionTranslationFilter catches the exception and checks if the principal is authenticated or not. 
 *  - If principal is authenticated, but still cannot be authorized, forwards to configured AccessDeniedHandler
 *  - If principal is unauthenticated, forwards to configured AuthenticationEntryPoint
 *
 * Control is transferred here by the ExceptionTransationFilter once the Access is denied for a secure object. In a typical
 * scenario, this will send a 403 and forward to a error page (if configured). This is good enough for our purpose in
 * general. But, I wanted to send an additional JSON message in these scenarios. So, i have configured my own
 * AccessDeniedHandler so that i can return a status code and JSON message back to the REST client.
 *
 *
 * @author Ganeshji Marwaha
 * @since 8/24/14
 */
@Slf4j
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.debug("<<<<<" + "RestAccessDeniedHandler.handle" + ">>>>>");


        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.contentType = 'application/json';

        def builder = new JsonBuilder();
        builder.error {
            code (HttpServletResponse.SC_FORBIDDEN)
            message (accessDeniedException.getMessage())
        };

        PrintWriter writer = response.getWriter();
        writer.println(builder.toPrettyString());
    }
}
