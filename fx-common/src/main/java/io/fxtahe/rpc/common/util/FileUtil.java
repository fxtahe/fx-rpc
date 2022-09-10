package io.fxtahe.rpc.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @author fxtahe
 * @since 2022/8/25 9:37
 */
public class FileUtil {

    private static final String READ_ONLY = "r";

    private static final String READ_WRITE = "rw";

    private static final int SLEEP_TIME = 10;

    private static final int RETRY_COUNT = 5;


    public static boolean writeConcurrentFileContent(String content, String path, String charset) throws IOException {
        File file = new File(path);
        if(!file.exists() && file.getParentFile() != null && !file.getParentFile().exists() && !file.getParentFile().mkdirs() && !file.createNewFile()) {
            return false;
        }
        FileChannel channel = null;
        FileLock lock = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, READ_WRITE);
            channel = raf.getChannel();
            int i = 0;
            do {
                try {
                    lock = channel.lock();
                } catch (Exception e) {
                    ++i;
                    if (i < RETRY_COUNT) {
                        throw new IOException("write " + file.getAbsolutePath() + "conflict.", e);
                    }
                    try {
                        Thread.sleep(SLEEP_TIME);
                    }catch (InterruptedException exception){
                        Thread.currentThread().interrupt();
                    }

                }
            } while (lock == null);

            byte[] contentBytes = content.getBytes(charset);
            ByteBuffer sendBuffer = ByteBuffer.wrap(contentBytes);
            while (sendBuffer.hasRemaining()) {
                channel.write(sendBuffer);
            }
            channel.truncate(contentBytes.length);

        } catch (
                FileNotFoundException e) {
            throw new IOException("file not exist");
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                    lock = null;
                } catch (IOException e) {
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                    channel = null;
                } catch (IOException e) {
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                    raf = null;
                } catch (IOException e) {
                }
            }

        }
        return true;


    }

}
