package virtual_threads.part2;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import virtual_threads.AbstractTest;

import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Rule3DoNotPoolVirtualThreadsTest extends AbstractTest {

    @Nested
    public class DoNot {

        @Test
        public void poolVirtualThreads() {
            try (var executorService = Executors.newCachedThreadPool(Thread.ofVirtual().factory())) {
                assertEquals("java.util.concurrent.ThreadPoolExecutor", executorService.getClass().getName());

                executorService.submit(() -> { sleep(1000); System.out.println("run"); });
            }
        }
    }

    @Nested
    public class Do {

        @Test
        public void createVirtualThreadPerTask() {
            try (var executorService = Executors.newVirtualThreadPerTaskExecutor()) {
                assertEquals("java.util.concurrent.ThreadPerTaskExecutor", executorService.getClass().getName());

                executorService.submit(() -> { sleep(1000); System.out.println("run"); });
            }
        }
    }
}
