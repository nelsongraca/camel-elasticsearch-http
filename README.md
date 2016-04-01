# Introduction
The camel-elasticsearch component in the Apache Project (http://camel.apache.org/elasticsearch.html) is based on the Elasticsearch Java client which has different APIs and behaviour depending on whether it was for 1.x or 2.x. The Elasticsearch HTTP API is a little less prone to breaking changes at least with regards to things like index or bulk_index. This component can be used in the same way as the camel-elasticsearch component but makes use of the HTTP API with a Jersey Client. 

# Usage
Update your pom or gradle with the following dependency:





