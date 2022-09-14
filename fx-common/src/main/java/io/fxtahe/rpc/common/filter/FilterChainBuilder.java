package io.fxtahe.rpc.common.filter;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.ext.ExtensionLoaderFactory;
import io.fxtahe.rpc.common.invoke.Invoker;

import java.util.List;

/**
 * @author fxtahe
 * @since 2022/8/31 16:14
 */
public class FilterChainBuilder {


    public static Invoker buildFilterChain(final Invoker originalInvoker, String group){

        List<Filter> filters = ExtensionLoaderFactory.getExtensionLoader(Filter.class).getExtensionsGroup(group);
        Invoker last = originalInvoker;
        if(filters!=null && filters.size()>0){
            for(int i=0;i<filters.size();i++){
                last = new FilterChainNode(last, filters.get(i));
            }
        }
        return last;
    }


    static class FilterChainNode implements Invoker {
        private Invoker nextNode;
        private Filter filter;


        public FilterChainNode(Invoker nextNode, Filter filter) {
            this.nextNode = nextNode;
            this.filter = filter;
        }

        @Override
        public Result invoke(Invocation invocation) {
            Result result = this.filter.filter(nextNode, invocation);
            result.whenComplete((r,t)->{
                if(t==null){
                    filter.onResponse(r,invocation);
                }else{
                    filter.onError(t,invocation);
                }
            });
            return result;
        }

        @Override
        public Class<?> getInterface() {
            return nextNode.getInterface();
        }
    }




}
