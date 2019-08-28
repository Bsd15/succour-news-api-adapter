package com.stackroute.succour.newsapiadapter.adapter;

import com.stackroute.succour.newsapiadapter.domain.Article;
import com.stackroute.succour.newsapiadapter.domain.NewsAPIResponseObject;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyArticlesException;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Controller for Newsapi.org containing route to return data from
 * newapi.org as a Flux object
 */

@Service
@EnableScheduling
public class NewAPIAdapter {

    private String API_KEY; /*Get API Key from properties*/
    private String API_ENDPOINT;
    private Properties appProps;

    private WebClient webClient = WebClient.create();

    /**
     * Reads properties from application.properties using java.util.Properties
     *
     * @throws IOException
     */
    public NewAPIAdapter() throws IOException {
        // Get the path of target folder.
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "application.properties";
        appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        API_KEY = appProps.getProperty("api_key");
        API_ENDPOINT = appProps.getProperty("api_endpoint");
    }

    /**
     * Queries newsapi.org for every news article containing the given keywords and returns
     * them as Flux
     * TODO change the method to take in parameters and add them as queries in URI
     *
     * @return Flux<Article> News articles as JSON
     */
//    @GetMapping("getNews")
    public Flux<Article> getNews() throws EmptyArticlesException {
        URI apiQueryURI = null; /*To store the URI formed by URIBuilder*/
        /*
         * Dynamically create URI required for calling the newsapi endpoint.
         * Uses apache's HttpClient.URIBuilder
         * Dynamically appends all the arguments as parameters for the URI
         * */
        try {
            /*Example query : "https://newsapi.org/v2/everything?q=Amazon fires&language=en" */
            URIBuilder apiURIBuilder = new URIBuilder(API_ENDPOINT);
            apiURIBuilder.addParameter("q", "Indian economy");
            apiURIBuilder.addParameter("language", "en");
            apiQueryURI = apiURIBuilder.build();
        } catch (URISyntaxException e) {
            // TODO Handle exception to logger or something else
            e.printStackTrace();
        }
        /*
         * Make the call to the URI using WebClient.
         * Retrieve the result as a Mono object as the retrieved result is a single
         * JSON object containing all the articles inside as an Array.
         * Add a header containing the API_KEY for authentication.
         * The call is <b>Synchronous</b>.
         * */
        NewsAPIResponseObject newsAPIResponseObject = webClient.get()
                //TODO edit endpoint to query for articles
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
        return Flux.fromArray(newsAPIResponseObject.getArticles());
    }

    @Scheduled(fixedDelay = 1000)
    public void testScheduled(){
        System.out.println("Print");
    }


}
