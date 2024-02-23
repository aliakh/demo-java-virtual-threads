package virtual_threads.part2;

import org.junit.jupiter.api.Nested;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class Rule4UseSemaphoresInsteadOfFixedThreadPoolsTest {

    @Nested
    public class DoNot {

        private final ExecutorService executorService = Executors.newFixedThreadPool(8);

        public String useFixedExecutorServiceToLimitConcurrency() throws ExecutionException, InterruptedException {
            Future<String> future = executorService.submit(this::sharedResource);
            return future.get();
        }

        private String sharedResource() {
            return "result";
        }
    }

    @Nested
    public class Do {

        private final Semaphore semaphore = new Semaphore(8);

        public String useSemaphoreToLimitConcurrency() throws InterruptedException {
            semaphore.acquire();
            try {
                return sharedResource();
            } finally {
                semaphore.release();
            }
        }

        private String sharedResource() {
            return "result";
        }
    }
}
