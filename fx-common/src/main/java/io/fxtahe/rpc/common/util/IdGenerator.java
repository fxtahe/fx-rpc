package io.fxtahe.rpc.common.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fxtahe
 * @since 2022/8/18 16:38
 */
public class IdGenerator {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    public static long generateId(){
        return ATOMIC_INTEGER.incrementAndGet();
    }
}
