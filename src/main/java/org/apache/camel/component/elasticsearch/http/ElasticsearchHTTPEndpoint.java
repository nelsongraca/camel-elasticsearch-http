/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.elasticsearch.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The elasticsearch http component is used for interfacing with ElasticSearch
 * server.
 */
@UriEndpoint(scheme = "eshttp", title = "Elasticsearch HTTP", syntax = "eshttp:options", producerOnly = true, label = "monitoring,search")
public class ElasticsearchHTTPEndpoint extends DefaultEndpoint {

	private static final Logger LOG = LoggerFactory
			.getLogger(ElasticsearchHTTPEndpoint.class);

	private ElasticsearchHTTPClient esHttpClient;
	@UriParam
	private ElasticsearchHTTPConfiguration configuration;

	public ElasticsearchHTTPEndpoint(String uri, ElasticsearchHTTPComponent component,
			ElasticsearchHTTPConfiguration config) throws Exception {
		super(uri, component);
		this.configuration = config;
		
	}

	public Producer createProducer() throws Exception {
		return new ElasticsearchHTTPProducer(this);
	}

	public Consumer createConsumer(Processor processor) throws Exception {
		throw new UnsupportedOperationException(
				"Cannot consume from an ElasticsearchEndpoint: "
						+ getEndpointUri());
	}

	public boolean isSingleton() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doStart() throws Exception {
		super.doStart();

		LOG.info("Using HTTP Client ");
		esHttpClient = new ElasticsearchHTTPClient();
		esHttpClient.setHost(configuration.getIp());
		esHttpClient.setPort(String.valueOf(configuration.getPort()));
		return;
	}

	@Override
	protected void doStop() throws Exception {
		
		super.doStop();
	}

	public ElasticsearchHTTPConfiguration getConfig() {
		return configuration;
	}

	public void setOperation(String operation) {
		configuration.setOperation(operation);
	}

	private String getIndexName(Message message) {
		return message.getHeader(ElasticsearchConstants.PARAM_INDEX_NAME,
				String.class);
	}

	private String getIndexType(Message message) {
		return message.getHeader(ElasticsearchConstants.PARAM_INDEX_TYPE,
				String.class);
	}

	public String index(Message message) {
		LOG.info("Indexing into Elasticsearch using HTTP Client ");
		return esHttpClient.index(getIndexName(message), getIndexType(message),
					message.getBody(String.class));
		
	}

	public Object update(Message message) {
		String id = message
				.getExchange()
				.getIn()
				.getHeader(ElasticsearchConstants.PARAM_INDEX_ID,
						String.class);
		return esHttpClient.update(getIndexName(message),
				getIndexType(message), id, message.getBody(Map.class));

	}

	public Object multiget(Message message) {
		String indexName = getIndexName(message);
		String indexType = getIndexType(message);
		return esHttpClient.multiget(indexName, indexType,
				message.getBody(List.class));
	}

	public Object bulk(Message message) {
		throw new UnsupportedOperationException();
	}

	public List<String> bulkIndex(Message message) {
		List<Object> objects = message.getBody(List.class);
		List<String> documents = new ArrayList<String>(objects.size());
		for (Object obj : objects) {
			documents.add((String) obj);
		}
		return esHttpClient.bulkIndex(getIndexName(message),
				getIndexType(message), documents);
	}

	public Object delete(Message message) {
		return esHttpClient.delete(getIndexName(message),
					getIndexType(message), message.getBody(String.class));
	}

	public boolean indexExists(Message message) {
		return esHttpClient.indexExists(getIndexName(message));
	}

	public Object search(Message message) {
		Map queryObject = message.getBody(Map.class);
		return esHttpClient.search(getIndexName(message), getIndexType(message), queryObject);
	}

	public Object getById(Message message) {
		return esHttpClient.getById(getIndexName(message),
					getIndexType(message), message.getBody(String.class));
	
	}

	public Object multisearch(Message message) {
		List queryObjects = message.getBody(List.class);
		return esHttpClient.multisearch(getIndexName(message), getIndexType(message), queryObjects);			
	}

}