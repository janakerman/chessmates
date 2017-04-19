package com.chessmates.utility

import com.chessmates.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

import javax.net.ssl.HttpsURLConnection
import javax.xml.ws.http.HTTPException
import java.time.LocalDateTime
import static java.time.temporal.ChronoUnit.*


/**
 * Http utility that throttles requests.
 */
@Component
class ThrottledHttpUtility implements HttpUtility {

    // TODO: Shouldn't be set here?
    private static final Integer THROTTLE_MILLIS = 5000
    private static final Integer COOLDOWN_TIME_MILLIS = 60000
    private LocalDateTime lastRequest

    private static final Logger logger = LoggerFactory.getLogger(ThrottledHttpUtility)

    /**
     * Makes a get request for given resource.
     */
    @Override
    @Cacheable('requestCache')
    String get(String targetUrl) throws HTTPException {
        def now = LocalDateTime.now()

        if (lastRequest) {
            def millisSince = MILLIS.between(lastRequest, now)

            if (millisSince < THROTTLE_MILLIS) {
                logger.debug "Throttled request: waiting ${THROTTLE_MILLIS - millisSince} millis (${targetUrl})"
                // TODO: I know I know...
                Thread.sleep(THROTTLE_MILLIS - millisSince)
            }
        }

        getRequest targetUrl
    }
    
    /**
     * Makes a get request for given resource.
     */
    private String getRequest(String targetUrl) throws HTTPException {
        def connection
        try {
            return ((HttpsURLConnection) targetUrl.toURL().openConnection()).with { conn ->
                requestMethod = 'GET'
                doOutput = true

                /* If we receive a 429 sleep the thread for 1 minute - this isn't a real solution just a quick semi-fix
                trying to avoid getting an IP ban during development. */
                if (responseCode == HttpStatus.TOO_MANY_REQUESTS.value()) {
                    throw new HTTPException(HttpStatus.TOO_MANY_REQUESTS.value())
                }

                // We're making http requests so we're assuming that we'll be getting back a HttpInputStream.
                return ((InputStream) conn.getContent()).withReader { r -> r.text }
            }
        } catch (HTTPException e) {
            logger.warn "Server returned HTTP response code: ${e.statusCode} for URL: ${targetUrl}"
            throw e
        } finally {
            if(connection != null) {
                connection.disconnect()
            }
            lastRequest = LocalDateTime.now()
        }
    }

}
