package demo.virtualthreads.part1;

import demo.virtualthreads.AbstractTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Example3CreateStartedAndUnstartedVirtualThreadsTest extends AbstractTest {

    @Test
    public void createStartedThreadTest() throws InterruptedException {
        Thread.Builder builder = Thread.ofVirtual();

        // Pitfall: start() launches the thread immediately, so by the time we reach getState() the thread may already be in TIMED_WAITING (inside sleep) rather than RUNNABLE
        Thread thread = builder.start(() -> {
            sleep(1000);
            System.out.println("run");
        });

        // Pitfall: this assertion is non-deterministic — it depends on scheduling timing. The thread could be RUNNABLE or TIMED_WAITING depending on how fast it enters sleep()
        assertEquals(Thread.State.RUNNABLE, thread.getState());

        thread.join();
    }

    @Test
    public void createUnstartedThreadTest() throws InterruptedException {
        Thread.Builder builder = Thread.ofVirtual();
        Thread thread = builder.unstarted(() -> {
            sleep(1000);
            System.out.println("run");
        });

        // Safe: thread has not been started yet, so NEW is guaranteed
        assertEquals(Thread.State.NEW, thread.getState());
        thread.start();

        // Pitfall: same race as above — after start(), the thread may already be in TIMED_WAITING (inside sleep) before this line executes
        assertEquals(Thread.State.RUNNABLE, thread.getState());
        thread.join();
    }
}
