package com.stromsland.dicejobsearch.controller;

import com.stromsland.dicejobsearch.model.JobListing;
import com.stromsland.dicejobsearch.service.JobSearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
 
@RestController
public class JobSearchController {

    public record SearchResponse(String query, List<JobListing> results) {}
    private final JobSearchService jobSearchService;
    private final String defaultQuery;
 
    public JobSearchController(JobSearchService jobSearchService, @Value("${jobsearch.default-query}") String defaultQuery) {
        this.jobSearchService = jobSearchService;
        this.defaultQuery = defaultQuery;
    }

    @GetMapping("/api")
    public SearchResponse initiateSearch(@RequestParam(value = "query", required = false) String query) {
        String searchQuery = StringUtils.hasText(query) ? query : defaultQuery;
        List<JobListing> results = jobSearchService.searchJobs(searchQuery);

        return new SearchResponse(searchQuery, results);
    }
}