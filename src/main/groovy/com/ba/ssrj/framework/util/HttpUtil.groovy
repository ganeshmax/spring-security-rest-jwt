package com.ba.ssrj.framework.util

import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletResponse

/**
 * TODO: Document Me
 *
 * @author Ganeshji Marwaha
 * @since 6/20/15
 */
public class HttpUtil {
    public static void sendJsonResponse(HttpServletResponse response, HttpStatus status, String content) {
        response.setStatus(status.value())
        response.contentType = 'application/json'

        PrintWriter writer = response.getWriter();
        writer.println(content);
    }
}
