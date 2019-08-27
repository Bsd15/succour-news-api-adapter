package com.stackroute.succour.newsapiadapter.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.stackroute.succour.newsapiadapter.domain.NewsAPIResponseObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * Controller for Newsapi.org containing route to return data from
 * newapi.org as a Flux object
 */
@RestController
@RequestMapping("api/v1/")
public class AdapterController {

    @Value("${api_key}")
    private String API_KEY; /*Get API Key from properties*/

    private WebClient webClient = WebClient.builder()
            .baseUrl("https://newsapi.org/v2/") /*Attach the base URL*/
            .build(); /*Build the WebClient object*/

    /**
     * @return
     */
    @GetMapping("getNews")
    public Flux<NewsAPIResponseObject> getNews() {
        return webClient.get()
                //TODO edit endpoint to query for articles
                .uri("top-headlines?country=us")
                /*Put the API Key in header for Authentication. Recommended by Documentation in newapi.org*/
                .header("X-Api-Key", API_KEY)
                .retrieve()
                .bodyToFlux(NewsAPIResponseObject.class);
    }
}
