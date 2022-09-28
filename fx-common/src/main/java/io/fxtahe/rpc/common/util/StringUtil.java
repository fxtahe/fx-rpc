package io.fxtahe.rpc.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author fxtahe
 * @since 2022-09-28 22:02
 */
public class StringUtil {

    private StringUtil(){}

    public static String toString(Throwable t){
        StringWriter stringWriter = new StringWriter();
        t.printStackTrace(new PrintWriter(stringWriter,true));
        return stringWriter.getBuffer().toString();

    }


}
