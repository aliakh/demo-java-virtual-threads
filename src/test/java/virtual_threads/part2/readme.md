## Code examples


### Write blocking synchronous code in the thread-per-request model

The following non-blocking asynchronous code will not benefit much from using virtual threads, because the `CompletableFuture` class already reuses worker threads from its executor between its stages:

<sub>The following code is a simplified example of an asynchronous multistage workflow. First, we call two long-running methods that return a product price in the EUR and the EUR/USD exchange rate. Then we calculate the net product price from the results of these methods. Then we call the third long-running method that takes the net product price and returns the tax amount. Finally, we calculate the gross product price from the net product price and the tax amount.</sub>


```java
public void useAsynchronousCode() throws InterruptedException, ExecutionException {
   CompletableFuture.supplyAsync(this::readPriceInEur)
       .thenCombine(CompletableFuture.supplyAsync(this::readExchangeRateEurToUsd), (price, exchangeRate) -> price * exchangeRate)
       .thenCompose(amount -> CompletableFuture.supplyAsync(() -> amount * (1 + readTax(amount))))
       .whenComplete((grossAmountInUsd, t) -> {
           if (t == null) {
               assertEquals(108, grossAmountInUsd.intValue());
           } else {
               fail(t);
           }
       })
       .get();
}
```


The following blocking synchronous code will benefit from using virtual threads because the much simpler code returns the same value for the same duration as the previous complex one:


```java
public void useSynchronousCode() throws InterruptedException, ExecutionException {
   try (var executorService = Executors.newVirtualThreadPerTaskExecutor()) {
       Future<Integer> priceInEur = executorService.submit(this::readPriceInEur);
       Future<Float> exchangeRateEurToUsd = executorService.submit(this::readExchangeRateEurToUsd);
       float netAmountInUsd = priceInEur.get() * exchangeRateEurToUsd.get();

       Future<Float> tax = executorService.submit(() -> readTax(netAmountInUsd));
       float grossAmountInUsd = netAmountInUsd * (1 + tax.get());
       assertEquals(108, (int) grossAmountInUsd);
   }
}
```



### Do not pool virtual threads

The following code needlessly uses a cached thread pool executor to reuse virtual threads between tasks:


```java
public void poolVirtualThreads() {
   try (var executorService = Executors.newCachedThreadPool(Thread.ofVirtual().factory())) {
       assertEquals("java.util.concurrent.ThreadPoolExecutor", executorService.getClass().getName());

       executorService.submit(() -> { sleep(1000); System.out.println("run"); });
   }
}
```


The following code correctly uses a _thread-per-task_ virtual thread executor to create a new thread for each task:


```java
public void createVirtualThreadPerTask() {
   try (var executorService = Executors.newVirtualThreadPerTaskExecutor()) {
       assertEquals("java.util.concurrent.ThreadPerTaskExecutor", executorService.getClass().getName());

       executorService.submit(() -> { sleep(1000); System.out.println("run"); });
   }
}
```



### Use semaphores instead of fixed thread pools to limit concurrency

The following code, which uses a fixed pool of threads to limit concurrency when accessing some shared resource, will not benefit from the use of virtual threads:


```java
private final ExecutorService executorService = Executors.newFixedThreadPool(8);

public String useFixedExecutorServiceToLimitConcurrency() throws ExecutionException, InterruptedException {
   Future<String> future = executorService.submit(this::sharedResource());
   return future.get();
}
```


The following code, which uses a `Semaphore` to limit concurrency when accessing some shared resource, will benefit from the use of virtual threads:


```java
private final Semaphore semaphore = new Semaphore(8);

public String useSemaphoreToLimitConcurrency() throws InterruptedException {
   semaphore.acquire();
   try {
       return sharedResource();
   } finally {
       semaphore.release();
   }
}
```



### Use thread-local variables carefully or switch to scoped values

The following code shows that a thread-local variable is mutable, is inherited in a child thread started from the parent thread, and exists until it is removed.


```java
private final InheritableThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

public void useThreadLocalVariable() throws InterruptedException {
   threadLocal.set("zero");
   assertEquals("zero", threadLocal.get());

   threadLocal.set("one");
   assertEquals("one", threadLocal.get());

   Thread childThread = new Thread(() -> {
      assertEquals("one", threadLocal.get());
   });
   childThread.start();
   childThread.join();

   threadLocal.remove();
   assertNull(threadLocal.get());
}
```


The following code shows that a scoped value is immutable, is reused in a structured concurrency scope, and exists only in a bounded context.


```java
private final ScopedValue<String> scopedValue = ScopedValue.newInstance();

public void useScopedValue() {
   ScopedValue.where(scopedValue, "zero").run(
       () -> {
           assertEquals("zero", scopedValue.get());
           ScopedValue.where(scopedValue, "one").run(
               () -> assertEquals("one", scopedValue.get())
           );
           assertEquals("zero", scopedValue.get());

           try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
               scope.fork(() -> {
                       assertEquals("zero", scopedValue.get());
                       return -1;
                   }
               );
               scope.join().throwIfFailed();
           } catch (InterruptedException | ExecutionException e) {
               fail(e);
           }
       }
   );

   assertThrows(NoSuchElementException.class, scopedValue::get);
}
```



### Use synchronized blocks and methods carefully or switch to reentrant locks

The following code uses a _synchronized_ block with an explicit object lock that causes pinning of virtual threads:


```java
private final Object lockObject = new Object();

public String useSynchronizedBlockForExclusiveAccess() {
   synchronized (lockObject) {
       return exclusiveResource();
   }
}
```


The following code uses a `ReentrantLock` that does not cause pinning of virtual threads:


```java
private final ReentrantLock reentrantLock = new ReentrantLock();

public String useReentrantLockForExclusiveAccess() {
   reentrantLock.lock();
   try {
       return exclusiveResource();
   } finally {
       reentrantLock.unlock();
   }
}
```
