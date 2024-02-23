package virtual_threads.part2;

import org.junit.jupiter.api.Nested;

import java.util.concurrent.locks.ReentrantLock;

public class Rule6UseSynchronizedBlocksAndMethodsCarefullyTest {

    @Nested
    public class DoNot {

        private final Object lockObject = new Object();

        public String useSynchronizedBlockForExclusiveAccess() {
            synchronized (lockObject) {
                return exclusiveResource();
            }
        }

        private String exclusiveResource() {
            return "result";
        }
    }

    @Nested
    public class Do {

        private final ReentrantLock reentrantLock = new ReentrantLock();

        public String useReentrantLockForExclusiveAccess() {
            reentrantLock.lock();
            try {
                return exclusiveResource();
            } finally {
                reentrantLock.unlock();
            }
        }

        private String exclusiveResource() {
            return "result";
        }
    }
}
