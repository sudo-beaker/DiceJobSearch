package com.stromsland.dicejobsearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JobListing(
        String id,
        String title,
        String summary,
        String companyName,
        @JsonProperty("jobLocation")
        String jobLocation,
        String detailsPageUrl,
        String companyPageUrl,
        String salary,

        String employmentType,
        String workplaceTypes,
        String postedDate,
        boolean easyApply,


        String diceId
) {}