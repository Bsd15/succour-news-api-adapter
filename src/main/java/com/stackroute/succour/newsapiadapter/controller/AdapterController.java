package com.stackroute.succour.newsapiadapter.controller;

import com.stackroute.succour.newsapiadapter.domain.NewsAPIResponseObject;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Controller for Newsapi.org containing route to return data from
 * newapi.org as a Flux object
 */
@RestController
@RequestMapping("api/v1/")
public class AdapterController {

    @Value("${api_key}")
    private String API_KEY; /*Get API Key from properties*/

    @Value("${api_endpoint}")
    private String API_ENDPOINT;

    private WebClient webClient = WebClient.create();

    /**
     * Queries newsapi.org for every news article containing the given keywords
     *TODO change the method to take in parameters and add them as queries in URI
     *
     * @return Flux<NewsAPIResponseObject> News articles as JSON
     */
    @GetMapping("getNews")
    public Flux<NewsAPIResponseObject> getNews() {
        URI apiQueryURI = null; /*To store the URI formed by URIBuilder*/
        try {
            /*Example query : "https://newsapi.org/v2/everything?q=Amazon fires&language=en" */
            URIBuilder apiURIBuilder = new URIBuilder(API_ENDPOINT);
            apiURIBuilder.addParameter("q", "Indian economy");
//            apiURIBuilder.addParameter("q", "Brazil");
            apiURIBuilder.addParameter("language", "en");
            apiQueryURI = apiURIBuilder.build();
        } catch (URISyntaxException e) {
            // TODO Handle exception to logger or something else
            e.printStackTrace();
        }

        return webClient.get()
                //TODO edit endpoint to query for articles
                .uri(apiQueryURI)
                /*Put the API Key in header for Authentication. Recommended by Documentation in newapi.org*/
                .header("X-Api-Key", API_KEY)
                .retrieve()
                .bodyToFlux(NewsAPIResponseObject.class);
    }
}
