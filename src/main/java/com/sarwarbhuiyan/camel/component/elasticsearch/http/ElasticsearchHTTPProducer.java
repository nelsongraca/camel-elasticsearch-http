package com.sarwarbhuiyan.camel.component.elasticsearch.http;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;

/**
 * Represents an Elasticsearch producer.
 */
public class ElasticsearchHTTPProducer extends DefaultProducer {

	public ElasticsearchHTTPProducer(ElasticsearchHTTPEndpoint endpoint) {
		super(endpoint);
	}

	@Override
	public ElasticsearchHTTPEndpoint getEndpoint() {
		return (ElasticsearchHTTPEndpoint) super.getEndpoint();
	}

	private String resolveOperation(Exchange exchange) {
		Object request = exchange.getIn().getBody();
		

		String operationConfig = exchange.getIn().getHeader(
				ElasticsearchConstants.PARAM_OPERATION, String.class);
		if (operationConfig == null) {
			operationConfig = getEndpoint().getConfig().getOperation();
		}
		if (operationConfig == null) {
			throw new IllegalArgumentException(
					ElasticsearchConstants.PARAM_OPERATION + " value '"
							+ operationConfig + "' is not supported");
		}
		return operationConfig;
	}

	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		final String operation = resolveOperation(exchange);

		// Set the index/type headers on the exchange if necessary. This is used
		// for type conversion.
		boolean configIndexName = false;
		String indexName = getEndpoint().getConfig().getIndexName();
		if (indexName != null) {
			message.setHeader(ElasticsearchConstants.PARAM_INDEX_NAME,
					getEndpoint().getConfig().getIndexName());
			configIndexName = true;
		}

		boolean configIndexType = false;
		String indexType = getEndpoint().getConfig().getIndexType();
		if (indexType != null) {
			message.setHeader(ElasticsearchConstants.PARAM_INDEX_TYPE,
					getEndpoint().getConfig().getIndexType());
			configIndexType = true;
		}

		boolean configConsistencyLevel = false;
		String consistencyLevel = message.getHeader(
				ElasticsearchConstants.PARAM_CONSISTENCY_LEVEL, String.class);
		if (consistencyLevel == null) {
			message.setHeader(ElasticsearchConstants.PARAM_CONSISTENCY_LEVEL,
					getEndpoint().getConfig().getConsistencyLevel());
			configConsistencyLevel = true;
		}

		ElasticsearchHTTPEndpoint endpoint = getEndpoint();
		if (ElasticsearchConstants.OPERATION_INDEX.equals(operation)) {
			message.setBody(endpoint.index(message));
		} else if (ElasticsearchConstants.OPERATION_UPDATE.equals(operation)) {
			message.setBody(endpoint.update(message));
		} else if (ElasticsearchConstants.OPERATION_GET_BY_ID.equals(operation)) {
			message.setBody(endpoint.getById(message));
		} else if (ElasticsearchConstants.OPERATION_MULTIGET.equals(operation)) {
			message.setBody(endpoint.multiget(message));
		} else if (ElasticsearchConstants.OPERATION_BULK.equals(operation)) {
			message.setBody(endpoint.bulk(message));
		} else if (ElasticsearchConstants.OPERATION_BULK_INDEX
				.equals(operation)) {
			message.setBody(endpoint.bulkIndex(message));
		} else if (ElasticsearchConstants.OPERATION_DELETE.equals(operation)) {
			message.setBody(endpoint.delete(message));
		} else if (ElasticsearchConstants.OPERATION_EXISTS.equals(operation)) {
			message.setBody(endpoint.indexExists(message));
		} else if (ElasticsearchConstants.OPERATION_SEARCH.equals(operation)) {
			message.setBody(endpoint.search(message));
		} else if (ElasticsearchConstants.OPERATION_MULTISEARCH
				.equals(operation)) {
			message.setBody(endpoint.multisearch(message));
		} else {
			throw new IllegalArgumentException(
					ElasticsearchConstants.PARAM_OPERATION + " value '"
							+ operation + "' is not supported");
		}

		if (configIndexName) {
			message.removeHeader(ElasticsearchConstants.PARAM_INDEX_NAME);
		}

		if (configIndexType) {
			message.removeHeader(ElasticsearchConstants.PARAM_INDEX_TYPE);
		}

		if (configConsistencyLevel) {
			message.removeHeader(ElasticsearchConstants.PARAM_CONSISTENCY_LEVEL);
		}

	}
}
