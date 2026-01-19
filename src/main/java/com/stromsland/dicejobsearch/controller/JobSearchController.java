package com.stromsland.dicejobsearch.controller;
 
import com.stromsland.dicejobsearch.service.JobSearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import java.util.Map;
import java.util.Optional;
 
@RestController
public class JobSearchController {
 
    private final JobSearchService jobSearchService;
    private final String defaultQuery;
 
    public JobSearchController(JobSearchService jobSearchService, @Value("${jobsearch.default-query}") String defaultQuery) {
        this.jobSearchService = jobSearchService;
        this.defaultQuery = defaultQuery;
    }
 
    @GetMapping("/default")
    public Map<String, String> initiateSearch(@RequestParam(value = "query", required = false) String query) {
        String searchQuery = Optional.ofNullable(query).orElse(defaultQuery);
        String results = jobSearchService.searchJobs(searchQuery);
        // Returning a Map ensures the response is serialized as JSON
        return Map.of("query", searchQuery, "results", results);
    }
}