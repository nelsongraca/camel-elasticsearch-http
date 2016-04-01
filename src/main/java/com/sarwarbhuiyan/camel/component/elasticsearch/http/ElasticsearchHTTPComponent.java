package com.sarwarbhuiyan.camel.component.elasticsearch.http;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;


/**
 * Represents the component that manages {@link ElasticsearchEndpoint}.
 */
public class ElasticsearchHTTPComponent extends UriEndpointComponent {

        public ElasticsearchHTTPComponent() {
        super(ElasticsearchHTTPEndpoint.class);
    }

    public ElasticsearchHTTPComponent(CamelContext context) {
        super(context, ElasticsearchHTTPEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        ElasticsearchHTTPConfiguration config = new ElasticsearchHTTPConfiguration();
        setProperties(config, parameters);
        Endpoint endpoint = new ElasticsearchHTTPEndpoint(uri, this, config);
        return endpoint;
    }

}
