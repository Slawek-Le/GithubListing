# Github Listing

A backend API for listing Github user’s public repositories, selected by user name.
Lists also repositories branches and last commit sha. Handles 404 error when user is not found.
Spring 3.5 and Java 21

## Example response:

```JSON
[{
        "Repository name": "Test",
        "Owner login": "Slawek-Le",
        "Branches":
        [
            {
                "Branch name": "main",
                "Last commit sha": "bf66add64643c62efe82a52c6a062b16c39d491e"
            },
            {
                "Branch name": "second_branch",
                "Last commit sha": "d7ecd128ba003d1df83afa1455eb3e790bdec732"
            }
        ]
 }]
```

## Endpoint:

Sever standard port 8080

“Server:8080” **/githublist/user/{userString}**

## Integration test

**\src\test\java\com\slawekle\GithubListing\GithubListControllerIntegrationTest.java**
