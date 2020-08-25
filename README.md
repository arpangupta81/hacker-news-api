HACKER-NEWS DETAILS - BACKEND

This project contains implementation of three API's with the help of Spring Caching which will evict the caches every 'n' minutes.

1. /top-stories: This API fetches top 'n' stories on the basis of the score of the stories.
2. /comments: This API fetches top 'n' comments for a particular story and the results are sorted on the basis of the number of children for a particular comment.
3. /past-stories: This API gives all the results which were served in the past for the top stories.

'n' is configurable and can be changed while running the application.

The code structure is made keeping in mind the fast performance and least resources available.

Some Major Improvements which can be done:

1. Using Cassanda/NoSQL databases.
2. Using Elasticache/Redis for caching the data.