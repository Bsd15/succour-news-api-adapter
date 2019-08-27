package com.stackroute.succour.newsapiadapter.domain;

import lombok.*;

/**
 * Entity object to store the response from newapi.org
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewsAPIResponseObject {
    private String status;
    private int totalResults;
    private Article[] articles;
}
