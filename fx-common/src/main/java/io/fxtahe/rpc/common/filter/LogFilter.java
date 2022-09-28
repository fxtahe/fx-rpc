package io.fxtahe.rpc.common.filter;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.invoke.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fxtahe
 * @since 2022/8/19 10:45
 */
@Extension(alias = "log",order = 0,singleton = true,group = "consumer")
public class LogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public Result filter(Invoker invoker, Invocation invocation) {
        log.info("log before invoke:"+invoker.getInterfaceName());
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result result, Invocation invocation) {
        log.info("log when completableFuture on response:{}",result.getValue());
    }

    @Override
    public void onError(Throwable throwable, Invocation invocation) {
        log.error("log when completableFuture on error:",throwable);
    }
}
