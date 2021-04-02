package com.firerms.multiTenancy.auditLogs;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CachedServletInputStream extends ServletInputStream {

    private ByteArrayInputStream input;

    public CachedServletInputStream(ByteArrayOutputStream cachedBytes) {
        input = new ByteArrayInputStream(cachedBytes.toByteArray());
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() {
        return input.read();
    }
}
