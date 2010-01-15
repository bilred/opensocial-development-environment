package com.googlecode.osde.internal.igoogle;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import com.googlecode.osde.internal.utils.Logger;

/**
 * Utility class providing HTTP-related functions for iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgHttpUtil {
    private static Logger logger = new Logger(IgHttpUtil.class);

    static final String HTTP_HEADER_COOKIE = "Cookie";
    static final String HTTP_HEADER_SET_COOKIE = "Set-Cookie";

    static final String URL_HTTP_GOOGLE = "http://www.google.com/";
    static final String URL_HTTPS_GOOGLE = "https://www.google.com/";
    static final String URL_HTTP_IG = URL_HTTP_GOOGLE + "ig";
    static final String URL_IG_GADGETS = URL_HTTP_IG + "/gadgets";

    /**
     * Retrieves HTTP response as String.
     *
     * @throws IgException
     */
    static String retrieveHttpResponseAsString(
            HttpClient httpClient, HttpRequestBase httpRequest, HttpResponse httpResponse)
            throws IgException {
        HttpEntity entity = httpResponse.getEntity();
        String response = null;
        if (entity != null) {
            InputStream inputStream = null;
            try {
                inputStream = entity.getContent();
                // TODO: is there any constant for "UTF-8"?
                response = IOUtils.toString(inputStream, "UTF-8");
            } catch (IOException e) {
                // The HttpClient's connection will be automatically released
                // back to the connection manager.
                logger.error("Error:\n" + e.getMessage());
                throw new IgException(e);
            } catch (RuntimeException e) { // To catch unchecked exception intentionally.
                // Abort HttpRequest in order to release HttpClient's connection
                // back to the connection manager.
                logger.error("Error:\n" + e.getMessage());
                httpRequest.abort();
                throw new IgException(e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            httpClient.getConnectionManager().shutdown();
        }
        logger.fine("response: '" + response + "'");
        return response;
    }
}
