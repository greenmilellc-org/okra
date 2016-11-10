# Okra
A simple and scalable Scheduler that uses MongoDB as backend

### Requirements

* Java 8
* MongoDB

#### Note
Right now Okra was only one module that requires Spring Data MongoDB to work, but you can send a Pull Request any time creating a new maven module (like okra-spring) that requires only the raw MongoDB Driver.

### Quick start

Configure a scheduler
```java
public class MyMongoScheduler {
    
    private MongoScheduler scheduler;
    ...
    public void initScheduler() {
        scheduler = new SpringMongoSchedulerBuilder<DefaultScheduledItem>()
                        .withMongoTemplate(new MongoTemplate(client, "schedulerBenchmark"))
                        .withDatabase("schedulerBenchmark")
                        .withSchedulerCollectionName("schedulerCollection")
                        .withExpiration(5, TimeUnit.MINUTES)
                        .withScheduledItemClass(DefaultScheduledItem.class)
                        .validateAndBuild();        
    }
    ...    
}
```

Then, use this scheduler to retrieve scheduled items...

```java
public class MyMongoScheduler {
    
    private MongoScheduler scheduler;
    ...    
    public void retrieveLoop() {
        while (running) {
            Optional<DefaultScheduledItem> scheduledOpt = scheduler.poll();
                if (scheduled.isPresent()) {
                    doSomeWork(scheduledOpt.get());                
                }    
        }
    }
    ...    
}
```


### License
```
MIT License

Copyright (c) 2016 Fernando Nogueira

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```