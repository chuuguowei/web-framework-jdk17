package org.example.filter;

import io.undertow.servlet.spec.HttpServletRequestImpl;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 日志拦截打印
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebFrameworkHttpLogFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(WebFrameworkHttpLogFilter.class);

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final List<String> IGNORE_URI_LIST = Arrays.asList("/actuator/health", "/actuator/prometheus");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            String method = ((HttpServletRequestImpl) request).getMethod();
            String url = ((HttpServletRequestImpl) request).getRequestURI();
            if (("GET".equals(method) || JSON_CONTENT_TYPE.equals(request.getContentType())) && !needIgnore(url)) {
                // 可重复读的request
                LoggingHttpServletRequestWrapper requestWrapper =
                        new LoggingHttpServletRequestWrapper((HttpServletRequest) request);

                // 可重复写的response
                ContentCachingResponseWrapper responseWrapper =
                        new ContentCachingResponseWrapper((HttpServletResponse) response);

                doWrapperFilter(requestWrapper, responseWrapper, chain);
            } else {
                chain.doFilter(request, response);
            }
        } catch (Exception e) {
            chain.doFilter(request, response);
        }
    }

    public void doWrapperFilter(LoggingHttpServletRequestWrapper requestWrapper,
                                ContentCachingResponseWrapper responseWrapper, FilterChain chain) {
        // 埋时间点
        long startTime = System.currentTimeMillis();
        try {
            doBefore(requestWrapper);
            chain.doFilter(requestWrapper, responseWrapper);
            try {
                doAfter(requestWrapper, responseWrapper, startTime);
            } finally {
                responseWrapper.copyBodyToResponse();
            }
        } catch (Exception e) {
            logger.warn("running exception, ", e);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("WebFrameworkHttpLogFilter init.");
    }

    @Override
    public void destroy() {
        logger.info("WebFrameworkHttpLogFilter destroy.");
    }

    private void doBefore(LoggingHttpServletRequestWrapper request) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("request").append(formatUri(request.getRequestURI())).append("\n");
            sb.append("> ").append(request.getMethod()).append(" ").append(request.getRequestURI()).append("\n");
            Enumeration names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                sb.append("> ");
                String name = names.nextElement().toString();
                sb.append(name).append(": ");
                Enumeration headers = request.getHeaders(name);
                while (headers.hasMoreElements()) {
                    sb.append(headers.nextElement());
                }
                sb.append("\n");
            }
            // 请求日志里加入traceId
            sb.append("> ");
            sb.append("X-B3-TraceId: ");
            sb.append(MDC.get("X-B3-TraceId"));
            sb.append("\n");

            String body = IOUtils.toString(request.getBody(), request.getCharacterEncoding());

            if (request.getContentType() == null && request.getMethod().equals("GET")) {
                Map<String, String[]> params = request.getParameterMap();
                StringBuilder paramStr = new StringBuilder();
                for (Map.Entry<String, String[]> entry : params.entrySet()) {
                    String[] val = entry.getValue();
                    paramStr.append(entry.getKey()).append("=").append(val[0]).append("&");
                }
                if (paramStr.length() > 0) {
                    body = paramStr.substring(0, paramStr.length() - 1); // 去掉最后一个&
                }
            }

            if (StringUtils.isBlank(body)) {
                sb.append("no request body");
            } else {
                sb.append(body);
            }
            logger.info(sb.toString());
        } catch (Exception e) {
            logger.error("doBefore failed: ", e);
        }
    }

    private void doAfter(LoggingHttpServletRequestWrapper request, ContentCachingResponseWrapper response,
                         long startTime) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("response").append(formatUri(request.getRequestURI())).append("\n");
            sb.append("< status: ").append(response.getStatus()).append("\n");
            Collection<String> names = response.getHeaderNames();
            for (String name : names) {
                sb.append("< ");
                sb.append(name).append(": ");
                String value;
                if ("X-B3-TraceId".equals(name)) {
                    value = MDC.get("X-B3-TraceId");
                } else {
                    value = response.getHeader(name);
                }
                sb.append(value);
                sb.append("\n");
            }
            sb.append("< cost: ").append(getCost(startTime)).append("ms\n");
            String res = getResponseBody(response);
            if (StringUtils.isBlank(res)) {
                sb.append("no response result");
            } else {
                sb.append(res);
            }

            logger.info(sb.toString());
        } catch (Exception e) {
            logger.error("doAfter failed: ", e);
        }
    }

    /**
     * 获取耗时
     * @param startTime
     * @return
     */
    private long getCost(long startTime) {
        long now = System.currentTimeMillis();
        return now - startTime;
    }

    /**
     * 获取返回值
     * @param response
     */
    private String getResponseBody(ContentCachingResponseWrapper response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(
                response,
                ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String payload;
                payload = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
                return payload;
            }
        }
        return "";
    }

    private boolean needIgnore(String url) {
        return IGNORE_URI_LIST.contains(url);
    }

    /**
     * 将uri格式化为 - 分隔, 用于检索
     *
     * @param uri 请求路径
     * @return 格式化路径
     */
    private String formatUri(String uri) {
        if (Objects.isNull(uri)) {
            return null;
        }
        return uri.replaceAll("/", "-");
    }

}

