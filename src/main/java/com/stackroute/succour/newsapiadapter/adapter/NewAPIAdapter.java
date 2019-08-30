package com.stackroute.succour.newsapiadapter.adapter;

import com.stackroute.succour.newsapiadapter.exceptions.EmptyAPIQueryURIException;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyQueryParamsException;
import com.stackroute.succour.newsapiadapter.service.NewsFetchService;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class NewAPIAdapter {

    private List<String> queryParams; /*List of query strings to be added to the URI */
    private String API_KEY; /*Get API Key from properties*/
    private String BASE_URI; /*Get BASE_URI from properties*/
    private URI apiQueryURI = null; /*To store the URI formed by URIBuilder*/

    public NewAPIAdapter() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "application.properties";
        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        API_KEY = appProps.getProperty("api_key");
        BASE_URI = appProps.getProperty("base_uri");
    }

    public List<String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<String> queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * Add query to the queryParams list.
     *
     * @param queryParam String to be added to query params list.
     */
    public void addQueryParam(String queryParam) {
        /*
         * Check if queryParams list is empty and if it's empty then
         * initialize it.
         * */
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<>();
        }
        this.queryParams.add(queryParam);
    }

    /*
     * Dynamically create URI required for calling the newsapi endpoint.
     * Uses apache's HttpClient.URIBuilder
     * Dynamically appends all the arguments as parameters for the URI
     * Example query : "https://newsapi.org/v2/everything?q=Amazon fires&language=en"
     * */
    private void buildURI() throws EmptyQueryParamsException {
        try {
            URIBuilder apiURIBuilder = new URIBuilder(BASE_URI);
            queryParams.forEach(queryParam -> apiURIBuilder.addParameter("q", queryParam));
            apiURIBuilder.addParameter("language", "en");
            if (this.queryParams != null && (!this.queryParams.isEmpty())) {
                this.apiQueryURI = apiURIBuilder.build();
            } else {
                throw new EmptyQueryParamsException();
            }
        } catch (URISyntaxException e) {
            // TODO Handle exception to logger or something else
            e.printStackTrace();
        }
    }

    public void startAdapter() throws EmptyQueryParamsException, EmptyAPIQueryURIException, IOException {
        /*Check if queryParams are not null*/
        if (this.queryParams != null && (!this.queryParams.isEmpty())) {
            buildURI();
            /*Check if URI is not null*/
            if (apiQueryURI != null) {
                NewsFetchService service = new NewsFetchService(API_KEY, apiQueryURI);
            } else {
                throw new EmptyAPIQueryURIException();
            }
        } else {
            throw new EmptyQueryParamsException();
        }
    }

}
