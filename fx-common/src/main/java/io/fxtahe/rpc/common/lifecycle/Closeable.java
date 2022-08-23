package io.fxtahe.rpc.common.lifecycle;

import java.io.IOException;

/**
 * @author fxtahe
 * @since 2022-08-23 22:07
 */
public interface Closeable {

    /**
     * shutdown resource
     */
    void shutdown() throws Exception;
}
