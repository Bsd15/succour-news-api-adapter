package com.stackroute.succour.newsapiadapter;

import com.stackroute.succour.newsapiadapter.adapter.NewsAPIAdapter;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyAPIQueryURIException;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyQueryParamsException;
import org.quartz.SchedulerException;
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
			newsAPIAdapter.startNewsStream();
		} catch (IOException | EmptyQueryParamsException e) {
			e.printStackTrace();
		} catch (EmptyAPIQueryURIException e) {
			e.printStackTrace();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

}
