package com.chessmates

import com.chessmates.model.LichessModel
import com.chessmates.model.LichessModelSerializer
import com.chessmates.utility.DisableSSL
import com.google.common.cache.CacheBuilder
import jdk.nashorn.internal.runtime.regexp.joni.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.guava.GuavaCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

import java.util.concurrent.TimeUnit

/**
 * This is the starting point of the application
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling // We includ the spring-boot-starter-actuator which also enables scheduling.
class Application {

    static final String REQUEST_CACHE_NAME = 'requestCache'
    static final int CACHE_EXPIRY_MINS = 30

    /**
     * Main entry method for the application
     * @param args
     */
    static void main(String[] args) {
        DisableSSL.disableCertificatesValidation()
        SpringApplication.run(Application, args)
    }

    /**
     * Configuration for Spring MVC.
     */
    @Bean
    WebMvcConfigurer createWebConfigurer() {
        new WebMvcConfigurerAdapter() {

            /**
             * Enable CORS for all origins.
             */
            @Override
            void addCorsMappings(CorsRegistry registry) {
                registry.addMapping('/**')
            }
        }
    }

    /**
     * Configure cache.
     */
    @Bean
    CacheManager createCacheManager() {
        // Create the cache itself
        def internalCache = CacheBuilder.newBuilder()
                .expireAfterWrite(CACHE_EXPIRY_MINS, TimeUnit.MINUTES)
                .build()

        // Feed to Spring's cache abstractions
        def springCache = new GuavaCache(REQUEST_CACHE_NAME, internalCache)

        def simpleCacheManager = new SimpleCacheManager()
        simpleCacheManager.setCaches(Arrays.asList(springCache))
        return simpleCacheManager
    }

    @Bean
    Jackson2ObjectMapperBuilderCustomizer addCustomSerializers() {
        new Jackson2ObjectMapperBuilderCustomizer() {

            @Override
            void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
                jacksonObjectMapperBuilder.serializerByType(LichessModel, new LichessModelSerializer())
            }
        }
    }

}
