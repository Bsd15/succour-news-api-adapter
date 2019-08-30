package com.stackroute.succour.newsapiadapter;

import com.stackroute.succour.newsapiadapter.adapter.NewAPIAdapter;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyAPIQueryURIException;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyQueryParamsException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class NewsApiAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsApiAdapterApplication.class, args);
		try {
			NewAPIAdapter newAPIAdapter = new NewAPIAdapter();
			newAPIAdapter.addQueryParam("india");
			newAPIAdapter.startAdapter();
		} catch (IOException | EmptyQueryParamsException e) {
			e.printStackTrace();
		} catch (EmptyAPIQueryURIException e) {
			e.printStackTrace();
		}
	}

}
