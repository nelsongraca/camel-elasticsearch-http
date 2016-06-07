package com.sarwarbhuiyan.camel.component.elasticsearch.http;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The elasticsearch http component is used for interfacing with ElasticSearch
 * server.
 */
@UriEndpoint(scheme = "eshttp", title = "Elasticsearch HTTP", syntax = "eshttp:options", producerOnly = false, label = "monitoring,search")
public class ElasticsearchHTTPEndpoint extends DefaultEndpoint {

	private static final Logger LOG = LoggerFactory
			.getLogger(ElasticsearchHTTPEndpoint.class);

	private ElasticsearchHTTPClient esHttpClient;

	@UriParam
	private ElasticsearchHTTPConfiguration configuration;

	private ObjectMapper objectMapper = new ObjectMapper();

	public ElasticsearchHTTPEndpoint(String uri,
			ElasticsearchHTTPComponent component,
			ElasticsearchHTTPConfiguration config) throws Exception {
		super(uri, component);
		this.configuration = config;

	}

	public Producer createProducer() throws Exception {
		return new ElasticsearchHTTPProducer(this);
	}

	public Consumer createConsumer(Processor processor) throws Exception {
		return new ElasticsearchHTTPConsumer(this, processor,
				this.configuration);
	}

	// public Consumer createConsumer(Processor processor) throws Exception {
	// throw new UnsupportedOperationException(
	// "Cannot consume from an ElasticsearchEndpoint: "
	// + getEndpointUri());
	// }

	public boolean isSingleton() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doStart() throws Exception {
		super.doStart();
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

	/**
	 * Delegates to the http client to index the message
	 */
	public String index(Message message) {
		if (!configuration.isPreserveIds()) {
			return esHttpClient.index(getIndexName(message),
					getIndexType(message), message.getBody(String.class));
		} else {
			String id = (String) message
					.getHeader(ElasticsearchConstants.PARAM_INDEX_ID);
			if (id != null) {
				return esHttpClient.indexWithId(getIndexName(message),
						getIndexType(message), id,
						message.getBody(String.class));
			}

		}
		return null;
	}

	/**
	 * Delegates to the http client to update the message
	 */
	public Object update(Message message) {
		String id = message.getExchange().getIn()
				.getHeader(ElasticsearchConstants.PARAM_INDEX_ID, String.class);
		return esHttpClient.update(getIndexName(message),
				getIndexType(message), id, message.getBody(Map.class));

	}

	/**
	 * Delegates to the http client to call multiget using the message object's
	 * contents
	 */
	public Object multiget(Message message) {
		String indexName = getIndexName(message);
		String indexType = getIndexType(message);
		return esHttpClient.multiget(indexName, indexType,
				message.getBody(List.class));
	}

	/**
	 * Not support operation
	 */
	public Object bulk(Message message) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Delegates to the http client to call bulk index using the message
	 * object's contents
	 */
	public List<String> bulkIndex(Message message) {
		// if reindexing, we expect a map of objects to come back with the key being the id
		if (!configuration.isPreserveIds()) {
			List<Object> objects = message.getBody(List.class);
			if(objects!=null) {
				List<String> documents = new ArrayList<String>(objects.size());
				for (Object obj : objects) {
					documents.add((String) obj);
				}
				return esHttpClient.bulkIndex(getIndexName(message),
						getIndexType(message), documents);
			}

		} else {
			
			Map<String, Object> objectsMap = message.getBody(Map.class);
			Map<String, String> documents = new HashMap<String, String>(
					objectsMap.size());
			for (Map.Entry<String, Object> entry : objectsMap.entrySet()) {
				documents.put(entry.getKey(), (String) entry.getValue());
			}
			return esHttpClient.bulkReIndex(getIndexName(message),
					getIndexType(message), documents);
		}
		return null;

	}

	/**
	 * Delegates to the http client the delete operation on the document
	 * represented by the message object
	 */
	public Object delete(Message message) {
		return esHttpClient.delete(getIndexName(message),
				getIndexType(message), message.getBody(String.class));
	}

	/**
	 * Delegates to the http client the index_exists operation on the index
	 * referred to by the message object
	 */
	public boolean indexExists(Message message) {
		return esHttpClient.indexExists(getIndexName(message));
	}

	/**
	 * Delegates to the http client the search query represented by the JSON or
	 * Map of maps in the message object
	 */
	public Object search(Message message) {
		Map queryObject = message.getBody(Map.class);
		return esHttpClient.search(getIndexName(message),
				getIndexType(message), queryObject);
	}

	/**
	 * Delegates to the http client the get_by_id operation of the id contained
	 * in the message object
	 */
	public Object getById(Message message) {
		return esHttpClient.getById(getIndexName(message),
				getIndexType(message), message.getBody(String.class));

	}

	/**
	 * Delegates to the http client the multisearch object of the queries
	 * contained in the mesasge object
	 */
	public Object multisearch(Message message) {
		List queryObjects = message.getBody(List.class);
		return esHttpClient.multisearch(getIndexName(message),
				getIndexType(message), queryObjects);
	}

	public ElasticsearchScanScrollIterator scanScroll() {
		return new ElasticsearchScanScrollIterator(esHttpClient, configuration);
	}

	public Map scan(String indexName, String indexType, String scanQuery) {
		String scrollPeriod = configuration.getScrollPeriod() != null ? configuration
				.getScrollPeriod() : "1m";
		int scrollSize = configuration.getScrollSize();
		return esHttpClient.scan(indexName, indexType, scanQuery, scrollPeriod,
				scrollSize);
	}

}
