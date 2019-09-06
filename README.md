# Adapter for Newsapi.org

The adapter fetches the data from the newapi.org using Quartz scheduler and publishes data to whomever subscribes to it using RxJava.

The adapter makes request to the newapi.org server after every 3 minutes **(as the limit is 500 requests, the number of request calls are limited to 1 per 3 minutes)**.

### Wiki

**Example**

To start the news stream
```java
NewsAPIAdapter newsAPIAdapter = new NewsAPIAdapter();
newsAPIAdapter.addQueryParam("india");
PublishSubject<Activity> publishSubject = newsAPIAdapter.getArticleSubject();
Disposable disposable = publishSubject.subscribe(activity -> {
    System.out.println(activity);
});
newsAPIAdapter.startNewsStream();
```

To stop the news stream 
```java
disposable.dispose();
newsAPIAdapter.stopNewsStream();
```

References:
- [Flux class documentation](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)
- [Scheduling for repeated calling of methods](https://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled)
- [WebFlux Documentation](https://docs.spring.io/spring/docs/5.1.9.RELEASE/spring-framework-reference/web-reactive.html#webflux)
- [Subject in RxJS](http://xgrommx.github.io/rx-book/content/getting_started_with_rxjs/subjects.html)
- [PublishSubject JavaDoc](http://reactivex.io/RxJava/javadoc/rx/subjects/PublishSubject.html)
- [rxjava add items after observable was created](https://stackoverflow.com/q/28913979)