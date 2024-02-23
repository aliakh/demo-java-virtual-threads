package virtual_threads.part1;

import org.junit.jupiter.api.Test;
import virtual_threads.AbstractTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Example3CreateStartedAndUnstartedVirtualThreadsTest extends AbstractTest {

    @Test
    public void createStartedThreadTest() throws InterruptedException {
        Thread.Builder builder = Thread.ofVirtual();

        Thread thread = builder.start(() -> { sleep(1000); System.out.println("run"); });
        assertEquals(Thread.State.RUNNABLE, thread.getState());

        thread.join();
    }

    @Test
    public void createUnstartedThreadTest() throws InterruptedException {
        Thread.Builder builder = Thread.ofVirtual();
        Thread thread = builder.unstarted(() -> System.out.println("run"));

        assertEquals(Thread.State.NEW, thread.getState());
        thread.start();

        assertEquals(Thread.State.RUNNABLE, thread.getState());
        thread.join();
    }
}
