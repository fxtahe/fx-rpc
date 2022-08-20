package io.fxtahe.rpc.common.filter;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fxtahe
 * @since 2022/8/19 10:45
 */
@Extension(alias = "log",order = 0,singleton = true)
public class LogFilter implements Filter{

    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void filter(Invocation invocation) {




    }
}
