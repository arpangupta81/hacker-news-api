HACKER-NEWS DETAILS - BACKEND

This project contains implementation of three API's with the help of Spring Caching which will evict the caches every 'n' minutes.

1. /top-stories: This API fetches top 'n' stories on the basis of the score of the stories.

**SAMPLE RESULT:**
`[
    {
		"id": 24245817,
		"author": "ra7",
		"score": 1240,
		"timeOfSubmission": "2020-08-22T17:51:36Z",
		"storyTitle": "Degoogle: Cutting Google out of your life",
		"url": "https://degoogle.jmoore.dev/"
	},
	{
		"id": 24235908,
		"author": "gamblor956",
		"score": 821,
		"timeOfSubmission": "2020-08-21T15:54:56Z",
		"storyTitle": "Telegram messaging app proves crucial to Belarus protests",
		"url": "https://www.latimes.com/world-nation/story/2020-08-21/telegram-messaging-app-crucial-belarus-protests"
	}
]`

2. /comments: This API fetches top 'n' comments for a particular story and the results are sorted on the basis of the number of children for a particular comment.

**SAMPLE INPUT**

`/comments?storyId=8863`

**SAMPLE RESULT:**
`[
    {
		"authorId": "jganetsk",
		"authorActiveTime": 13,
		"commentText": "How are you going to scale up your storage to meet the demands of the users? Are you doing something clever, like Google Filesystem? This is not an easy problem, if you aren't prepared for it in advance. If 10,000 users sign up tomorrow... you might be very very hosed, as opposed to very very happy."
	},
	{
		"authorId": "nefele",
		"authorActiveTime": 13,
		"commentText": "Drew,<p>I saw your short demo at BarCamp and I must say Dropbox looks great! Are you planning on having a Linux port as well, or is too early to talk about that?<p>Also, as another SFP applicant I have to tell you that I really hope you get the funding - you deserve it.\n"
	}
]
`
3. /past-stories: This API gives all the results which were served in the past for the top stories.

**SAMPLE RESULT:**
`[
    {
		"id": 24245817,
		"author": "ra7",
		"score": 1240,
		"timeOfSubmission": "2020-08-22T17:51:36Z",
		"storyTitle": "Degoogle: Cutting Google out of your life",
		"url": "https://degoogle.jmoore.dev/"
	},
	{
		"id": 24235908,
		"author": "gamblor956",
		"score": 821,
		"timeOfSubmission": "2020-08-21T15:54:56Z",
		"storyTitle": "Telegram messaging app proves crucial to Belarus protests",
		"url": "https://www.latimes.com/world-nation/story/2020-08-21/telegram-messaging-app-crucial-belarus-protests"
	}
]`

'n' is configurable and can be changed while running the application.

The code structure is made keeping in mind the fast performance and least resources available.

Some Major Improvements which can be done:

1. Using Cassanda/NoSQL databases.
2. Using Elasticache/Redis for caching the data.