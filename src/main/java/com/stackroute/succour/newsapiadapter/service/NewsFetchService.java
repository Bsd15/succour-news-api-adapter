package com.stackroute.succour.newsapiadapter.service;

import com.stackroute.succour.newsapiadapter.domain.Article;
import com.stackroute.succour.newsapiadapter.domain.NewsAPIResponseObject;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyArticlesException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableScheduling
public class NewsFetchService {
    private String API_KEY;
    private URI apiQueryURI;
    public Flux articleFlux; /*Flux to store the article objects*/
    private List<Article> articlesList;

    private WebClient webClient = WebClient.create();

    /**
     * Reads properties from application.properties using java.util.Properties
     *
     * @throws IOException
     */
    public NewsFetchService(String API_KEY, URI apiQueryURI) throws IOException {
        this.API_KEY = API_KEY;
        this.apiQueryURI = apiQueryURI;
        articlesList = new ArrayList(); /*Initialize arraylist to store articles called during getNews*/
        articleFlux = Flux.fromIterable(articlesList); /*Create flux from arraylist*/
    }

    /**
     * Queries newsapi.org for every news article containing the given keywords and returns
     * them as Flux
     * TODO change the method to take in parameters and add them as queries in URI
     *
     * @return Flux<Article> News articles as JSON
     */
//    @GetMapping("getNews")
    @Scheduled(fixedDelay = 1000)
    public void getNews() throws EmptyArticlesException {
        /*
         * Make the call to the URI using WebClient.
         * Retrieve the result as a Mono object as the retrieved result is a single
         * JSON object containing all the articles inside as an Array.
         * Add a header containing the API_KEY for authentication.
         * The call is <b>Synchronous</b>.
         * */
        NewsAPIResponseObject newsAPIResponseObject = webClient.get()
                .uri(apiQueryURI)
                /*Put the API Key in header for Authentication. Recommended by Documentation in newapi.org*/
                .header("X-Api-Key", API_KEY)
                .retrieve()
                .bodyToMono(NewsAPIResponseObject.class)
                .block();
        /*
         * Assert if newsAPIResponseObject is null.
         * Get the articles from the NewsAPIResponseObject.
         * */
        assert newsAPIResponseObject != null : "Empty NewsAPIResponseObject";
        Article[] articles = newsAPIResponseObject.getArticles();
        /*
         * Check if articles array is null or empty.
         * If yes throw EmptyArticlesException
         * else return a Flux object.
         * */
        if (articles == null || articles.length <= 0) {
            throw new EmptyArticlesException("No Articles present in the response");
        }
        articlesList.addAll(Arrays.asList(articles));
    }
}
