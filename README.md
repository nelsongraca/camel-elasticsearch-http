# Introduction
The camel-elasticsearch component in the Apache Project (http://camel.apache.org/elasticsearch.html) is based on the Elasticsearch Java client which has different APIs and behaviour depending on whether it was for 1.x or 2.x. The Elasticsearch HTTP API is a little less prone to breaking changes at least with regards to things like index or bulk_index. This component can be used in the same way as the camel-elasticsearch component but makes use of the HTTP API with a Jersey Client. 

# Build
```shell
>mvn clean install
```

# Usage
Update your pom or gradle with the following dependency:

```xml
<groupId>com.sarwarbhuiyan</groupId>
<artifactId>camel-elasticsearch-http</artifactId>
<version>0.1.0</version>
```

In your route, you can use the following URI format:

```
eshttp://clusterName&ip=<ip address>&port=9200&indexName=<index name>&indexType=<type name>&operation=<operation name>
```

Where operation name can take the values:

* INDEX
* GET_BY_ID
* DELETE
* BULK_INDEX
* SEARCH
* MULTIGET
* MULTISEARCH
* EXISTS
* UPDATE
* SCAN_SCROLL (as a consumer endpoint. If this is used as a consumer endpoint and then used to reindex/bulk reindex using an output/producer endpoint, one can use the preserveIds=true parameter to preserve IDs from the source index)

Other parameters supported are:

* consistencyLevel (can take values ONE, QUORUM, or DEFAULT)

The objects passed to the endpoint can either be JSON strings or Map of Maps representing the JSON to be passed in.

For the most part, this is in keeping with the operations on camel-elasticsearch but not supporting the classes/objects in the Java Elasticsearch Client API.

Connection Pooling and Long-lived HTTP connections are used under the hood by virtue of using Apache HttpClient

Examples:

The following show pipeline builders that can be included in a Camel application:

1. Reindex from existing Elasticseach to new Elasticsearch index/cluster

```
from("eshttp://elasticsearch?ip=localhost&port=3000&operation=SCAN_SCROLL&indexName=my-index&scrollSize=10")
    		.aggregate(constant(true), new BulkReindexStrategy())
    		.completionSize(1000)     // set bulk size here
    		.forceCompletionOnStop()
    		.completionTimeout(1000)  // set completion timeout here
    		.to("eshttp://elasticsearch?ip=localhost&port=3000&operation=BULK_INDEX&indexName=my-index2&preserveIds=true");
```

2. Read from twitter and index into Elasticsearch
```
        from("twitter://streaming/sample?type=EVENT&consumerKey=<consumer-key>&consumerSecret=<consumer-secret>&accessToken=<access-token>&accessTokenSecret=<access-token-secret>&delay=1")
        .to("jms:test.TwitterQueue");
        
        from("jms:test.TwitterQueue")
        .marshal().json(JsonLibrary.Jackson)
        .aggregate(constant(true), new BulkIndexStrategy())
		.completionSize(1000)
		.forceCompletionOnStop()
        .to("eshttp://elasticsearch?ip=localhost&port=9300&operation=BULK_INDEX&indexName=twitter&indexType=tweet");
```

# Future Work

* Load balancing across multiple data nodes
* Sniffing of all data nodes from network via calls to the cluster




