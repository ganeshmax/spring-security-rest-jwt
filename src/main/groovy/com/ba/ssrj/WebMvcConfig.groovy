package com.ba.ssrj

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * NOTE: Do not use @EnableWebMvc unless you plan to configure all the web-mvc related stuff yourself. Presence of this 
 * annotation tells spring that it should not auto-configure any web-mvc related stuff
 *
 * @author Ganeshji Marwaha
 * @since 6/20/15
 */
@Configuration
class WebMvcConfig extends WebMvcConfigurerAdapter {
}
