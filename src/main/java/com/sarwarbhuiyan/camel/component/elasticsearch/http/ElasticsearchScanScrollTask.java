package com.sarwarbhuiyan.camel.component.elasticsearch.http;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ElasticsearchScanScrollTask implements Runnable {

	private static final Logger LOG = LoggerFactory
			.getLogger(ElasticsearchScanScrollTask.class);

	private final ElasticsearchHTTPEndpoint endpoint;

	private final ElasticsearchHTTPConfiguration configuration;

	private final Processor processor;

	private final ElasticsearchHTTPConsumer consumer;

	private final ElasticsearchScanScrollIterator iterator;

	public ElasticsearchScanScrollTask(
			final ElasticsearchHTTPEndpoint endpoint,
			final ElasticsearchHTTPConsumer consumer,
			final Processor processor,
			final ElasticsearchHTTPConfiguration configuration) {
		this.endpoint = endpoint;
		this.consumer = consumer;
		this.processor = processor;
		this.configuration = configuration;
		this.iterator = this.endpoint.scanScroll();
	}

	@Override
	public void run() {

		while(iterator.hasNext()) {
			List list = iterator.next();
			final Message message = new DefaultMessage();
			ObjectMapper objectMapper = new ObjectMapper();
			for(Object o: list) {
				try {
					Map objectMap = (Map)o;
					if(objectMap.containsKey("_source")) {
						Map sourceMap = (Map)objectMap.get("_source");
						String id = (String)objectMap.get("_id");
						String type = (String)objectMap.get("_type");
						String indexName = (String)objectMap.get("_index");
						message.setHeader(ElasticsearchConstants.PARAM_INDEX_ID, id);
						message.setHeader(ElasticsearchConstants.PARAM_INDEX_TYPE, type);
						message.setHeader(ElasticsearchConstants.PARAM_INDEX_NAME, indexName);

						message.setBody(objectMapper.writeValueAsString(sourceMap));
						Exchange exchange = new DefaultExchange(endpoint.getCamelContext(), endpoint.getExchangePattern());
						exchange.setIn(message);
						try {
							this.processor.process(exchange);
						} catch (Exception e) {
							LOG.error("Error processing scan scroll list", e);
							consumer.getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());

						} finally {
						}
					}

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}


		}

		try {
			LOG.info("!!!!!!  Will call shutdown now !!!!!!");
			endpoint.getCamelContext().stop();
//			endpoint.getCamelContext().stopRoute("fromRoute");
//			endpoint.shutdown();
		} catch (Exception e) {
			LOG.error("Error shutting down endpoint", e);
		}

	}


}
