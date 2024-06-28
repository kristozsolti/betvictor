# Lorem Ipsum API

The **Lorem Ipsum API** provided by loripsum.net offers a RESTful interface for generating dummy text data. In this project, we'll create a small and simple system consisting of two applications:

## 1. Processing Application

The processing application is a Java server that exposes a REST API with the following description:

- **Endpoint URL**: `/betvictor/text`
- **Accepted Parameters**:
    - `p`: Indicates the maximum number of paragraphs (a positive number greater than 0)
    - `l`: Indicates the length of each paragraph (one of: `short`, `medium`, `long`, `verylong`)
- **Method**: HTTP GET
- **Response (JSON)**:
  ```json
  {
    "freq_word": "<most_frequent_word>",
    "avg_paragraph_size": <avg_paragraph_size>,
    "avg_paragraph_processing_time": <avg_paragraph_processing_time>,
    "total_processing_time": <total_processing_time>
  }

where:
- <most_frequent_word>- the word that was the most frequent in the paragraphs
- <avg_paragraph_size> - the average size of a paragraph
- <avg_paragraph_processing_time> - the average time spent analyzing a paragraph
- <total_processing_time> - total processing time to generate the final response

The application will make requests to the Lorem Ipsum API to generate dummy text based on the betvictor endpoint request parameters. For example:

HTTP GET /betvictor/text?p=3&l=short will generate dummy text containing 1 to 3 paragraphs of “short” length.

The processing application must process all responses, compute the most frequent word found, calculate the average paragraph size, average paragraph processing time, and total processing time. The results of each computation should be sent to a Kafka topic named words.processed in the same format as an HTTP response (i.e., every request to HTTP GET /betvictor/text should return an HTTP response and produce a Kafka message).

### Additional requirements:

- Kafka-produced messages with the same <most_frequent_word> must be available for consumption in the same order they were sent.
- Assume that the Kafka topic has 4 partitions.
- The application should be configurable to allow Kafka producing to any Kafka broker.

## 2. Repository Application
   The repository application consumes messages from the Kafka topic words.processed. These consumed messages should be stored in a data source. The repository application exposes an HTTP endpoint:

HTTP GET /betvictor/history: Displays the last 10 computation results from the database.

### Additional requirements:

- The application should be configurable to allow Kafka consuming from any Kafka broker.
- The number of concurrent Kafka consumers can be configured (hint: use separated threads, not application instance scaling).
