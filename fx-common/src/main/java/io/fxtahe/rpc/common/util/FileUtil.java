package io.fxtahe.rpc.common.util;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (!file.exists() && file.getParentFile() != null && !file.getParentFile().exists() && !file.getParentFile().mkdirs() && !file.createNewFile()) {
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
                    } catch (InterruptedException exception) {
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

    public static Map<String, String> readFilesContent(String path, String fileType, String charset) throws IOException {
        Map<String, String> contents = new HashMap<>();
        File direct = new File(path);
        if (!direct.exists()) {
            return contents;
        }
        File[] files = direct.listFiles((f) -> f.getName().endsWith("." + fileType));
        if (files == null || files.length == 0) {
            return contents;
        }
        FileChannel channel = null;
        RandomAccessFile raf = null;
        FileLock lock = null;
        CharsetDecoder charsetDecoder = Charset.forName(charset).newDecoder();
        for (File file : files) {
            try {
                raf = new RandomAccessFile(file, READ_ONLY);

                channel = raf.getChannel();
                int i = 0;
                do {
                    try {
                        lock = channel.tryLock(0L, Long.MAX_VALUE, true);
                    } catch (Exception e) {
                        ++i;
                        if (i < RETRY_COUNT) {
                            throw new IOException("read " + file.getAbsolutePath() + "conflict.", e);
                        }
                        try {
                            Thread.sleep(SLEEP_TIME);
                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                        }

                    }
                } while (lock == null);

                ByteBuffer allocate = ByteBuffer.allocate((int) channel.size());
                channel.read(allocate);
                allocate.flip();

                CharBuffer decode = charsetDecoder.decode(allocate);
                String content = decode.toString();
                String name = file.getName().substring(0,file.getName().lastIndexOf("."));
                contents.put(name,content);
            } catch (FileNotFoundException e) {
                throw new IOException("file not exist");
            } finally {
                if (lock != null) {
                    try {
                        lock.release();
                        lock = null;
                    } catch (IOException ignored) {
                    }
                }
                if (channel != null) {
                    try {
                        channel.close();
                        channel = null;
                    } catch (IOException ignored) {
                    }
                }
                if (raf != null) {
                    try {
                        raf.close();
                        raf = null;
                    } catch (IOException ignored) {
                    }
                }

            }
        }
        return contents;
    }

}
