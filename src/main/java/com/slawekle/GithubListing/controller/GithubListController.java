package com.slawekle.GithubListing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.slawekle.GithubListing.service.IGithubListService;

@RestController
@RequestMapping("/githublist")
public class GithubListController {

    private final IGithubListService githubListService;
    private final ObjectMapper objectMapper;

    public GithubListController(IGithubListService githubListService) {
        this.githubListService = githubListService;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/user/{userString}")
    public ResponseEntity<JsonNode> getGithubApiResponse(@PathVariable String userString) {
        try {
            JsonNode response = githubListService.getGithubResponse(userString);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException e) {
            JsonNode errorNode = createErrorResponse(e);           
            return ResponseEntity.status(e.getStatusCode()).body(errorNode);
        }
    }

    private JsonNode createErrorResponse(HttpClientErrorException e) {
        try {
            JsonNode errorNode = objectMapper.readTree(e.getResponseBodyAsString());
            return errorNode;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse error message", ex);
        }
    }

}
