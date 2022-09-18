package io.fxtahe.rpc.common.invoke;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.remoting.Client;

public class ConsumerProxyInvoker implements Invoker{


    private Client client;

    private Class<?> interfaceClass;

    @Override
    public Result invoke(Invocation invocation) {

        if(!isOneWay(invocation)){

            

        }else{
            //TODO
        }





        return null;
    }

    @Override
    public Class<?> getInterface() {
        return interfaceClass;
    }


    public boolean isOneWay(Invocation invocation){
        return false;
    }

}
