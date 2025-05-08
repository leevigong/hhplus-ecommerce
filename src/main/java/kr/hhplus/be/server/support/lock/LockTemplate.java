package kr.hhplus.be.server.support.lock;

import java.util.concurrent.TimeUnit;

public interface LockTemplate {

    LockStrategy getLockStrategy();

    <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) throws Throwable;

}
