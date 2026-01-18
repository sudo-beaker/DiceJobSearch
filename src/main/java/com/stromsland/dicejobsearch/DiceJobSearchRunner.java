package com.stromsland.dicejobsearch;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DiceJobSearchRunner implements CommandLineRunner {

    private final ChatClient chatClient;
    private final ToolCallbackProvider mcpTools;

    public DiceJobSearchRunner(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools) {
        // Configure a chat client builder instance with the available tools
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(mcpTools.getToolCallbacks())
                .build();
        this.mcpTools = mcpTools;
    }

    @Override
    public void run(String... args) throws Exception {
        String userRequest = "Find senior remote Java developer jobs on Dice that don't require Spring Boot";
        String response = chatClient.prompt()
                .user(userRequest)
                .call()
                .content();
        System.out.println(response);
    }
}