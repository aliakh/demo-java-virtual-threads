package virtual_threads.part1;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Example5VirtualThreadPerTaskExecutorTest {

    @Test
    public void virtualThreadPerTaskExecutorTest() throws InterruptedException, ExecutionException {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            assertEquals("java.util.concurrent.ThreadPerTaskExecutor", executorService.getClass().getName());

            Future<?> future = executorService.submit(() -> System.out.println("run"));
            future.get();
        }
    }
}
