package demo.virtualthreads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class AbstractTest {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected void sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    protected <T> T sleepAndGet(int millis, T value) {
        logger.info("task({}) started", value);
        sleep(millis);
        logger.info("task({}) finished", value);
        return value;
    }
}
