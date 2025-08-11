package com.slawekle.GithubListing.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class GithubListService implements IGithubListService {
    private final ObjectMapper objectMapper;

    public GithubListService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode getGithubResponse(String userString) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.github.com/users/" + userString + "/repos";

        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        } catch (HttpClientErrorException e) {
            ObjectNode exeptionNode = objectMapper.createObjectNode();
            exeptionNode.put("status", e.getStatusCode().value());
            exeptionNode.put("message", e.getStatusCode().value() == 404 ? "User " + userString + " not found" : e.getMessage());
            throw new HttpClientErrorException(
                e.getStatusCode(),
                e.getStatusText(),
                e.getResponseHeaders(),
                exeptionNode.toString().getBytes(),
                null
            );
        }

        return parseResponse(response.getBody());
    }

    private JsonNode parseResponse(String responseBody) {
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(responseBody);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response", e);
        }

        JsonNode jsonRepositoryNames = parseJsonNode(jsonNode);

        return jsonRepositoryNames;
    }

    private JsonNode parseJsonNode(JsonNode jsonNode) {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (JsonNode node : getRepositoryListFromJsonNode(jsonNode)) {
            if (!node.get("fork").asBoolean()) {
                ObjectNode partNode = objectMapper.createObjectNode();
                partNode.put("Repository name", node.get("name").asText());
                partNode.put("Owner login", node.get("owner").get("login").asText());
                partNode.set("Branches", parseBranchesNode(node));
                arrayNode.add(partNode);
            }
        }

        return arrayNode;
    }

    private List<JsonNode> getRepositoryListFromJsonNode(JsonNode jsonNode) {
        return Optional.ofNullable(jsonNode)
                .filter(JsonNode::isArray)
                .map(node -> {
                    List<JsonNode> list = new ArrayList<>();
                    node.forEach(list::add);
                    return list;
                })
                .orElse(Collections.emptyList());
    }

    private ArrayNode parseBranchesNode(JsonNode jsonNode) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.github.com/repos/" + jsonNode.get("owner").get("login").asText() + "/"
                + jsonNode.get("name").asText() + "/branches";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        JsonNode parseJsonNode;

        try {
            parseJsonNode = objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response", e);
        }

        ArrayNode arrayNode = objectMapper.createArrayNode();

        for (JsonNode node : getBranchesListFromJsonNode(parseJsonNode)) {
            if (true) {
                ObjectNode partNode = objectMapper.createObjectNode();
                partNode.put("Branch name", node.get("name").asText());
                partNode.put("Last commit sha", node.get("commit").get("sha").asText());

                arrayNode.add(partNode);
            }
        }

        return arrayNode;
    }

    private List<JsonNode> getBranchesListFromJsonNode(JsonNode jsonNode) {
        return Optional.ofNullable(jsonNode)
                .filter(JsonNode::isArray)
                .map(node -> {
                    List<JsonNode> list = new ArrayList<>();
                    node.forEach(list::add);
                    return list;
                })
                .orElse(Collections.emptyList());
    }

}
