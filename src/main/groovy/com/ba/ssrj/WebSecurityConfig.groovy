package com.ba.ssrj

import com.ba.ssrj.security.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

/**
 * Found by the app because of ComponentScan on ApplicationConfig.
 *
 * Configure spring security for REST using JWT.
 * - Configure web security
 * - Stateless session
 * - JWT token (stateless, no token storage)
 * - Non invasive integration with spring security hooks
 *  
 * NOTE:
 * @EnableWebSecurity allows configuration via a @Configuration class that implements WebSecurityConfigurer
 * @EnableWebMvcSecurity adds integration to MVC like @AuthenticationPrincipal etc in MVC controller methods.
 * From Spring 4.x, @EnableWebMvcSecurity is deprecated. Usage of @EnableWebSecurity automatically adds MVC
 * integration based on conditional configuration using classpath
 *
 * @author Ganeshji Marwaha
 * @since 6/20/15
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Configure a global authentication manager which will be used by web and rest modules.
     * The reason is that, this system's users are always the same with the same credentials regardless of whether
     * a web client or rest client accesses it. So, the same authentication manager can be used
     * across all the modules as long as it is for user authentication
     *
     * @param auth
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        // You could potentially use auth.userDetailsService(userService) to setup your own UserDetailsService
        // insted of in-memory like here. The in-memory version is just used for testing purposes and ease.
        auth.inMemoryAuthentication()
            .withUser("user").password("password").roles("USER")
            .and()
            .withUser("admin").password("password").roles("USER", "ADMIN")
    }

    @Configuration
    @Order(1)
    public static class JwtBasedRestSecurityConfig extends WebSecurityConfigurerAdapter {

        /**
         * TODO:
         * configure loginPage(/rest/login-page) and return a JSON asking the user to POST to /rest/login for logging in.
         * This is because, loginPage() makes sense only in form-login scenario. But, since we are reusing form-login
         * configuration here, we should make sure that the default behaviour of generating a login page when /login is
         * called should be taken care of
         *  
         * @param http
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // Applies to /rest/** URLs only
            http.antMatcher("/rest/**")
                .authorizeRequests()
                    .antMatchers("/rest/login", "/rest/logout").permitAll()
                    .anyRequest().authenticated()
            
            // Configure REST friendly entry-point and access-denied handler
            http.exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint())
                .accessDeniedHandler(restAccessDeniedHandler())
            
            // Configure REST friendly authenticate success and failure handler
            http.formLogin()
                .loginProcessingUrl("/rest/login")
                .successHandler(restAuthenticationSuccessHandler())
                .failureHandler(restAuthenticationFailureHandler())
            
            // Configure REST friendly logout success handler
            http.logout()
                .logoutUrl("/rest/logout")
                .logoutSuccessHandler(restLogoutSuccessHandler())
            
            // Configure Security Context Repository so that we can create a SC during subsequent requests, from JWT
            http.securityContext()
                .securityContextRepository(restSecurityContextRepository())
            
            // Stateless REST configuration
            http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            // Java config enables CSRF protection by default. Disable it here
            http.csrf().disable()
        }
        

        @Bean
        protected RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
            return new RestAuthenticationEntryPoint();
        }

        @Bean
        protected RestAuthenticationSuccessHandler restAuthenticationSuccessHandler() {
            return new RestAuthenticationSuccessHandler();
        }

        @Bean
        protected RestAuthenticationFailureHandler restAuthenticationFailureHandler() {
            return new RestAuthenticationFailureHandler();
        }

        @Bean
        protected RestLogoutSuccessHandler restLogoutSuccessHandler() {
            return new RestLogoutSuccessHandler();
        }

        @Bean
        protected RestAccessDeniedHandler restAccessDeniedHandler() {
            return new RestAccessDeniedHandler();
        }
        
        @Bean
        protected RestSecurityContextRepository restSecurityContextRepository() {
            return new RestSecurityContextRepository()
            
        }
    }
    
    @Configuration
    @Order(2)
    public static class CookieBasedWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // For anything other than /rest/** consider the resource protected.
            http
                    .antMatcher("/**")
                        .authorizeRequests()
                            .anyRequest().authenticated()
                    .and()
                        .formLogin()
                    .and()
                        .csrf().disable()
        }
    }
}
