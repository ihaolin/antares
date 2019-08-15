package me.hao0.antares.common.http;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Strings;
import me.hao0.antares.common.exception.HttpException;

import java.io.*;
import java.util.*;

/**
 * The Http client
 * Author: haolin
 * Email: haolin.h0@gmail.com
 */
public class Http {

    private String url;

    private HttpMethod method = HttpMethod.GET;

    private Map<String, String> headers = Collections.emptyMap();

    private Map<String, ?> params = Collections.emptyMap();

    /**
     * The request body
     */
    private String body;

    private Boolean ssl = Boolean.FALSE;

    private Integer connectTimeout = 1000 * 5;

    private Integer readTimeout = 1000 * 5;

    /**
     * Encode or not
     */
    private Boolean encode = Boolean.TRUE;

    /**
     * The content type
     */
    private String contentType = "";

    /**
     * The charset
     */
    private String charset = HttpRequest.CHARSET_UTF8;

    /**
     * The accept type
     */
    private String accept = "";

    private Http(String url) {
        this.url = url;
    }

    private Http method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public Http ssl() {
        this.ssl = Boolean.TRUE;
        return this;
    }

    public Http headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Http params(Map<String, ?> params) {
        this.params = params;
        return this;
    }

    /**
     * The request body
     *
     * @param body request body
     * @return this
     */
    public Http body(String body) {
        this.body = body;
        return this;
    }

    public Http encode(Boolean encode) {
        this.encode = encode;
        return this;
    }

    public Http contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Http charset(String charset) {
        this.charset = charset;
        return this;
    }

    public Http accept(String accept) {
        this.accept = accept;
        return this;
    }

    /**
     * set connect timeout
     *
     * @param connectTimeout (s)
     * @return this
     */
    public Http connTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout * 1000;
        return this;
    }

    /**
     * set read timeout
     *
     * @param readTimeout (s)
     * @return this
     */
    public Http readTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout * 1000;
        return this;
    }

    public String request() {
        switch (method) {
            case GET:
                return doGet();
            case POST:
                return doPost();
            default:
                break;
        }
        return null;
    }


    private String doPost() {
        HttpRequest post = HttpRequest.post(url, encode)
                .headers(headers)
                .connectTimeout(connectTimeout)
                .readTimeout(readTimeout)
                .acceptGzipEncoding()
                .uncompress(true);
        setOptionalHeaders(post);

        if (body != null) {
            post.send(body);
        }

        if (ssl) {
            trustHttps(post);
        }

        return post.body();
    }

    private String doGet() {
        HttpRequest get = HttpRequest.get(url, params, encode)
                .headers(headers)
                .connectTimeout(connectTimeout)
                .readTimeout(readTimeout)
                .acceptGzipEncoding()
                .uncompress(true);
        if (ssl) {
            trustHttps(get);
        }
        setOptionalHeaders(get);
        return get.body();
    }

    public static String mapToQueryString(Map<String, ?> params) {
        final StringBuilder result = new StringBuilder();
        Iterator<? extends Map.Entry<String, ?>> iterator = params.entrySet().iterator();

        Map.Entry<String, ?> entry = iterator.next();
        addParam(entry.getKey(), entry.getValue(), result);

        while (iterator.hasNext()) {
            result.append('&');
            entry = iterator.next();
            addParam(entry.getKey(), entry.getValue(), result);
        }
        return result.toString();
    }


    private static void addParam(final Object key, Object value, final StringBuilder result) {
        if (value != null && value.getClass().isArray())
            value = arrayToList(value);

        if (value instanceof Iterable<?>) {
            Iterator<?> iterator = ((Iterable<?>) value).iterator();
            while (iterator.hasNext()) {
                result.append(key);
                result.append("[]=");
                Object element = iterator.next();
                if (element != null)
                    result.append(element);
                if (iterator.hasNext())
                    result.append("&");
            }
        } else {
            result.append(key);
            result.append("=");
            if (value != null)
                result.append(value);
        }
    }

    /**
     * Represents array of any type as list of objects so we can easily iterate over it
     *
     * @param array of elements
     * @return list with the same elements
     */
    private static List<Object> arrayToList(final Object array) {
        if (array instanceof Object[])
            return Arrays.asList((Object[]) array);

        List<Object> result = new ArrayList<Object>();
        // Arrays of the primitive types can't be cast to array of Object, so this:
        if (array instanceof int[])
            for (int value : (int[]) array) result.add(value);
        else if (array instanceof boolean[])
            for (boolean value : (boolean[]) array) result.add(value);
        else if (array instanceof long[])
            for (long value : (long[]) array) result.add(value);
        else if (array instanceof float[])
            for (float value : (float[]) array) result.add(value);
        else if (array instanceof double[])
            for (double value : (double[]) array) result.add(value);
        else if (array instanceof short[])
            for (short value : (short[]) array) result.add(value);
        else if (array instanceof byte[])
            for (byte value : (byte[]) array) result.add(value);
        else if (array instanceof char[])
            for (char value : (char[]) array) result.add(value);
        return result;
    }


    private void setOptionalHeaders(HttpRequest request) {
        if (!Strings.isNullOrEmpty(contentType)) {
            request.contentType(contentType, charset);
        }
        if (!Strings.isNullOrEmpty(accept)) {
            request.accept(accept);
        }
    }

    private void trustHttps(HttpRequest request) {
        request.trustAllCerts().trustAllHosts();
    }

    public static Http get(String url) {
        return new Http(url);
    }

    public static Http post(String url) {
        return new Http(url).method(HttpMethod.POST);
    }

    public static Http put(String url) {
        return new Http(url).method(HttpMethod.PUT);
    }

    public static Http delete(String url) {
        return new Http(url).method(HttpMethod.DELETE);
    }

    /**
     * upload file
     *
     * @param url       url
     * @param fieldName field name
     * @param fileName  file name
     * @param data      file byte data
     * @param params    other params
     * @return string response
     */
    public static String upload(String url, String fieldName, String fileName, byte[] data, Map<String, String> params) {
        return upload(url, fieldName, fileName, new ByteArrayInputStream(data), params);
    }

    /**
     * upload file
     *
     * @param url       url
     * @param fieldName field name
     * @param fileName  file name
     * @param in        InputStream
     * @param params    other params
     * @return string response
     */
    public static String upload(String url, String fieldName, String fileName, InputStream in, Map<String, String> params) {
        try {
            HttpRequest request = HttpRequest.post(url);
            if (!params.isEmpty()) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    request.part(param.getKey(), param.getValue());
                }
            }
            request.part(fieldName, fileName, null, in);
            return request.body();
        } catch (Exception e) {
            throw new HttpException(e);
        }
    }

    /**
     * download a file
     *
     * @param url  http url
     * @param into the file which downloaded content will fill into
     */
    public static void download(String url, File into) {
        try {
            download(url, new FileOutputStream(into));
        } catch (FileNotFoundException e) {
            throw new HttpException(e);
        }
    }

    /**
     * download a file
     *
     * @param url    http url
     * @param output the output which downloaded content will fill into
     */
    public static void download(String url, OutputStream output) {
        try {
            HttpRequest request = HttpRequest.get(url);
            if (request.ok()) {
                request.receive(output);
            } else {
                throw new HttpException("request isn't ok: " + request.body());
            }
        } catch (Exception e) {
            throw new HttpException(e);
        }
    }
}
