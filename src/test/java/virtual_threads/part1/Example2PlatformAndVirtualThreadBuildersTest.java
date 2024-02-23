package virtual_threads.part1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Example2PlatformAndVirtualThreadBuildersTest {

    @Test
    public void usePlatformThreadBuilderTest() {
        Thread.Builder builder = Thread.ofPlatform()
            .daemon(false)
            .priority(10)
            .stackSize(1024)
            .name("platform thread")
            .inheritInheritableThreadLocals(false)
            .uncaughtExceptionHandler((t, e) -> System.out.printf("thread %s failed with exception %s", t, e));
        System.out.println(builder.getClass().getName());
        assertEquals("java.lang.ThreadBuilders$PlatformThreadBuilder", builder.getClass().getName());

        Thread thread = builder.unstarted(() -> System.out.println("run"));

        assertEquals("java.lang.Thread", thread.getClass().getName());
        assertEquals("platform thread", thread.getName());
        assertFalse(thread.isDaemon());
        assertEquals(10, thread.getPriority());
    }

    @Test
    public void useVirtualThreadBuilderTest() {
        Thread.Builder builder = Thread.ofVirtual()
            .name("virtual thread")
            .inheritInheritableThreadLocals(false)
            .uncaughtExceptionHandler((t, e) -> System.out.printf("thread %s failed with exception %s", t, e));
        assertEquals("java.lang.ThreadBuilders$VirtualThreadBuilder", builder.getClass().getName());

        Thread thread = builder.unstarted(() -> System.out.println("run"));

        assertEquals("java.lang.VirtualThread", thread.getClass().getName());
        assertEquals("virtual thread", thread.getName());
        assertTrue(thread.isDaemon());
        assertEquals(5, thread.getPriority());
    }
}
