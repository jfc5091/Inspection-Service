package com.firerms.multiTenancy.auditLogs;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {

    private ByteArrayOutputStream cachedBytes;

    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
        public ServletInputStream getInputStream() throws IOException {
            if (cachedBytes == null)
                cacheInputStream();

            return new CachedServletInputStream(cachedBytes);
        }

        @Override
        public BufferedReader getReader() throws IOException{
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        private void cacheInputStream() throws IOException {
            cachedBytes = new ByteArrayOutputStream();
            IOUtils.copy(super.getInputStream(), cachedBytes);
        }

    public String getBody() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = this.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    if (!line.equals("")) {
                        stringBuilder.append(line);
                    }
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        String body = stringBuilder.toString();
        if (body.isEmpty()) {
            body = null;
        }
        return body;
    }
}
