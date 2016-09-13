#NHS Crawler

Crawls NHS syndication website, loading conditions into embedded Elasticsearch. Provides a REST service to
query data and get URLs to relevant NHS pages

##Instructions to run:
1. mvn clean package
2. java -jar target/nhs-conditions-service-1.0.0.jar

You can access REST like this:
http://localhost:8080/nhs/conditions/search?q=What about headache?

Crawler will start up automatically. Currently crawler log is in DEBUG mode to show visited/saved pages.
Debug log can be suppressed in main/resources/logback.xml file.

##Configuration

Application properties can be found in the file application.yml:

crawler:  - crawler-related properties
  apiKey:  - Nhs API key 
  agent:   - browser agent string for crawler
  numOfThreads: - how many threads to use for crawling
  crawlDelay: - delay in millis between crawling requests
  workFolder: - work folder for crawler
  baseUrl: - crawler will handle only URLs which start with this value
  startUrl: - crawler will start from this url, it is A to Z conditions catalog
  enabled: true - crawler can be enabled/disabled

elasticsearch: - ES related properties
  dbPath: - path to store ES db
  nodeName: - ES node name
  indexName: - ES index name
  typeName: - ES type name to store Nhs pages

