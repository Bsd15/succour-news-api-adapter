package com.stackroute.succour.newsapiadapter.adapter;

import com.stackroute.succour.newsapiadapter.domain.Article;
import com.stackroute.succour.newsapiadapter.domain.NewsAPIResponseObject;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyAPIQueryURIException;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyQueryParamsException;
import com.stackroute.succour.newsapiadapter.service.NewsFetchService;
import org.apache.http.client.utils.URIBuilder;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


public class NewsAPIAdapter {

    private List<String> queryParams; /*List of query strings to be added to the URI */
    private String API_KEY; /*Get API Key from properties*/
    private String BASE_URI; /*Get BASE_URI from properties*/
    private URI apiQueryURI = null; /*To store the URI formed by URIBuilder*/
    private Flux<Article[]> newResponseFlux;
    private WebClient webClient = WebClient.create();
    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;
    private JobDetail newsFetchJob;
    private Trigger trigger;
    private ArrayList<Article> articlesList;

    public NewsAPIAdapter() throws IOException, SchedulerException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "application.properties";
        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        API_KEY = appProps.getProperty("api_key");
        BASE_URI = appProps.getProperty("base_uri");
        schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        articlesList = new ArrayList();
        trigger = newTrigger().withIdentity("myTrigger", "group1").startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(5).repeatForever()).build();
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

    /**
     * Queries newsapi.org for every news article containing the given keywords and returns
     * them as Flux
     * TODO change the method to take in parameters and add them as queries in URI
     *
     * @return NewsAPIResponseObject
     */

    public Article[] fetchNews() {
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

        return newsAPIResponseObject.getArticles();
    }

    private void addJobToScheduler() throws SchedulerException {
        if (newsFetchJob != null && trigger != null) {
            scheduler.scheduleJob(newsFetchJob, trigger);
        }
    }

    private void startNewsFetchService() throws SchedulerException {
        scheduler.start();
    }

    private void stopNewsFetchService() throws SchedulerException {
        scheduler.shutdown(true);
    }

    public void startNewsStream() throws EmptyQueryParamsException, EmptyAPIQueryURIException, SchedulerException {


        if (this.queryParams != null && (!this.queryParams.isEmpty())) {
            buildURI();
            /*Check if URI is not null*/
            if (apiQueryURI != null) {
                initNewsFetchJob();
                addJobToScheduler();
                startNewsFetchService();
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(60L * 100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(articlesList.toString());
                }
            } else {
                throw new EmptyAPIQueryURIException();
            }
        } else {
            throw new EmptyQueryParamsException();
        }
    }

    private void initNewsFetchJob() {
        if (webClient != null && articlesList != null && apiQueryURI != null) {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("API_KEY",API_KEY);
            jobDataMap.put("APIQueryURI", apiQueryURI);
            jobDataMap.put("webClient", webClient);
            jobDataMap.put("articlesList", articlesList);
            newsFetchJob = newJob(NewsFetchService.class)
                    .withIdentity("job1", "group1")
                    .usingJobData(jobDataMap)
                    .build();
        }
    }

    public void stopNewsStream() throws SchedulerException {
        stopNewsFetchService();
    }

}
