package com.stackroute.succour.newsapiadapter.service;

import com.stackroute.succour.newsapiadapter.domain.Article;
import com.stackroute.succour.newsapiadapter.domain.NewsAPIResponseObject;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyArticlesException;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.quartz.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NewsFetchService implements Job {
    private String API_KEY;
    private URI APIQueryURI;
    private WebClient webClient;
    private List<Article> articlesList;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        /*
         * Make the call to the URI using WebClient.
         * Retrieve the result as a Mono object as the retrieved result is a single
         * JSON object containing all the articles inside as an Array.
         * Add a header containing the API_KEY for authentication.
         * The call is <b>Synchronous</b>.
         * */
        NewsAPIResponseObject newsAPIResponseObject = webClient.get()
                .uri(APIQueryURI)
                /*Put the API Key in header for Authentication. Recommended by Documentation in newapi.org*/
                .header("X-Api-Key", API_KEY)
                .retrieve()
                .bodyToMono(NewsAPIResponseObject.class)
                .block();
        assert newsAPIResponseObject != null : new EmptyArticlesException();
//        System.out.println(newsAPIResponseObject.toString());
        articlesList.addAll(Arrays.asList(newsAPIResponseObject.getArticles()));
    }
}
