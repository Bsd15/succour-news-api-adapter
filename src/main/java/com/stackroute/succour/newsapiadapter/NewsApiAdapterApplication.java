package com.stackroute.succour.newsapiadapter;

import com.stackroute.succour.newsapiadapter.adapter.NewAPIAdapter;
import com.stackroute.succour.newsapiadapter.domain.Article;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyArticlesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class NewsApiAdapterApplication {

	@Autowired
	private NewAPIAdapter newAPIAdapter;

	public static void main(String[] args) {
		SpringApplication.run(NewsApiAdapterApplication.class, args);
		NewAPIAdapter newAPIAdapter = null;
		try {
			newAPIAdapter = new NewAPIAdapter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Flux<Article> returnData= null;
		try {
			returnData = newAPIAdapter.getNews();
			List<Article> articleList = new ArrayList<>();
			returnData.subscribe(articleList::add);
			for (Article article : articleList) {
				System.out.println("-----------------");
				System.out.println(article);
				System.out.println("-----------------");
			}
		} catch (EmptyArticlesException e) {
			e.printStackTrace();
		}
	}

}
