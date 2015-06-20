package com.ba.ssrj.security

import groovy.util.logging.Slf4j
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Control is transferred here once the user logs out of the application by visiting /logout URL.
 * By default, form-login configuration redirects the user back to the login-url with a logout parameter. But this is
 * not applicable for the REST scenario.
 *
 * For REST, we simply return a 200 so that the client knows that the logout was successful.
 *
 * @author Ganeshji Marwaha
 * @since 8/24/14
 */
@Slf4j
public class RestLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.debug("<<<<<" + "RestLogoutSuccessHandler.onLogoutSuccess" + ">>>>>");
        // Do not do anything. This should technically return a 200
    }
}
