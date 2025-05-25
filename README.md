# RestAPI_Complete

## News Endpoint

Provides access to news articles fetched from the GNews service.

**Path:** `GET /news`

**Description:** Fetches news articles based on a query and other optional criteria.

**Query Parameters:**

*   `q` (String, **Required**): The search query. You can use GNews advanced syntax like:
    *   `"Exact Phrase"`
    *   `Keyword1 AND Keyword2`
    *   `Keyword1 OR Keyword2`
    *   `Keyword NOT KeywordToExclude`
    *   `(Complex AND Query) OR Other`
*   `lang` (String, Optional): The language of the news articles (e.g., `en`, `de`, `fr`). Defaults to any if not specified. Refer to GNews documentation for a full list of supported languages.
*   `country` (String, Optional): The country where the news articles were published (e.g., `us`, `gb`, `ca`). Defaults to any if not specified. Refer to GNews documentation for a full list of supported countries.
*   `from` (String, Optional): The start date and time to search for articles from. Format: `YYYY-MM-DDTHH:mm:ssZ` (e.g., `2023-10-01T00:00:00Z`).
*   `to` (String, Optional): The end date and time to search for articles up to. Format: `YYYY-MM-DDTHH:mm:ssZ` (e.g., `2023-10-31T23:59:59Z`).
*   `pageSize` (Integer, Optional, Default: `10`): The number of articles to return. GNews free tier has a maximum, typically 10.
*   `page` (Integer, Optional, Default: `1`): The page number for pagination. Note: The GNews API's 'page' parameter might be a paid feature; the free tier might not support pagination beyond a certain limit or depth.
*   `sort` (String, Optional, Default: `publishedAt`): How to sort the articles. Possible values:
    *   `publishedAt`: Sort by publication date (newest first).
    *   `relevance`: Sort by best match to the query.
*   `in` (String, Optional): Which attributes of the article to search in. Can be a comma-separated list (e.g., `title`, `description`, `content`, or `title,description`). If not specified, GNews defaults usually cover title and description.

**Example Request:**

`GET /mobile-app-ws/news?q=Agentic%20AI&lang=en&pageSize=5&sort=relevance`
(Note: `/mobile-app-ws` might be part of your application's context path, adjust if necessary based on deployment.)

**Response:**

A JSON array of news articles. Each article object contains:
*   `title` (String): The headline of the article.
*   `description` (String): A short snippet or description of the article.
*   `url` (String): A link to the full article.

**Important Configuration:**

This endpoint relies on the GNews API. A valid GNews API key must be configured in the `application.properties` file under the key `gnews.api.key`. You can obtain a free API key from [https://gnews.io/](https://gnews.io/).