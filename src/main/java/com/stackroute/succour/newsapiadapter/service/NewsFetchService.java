package com.stackroute.succour.newsapiadapter.service;


import com.ibm.common.activitystreams.Activity;
import com.stackroute.succour.newsapiadapter.domain.Article;
import com.stackroute.succour.newsapiadapter.domain.NewsAPIResponseObject;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyArticlesException;
import io.reactivex.subjects.PublishSubject;
import lombok.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

import static com.ibm.common.activitystreams.Makers.activity;
import static com.ibm.common.activitystreams.Makers.object;

/**
 * Service class to fetch data from newapi.org. The fields APIQueryURI, webClient,
 * articlesList will be passed from NewsAPIAdapter class using jobDataMap and will be
 * automatically assigned to the fields using the getters and setters.
 * <p>
 * The object of this class will be created and destroyed again and again and thus @PersistJobDataAfterExecution and
 *
 * @DisallowConcurrentExecution are used to make the same data available for other instances
 * of this class.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NewsFetchService implements Job {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final String API_KEY = "389599afec1e4727a7de75f65b5f050c"; /*API Key required for newapi.org*/
    private URI APIQueryURI; /*To be passed from NewsAPIAdapter*/
    private WebClient webClient;
    private PublishSubject<Activity> articlePublishSubject;
    private Logger logger = LoggerFactory.getLogger(NewsFetchService.class);

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
        /*Convert the articles to activity stream object and send to publishSubject*/
        for (Article article : newsAPIResponseObject.getArticles()) {
            assert article == null : "Empty article";
            assert article.getContent() == null : "Empty content";
            logger.info(article.toString());
            articlePublishSubject.onNext(convertToActivity(article));
        }

    }

    /**
     * Convert the given article object into a Activity stream object.
     * @param article Article to converted to activity stream object
     * @return Activity
     */
    private Activity convertToActivity(Article article) {
        return activity()
                .verb("post")
                .actor("News-Adapter")
                .object(object("article").content(article.getContent()))
                .get();
    }
}
