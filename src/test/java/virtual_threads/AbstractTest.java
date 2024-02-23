package virtual_threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

abstract public class AbstractTest {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected void sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T sleepAndGet(int millis, T value) {
        logger.info("{} started", value);
        sleep(millis);
        logger.info("{} finished", value);
        return value;
    }
}
