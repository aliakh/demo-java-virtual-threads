package virtual_threads.part1;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Example4VirtualThreadFactoryTest {

    @Test
    public void virtualThreadFactory() {
        Thread.Builder builder = Thread.ofVirtual()
            .name("virtual thread");

        ThreadFactory factory = builder.factory();
        assertEquals("java.lang.ThreadBuilders$VirtualThreadFactory", factory.getClass().getName());
        Thread thread = factory.newThread(() -> System.out.println("run"));

        assertEquals("java.lang.VirtualThread", thread.getClass().getName());
        assertTrue(thread.isVirtual());
        assertEquals("virtual thread", thread.getName());
        assertEquals(Thread.State.NEW, thread.getState());
    }
}
