package com.ba.ssrj

import groovy.time.TimeCategory
import org.springframework.boot.SpringApplication

/**
 * Main
 *
 * @author Ganeshji Marwaha
 * @since 6/20/15
 */
class Application {
    static void main(String[] args) {
        mixin()
        SpringApplication.run ApplicationConfig, args
    }
    
    private static void mixin() {
        Integer.metaClass.mixin TimeCategory
        Date.metaClass.mixin TimeCategory
    }
}
