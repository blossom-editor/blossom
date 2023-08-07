package com.blossom.backend.base.auth.filters;

import cn.hutool.core.io.IoUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * request 封装类,用于实现多次读取 requet body
 *
 * @author xzzz
 * @since 0.0.1
 */
public class RequestBodyReaderWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    public RequestBodyReaderWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = IoUtil.readBytes(request.getInputStream(), false);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

        };
    }

    public void setInputStream(byte[] body) {
        this.body = body;
    }
}
