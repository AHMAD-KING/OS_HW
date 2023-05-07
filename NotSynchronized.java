/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ahmad
 */
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotSynchronized {

    static int sharedMemory = 0;
    static int N = 652;

    public static void main(String[] args) {
        try {


            Thread[] thread = new Thread[N];
            for (int i = 0; i < N; i++) {

                thread[i] = new Thread(new Runnable() {
                    static final Logger LOGGER = Logger.getLogger(NotSynchronized.class.getName());

                    static {
                        try {
                            Handler handler = new FileHandler("logsUnsynchronized.log"); // log file name
                            LOGGER.addHandler(handler);
                            LOGGER.setUseParentHandlers(false);
                            LOGGER.setLevel(Level.ALL);
                            handler.setLevel(Level.ALL);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void run() {
                        
                        int Tid = (int) Thread.currentThread().getId();
                        System.out.printf("I am Thread %d; about to go to sleep for %d nanoseconds \n", Tid, Tid % 10);
                        try {
                            Thread.sleep(Tid % 10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < N; i++) {
                            int value = sharedMemory;
                            LOGGER.info(String.format("I am Thread %d, about to increment the counter, old value was %d\n", Tid, value));
                            sharedMemory = value + 1;
                            LOGGER.info(String.format("I am Thread %d, finished incrementing the counter, new value is %d\n", Tid, sharedMemory));
                        }
                    }
                });
                thread[i].start();
            }
            for (int i = 0; i < N; i++) {
                thread[i].join();
            }
            int expected = N * N ;
//            LOGGER.info(String.format("Expected value is %d, Thread value %d\n", expected, buffer.getInt(0)));
            System.out.printf("Expected value is %d, Thread value %d\n", expected, sharedMemory);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
