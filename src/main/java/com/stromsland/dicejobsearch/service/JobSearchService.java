package com.stromsland.dicejobsearch.service;


import com.stromsland.dicejobsearch.model.DiceJobEntity;
import com.stromsland.dicejobsearch.model.JobListing;
import com.stromsland.dicejobsearch.repository.DiceJobRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
@Service
public class JobSearchService {
 
    private final ChatClient chatClient;
    private final RestTemplate restTemplate;
    // Use TypeReference to properly handle the List of Records
    private final BeanOutputConverter<List<JobListing>> outputConverter;
    private final DiceJobRepository diceJobRepository;

    private final String systemPrompt = """
    You are a job search assistant. 
    
    WORKFLOW:
    1. Search for jobs using 'search_jobs' with the user's query.
    2. For the FIRST result, use 'fetchDiceId' with the 'detailsPageUrl'.
    3. Include that ID in the 'diceId' field of your response.
    
    DATA MAPPING:
    - The Dice tool returns location in 'jobLocation.displayName'.\s
    - You MUST map that value to the 'jobLocation' field in your JSON output.
    - Map 'companyName' to 'companyName'.
    - Map 'employmentType' to 'employmentType'.
    """;

    public JobSearchService(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools, DiceJobRepository diceJobRepository) {
        this.diceJobRepository = diceJobRepository;
        this.restTemplate = new RestTemplate();

        // Correct initialization using Spring's ParameterizedTypeReference
        this.outputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<JobListing>>() {});

        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .defaultToolCallbacks(mcpTools.getToolCallbacks())
                .defaultTools(this)
                .build();
    }

    public List<JobListing> searchJobs(String query) {
        List<JobListing> listings = chatClient.prompt()
                .user(query)
                .call()
                .entity(outputConverter);

        if (listings != null) {
            List<DiceJobEntity> entities = listings.stream()
                    .filter(listing -> !diceJobRepository.existsById(listing.id()))
                    .map(listing -> {
                        DiceJobEntity entity = new DiceJobEntity();
                        BeanUtils.copyProperties(listing, entity);
                        return entity;
            }).toList();

            diceJobRepository.saveAll(entities);
        }

        return listings;
    }
 
    @Tool(description = "Retrieves the 'Dice Id' from a Dice job details page URL")
    public String fetchDiceId(String url) {
        try {
            String html = restTemplate.getForObject(url, String.class);
            if (html == null) return "Could not fetch page content";
            
            // Search for "Dice Id:" label and capture value to the right
            Pattern pattern = Pattern.compile("Dice Id:\\s*<!--\\s*-->\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
            return "Dice Id not found on page";
        } catch (Exception e) {
            return "Error retrieving Dice Id: " + e.getMessage();
        }
    }
}