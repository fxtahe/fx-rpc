package io.fxtahe.rpc.test;


/**
 * @author fxtahe
 * @since 2022-09-28 21:21
 */
public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(long id) {
        int i=10/0;
        return "hello "+id;
    }


}
