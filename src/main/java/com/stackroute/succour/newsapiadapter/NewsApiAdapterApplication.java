package com.stackroute.succour.newsapiadapter;

import com.ibm.common.activitystreams.Activity;
import com.stackroute.succour.newsapiadapter.adapter.NewsAPIAdapter;
import com.stackroute.succour.newsapiadapter.domain.Article;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyAPIQueryURIException;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyQueryParamsException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import org.quartz.SchedulerException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class NewsApiAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsApiAdapterApplication.class, args);
		try {
			NewsAPIAdapter newsAPIAdapter = new NewsAPIAdapter();
			newsAPIAdapter.addQueryParam("india");
//			Thread.sleep(60L * 100L);
//			newsAPIAdapter.getNewsStream().subscribe(article -> System.out.println(article));
			PublishSubject<Activity> publishSubject = newsAPIAdapter.getArticleSubject();
			Disposable disposable = publishSubject.subscribe(System.out::println);
			publishSubject.doOnDispose(() -> System.out.println("Disposed"));
			newsAPIAdapter.startNewsStream();
			Thread.sleep(60L * 300L);
			disposable.dispose();
			System.out.println("Disposed");
			Thread.sleep(60L * 100L);
			newsAPIAdapter.stopNewsStream();
		} catch (IOException | EmptyQueryParamsException e) {
			e.printStackTrace();
		} catch (EmptyAPIQueryURIException e) {
			e.printStackTrace();
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
