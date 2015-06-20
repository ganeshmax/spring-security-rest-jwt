package com.ba.ssrj.security

import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Control is transferred here after an unsuccessful authentication so that the user is redirected to a failure URL.
 * But, this is applicable only for form-login scenario.
 *
 * For REST scenario, we just return a 401 so that the REST client can retry or prompt the user to correct the
 * supplied credentials
 *
 * @author Ganeshji Marwaha
 * @since 8/24/14
 */
@Slf4j
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    void onAuthenticationFailure(HttpServletRequest request,
                                 HttpServletResponse response,
                                 AuthenticationException exception) throws IOException, ServletException {

        log.debug("<<<<<" + "RestAuthenticationFailureHandler.onAuthenticationFailure" + ">>>>>");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.contentType = 'application/json';

        def builder = new JsonBuilder();
        builder.error {
            code (HttpServletResponse.SC_UNAUTHORIZED)
            message (exception.getMessage())
        };

        PrintWriter writer = response.getWriter();
        writer.println(builder.toPrettyString());
    }
}
