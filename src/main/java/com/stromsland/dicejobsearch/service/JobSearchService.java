package com.stromsland.dicejobsearch.service;
 
 
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
@Service
public class JobSearchService {
 
    private final ChatClient chatClient;
    private final RestTemplate restTemplate;
 
    private final String systemPrompt = """
            You are a job search assistant. When using the 'search_jobs' tool, if you specify the 'fields' parameter, you MUST only use the following allowed values:
            'id', 'jobId', 'guid', 'summary', 'title', 'postedDate', 'modifiedDate', 'jobLocation.displayName', 'detailsPageUrl', 'salary', 'clientBrandId', 'companyPageUrl', 'companyLogoUrl', 'companyLogoUrlOptimized', 'positionId', 'companyName', 'employmentType', 'isHighlighted', 'score', 'easyApply', 'employerType', 'workFromHomeAvailability', 'workplaceTypes', 'isRemote', 'debug', 'jobMetadata', 'willingToSponsor'.
            Do NOT use 'location' in the 'fields' parameter; use 'jobLocation.displayName' instead if you need location information in the fields.
            
            When providing job results:
            1. Capture the response and the URL (detailsPageUrl) for the 1st listing.
            2. Use the 'fetchDiceId' tool with that URL to retrieve the 'Dice Id'.
            3. Include the 'Dice Id' in your response for that listing.
            """;
 
    public JobSearchService(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools) {
        this.restTemplate = new RestTemplate();
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .defaultToolCallbacks(mcpTools.getToolCallbacks())
                .defaultTools(this)
                .build();
    }
 
    public String searchJobs(String query) {
        return chatClient.prompt()
                .user(query)
                .call()
                .content();
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