package io.fxtahe.rpc.common.bootstrap;

import io.fxtahe.rpc.common.invoke.Invoker;

/**
 * @author fxtahe
 * @since 2022/9/13 14:43
 */
public interface BootStrap {

    /**
     * export service
     * @return invoker
     */
    void export(Invoker invoker);

    /**
     * unExport service
     */
    void unExport(Invoker invoker);

    /**
     * refer service
     * @return invoker
     */
    Invoker refer();

    /**
     * unRefer service
     */
    void unRefer();

}
