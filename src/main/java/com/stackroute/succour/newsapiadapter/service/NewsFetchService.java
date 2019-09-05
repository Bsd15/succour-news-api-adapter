package com.stackroute.succour.newsapiadapter.service;

import com.ibm.common.activitystreams.Activity;
import com.stackroute.succour.newsapiadapter.domain.Article;
import com.stackroute.succour.newsapiadapter.domain.NewsAPIResponseObject;
import com.stackroute.succour.newsapiadapter.exceptions.EmptyArticlesException;
import io.reactivex.subjects.PublishSubject;
import lombok.*;
import org.quartz.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Service class to fetch data from newapi.org. The fields APIQueryURI, webClient,
 * articlesList will be passed from NewsAPIAdapter class using jobDataMap and will be
 * automatically assigned to the fields using the getters and setters.
 *
 * The object of this class will be created and destroyed again and again and thus @PersistJobDataAfterExecution and
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
    private List<Article> articlesList;
    private PublishSubject<Article> articlePublishSubject;

    private int callCount;

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
        System.out.println("API Called " + callCount + " times\n\n\n\n\n");
        setCallCount(++callCount);
//        articlesList.addAll(Arrays.asList(newsAPIResponseObject.getArticles()));
        for (Article article : newsAPIResponseObject.getArticles()) {
            articlePublishSubject.onNext(article);
        }

    }

    private Activity convertToActivityStream(Article article) {
//        Activity articleActivity = activity().object(ob)
        return null;
    }
}
