package com.slawekle.GithubListing.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface IGithubListService {
    JsonNode getGithubResponse(String userString);
}
