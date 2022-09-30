package io.fxtahe.rpc.test;


/**
 * @author fxtahe
 * @since 2022-09-28 21:21
 */
public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(long id) {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {

        }
        return "hello "+id;
    }


}
