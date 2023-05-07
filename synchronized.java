/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */



/**
 *
 * @author ahmad
 */
import java.util.concurrent.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.io.*;
import java.nio.channels.*;
import java.util.logging.*;
import static java.lang.Runtime.*;
import java.nio.channels.FileChannel.MapMode;
import java.util.EnumSet;

public class Main {
    static int N = 152 + 500; // my id is 11820152
    static int pid;
    static String name = "shared_mem";
    static int SIZE = N * Integer.SIZE / 8;

    public static void main(String[] args) {
        try {
            FileChannel channel = FileChannel.open(Paths.get("shared_mem.bin"),
                    StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
            MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, SIZE);
            buffer.putInt(0);
            buffer.force();
            Thread[] thread = new Thread[N];
            for (int i = 0; i < N; i++) {
                Runnable runnable = new UpdateShared(buffer);
                thread[i] = new Thread(runnable);
                thread[i].start();
            }
            for (int i = 0; i < N; i++) {
                thread[i].join();
            }
            int expected = N * N ;
            System.out.printf("Expected value is %d, Thread value %d\n", expected, buffer.getInt(0));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class UpdateShared implements Runnable {
        static final Logger LOGGER = Logger.getLogger(UpdateShared.class.getName());
        static int Tid;
        static String name = "shared_mem";
        static int N = 152 + 500;
        static int SIZE = N * Integer.SIZE / 8;
        static MappedByteBuffer buffer;

        static {
            try {
                Handler handler = new FileHandler("logs2.log"); // log file name
                LOGGER.addHandler(handler);
                LOGGER.setUseParentHandlers(false);
                LOGGER.setLevel(Level.ALL);
                handler.setLevel(Level.ALL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public UpdateShared(MappedByteBuffer buffer) {
            this.buffer = buffer;
        }

        public void run() {
    Tid = (int) Thread.currentThread().getId();
    synchronized (buffer) {
        
        System.out.printf("I am Thread %d; about to go to sleep for %d nanoseconds \n", Tid, Tid % 10);
        try {
            Thread.sleep(Tid % 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int value ;
        for (int i = 0; i < N; i++) {
            value = buffer.getInt(0);
            LOGGER.info(String.format("I am Thread %d, about to increment the counter, old value was %d\n", Tid, buffer.getInt(0)));
            buffer.putInt(0, value + 1);
            buffer.force();
            LOGGER.info(String.format("I am Thread %d, finished incrementing the counter, new value is %d\n", Tid, buffer.getInt(0)));
        }
    }
}

    }
}


