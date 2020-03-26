package org.byron4j.concurrent.magic;

import java.io.FileDescriptor;

/**
 * 扩展SecurityManager限制一些访问操作
 */
public class MySecurityManager extends SecurityManager {

    @Override
    public void checkRead(FileDescriptor fd) {
        throw new SecurityException("File reading is not allowed");
    }

    @Override
    public void checkWrite(FileDescriptor fd) {
        throw new SecurityException("File writing is not allowed");
    }

    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("Socket connections are not allowed");
    }
}
