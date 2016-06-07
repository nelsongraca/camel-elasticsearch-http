package com.sarwarbhuiyan.camel.component.elasticsearch.http;

import java.util.concurrent.ExecutorService;

import org.apache.camel.AsyncProcessor;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.util.AsyncProcessorConverterHelper;

public class ElasticsearchHTTPConsumer extends DefaultConsumer {

	private ElasticsearchHTTPEndpoint endpoint;
	private ElasticsearchHTTPConfiguration configuration;
	private AsyncProcessor processor;
	private ExecutorService executor;

	public ElasticsearchHTTPConsumer(final ElasticsearchHTTPEndpoint endpoint,
			final Processor processor,
			final ElasticsearchHTTPConfiguration configuration) {
		super(endpoint, processor);
		this.endpoint = endpoint;
		this.configuration = configuration;
		this.processor = AsyncProcessorConverterHelper.convert(processor);

		// validate configuration
		checkConsumerConfiguration(configuration);

	}

	protected static void checkConsumerConfiguration(
			final ElasticsearchHTTPConfiguration configuration) {
		// TODO check that only consumer endpoint related stuff here
		
	}

	protected static ExecutorService getExecutorService(
			final ElasticsearchHTTPEndpoint endpoint,
			final ElasticsearchHTTPConfiguration configuration) {

		return endpoint
				.getCamelContext()
				.getExecutorServiceManager()
				.newFixedThreadPool(endpoint,
						"ESScanScroll[" + configuration.getIndexName() + "]",
						configuration.getConcurrentConsumers());
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		if(configuration.getOperation().equals("SCAN_SCROLL")) {
			this.executor = getExecutorService(endpoint, configuration);

			this.executor.submit(new ElasticsearchScanScrollTask(endpoint, this,
					processor, configuration));
		} else {
			log.warn("No operation specified, thus exiting");
			endpoint.shutdown();
		}

	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		//TODO send end scan/scroll message to ElasticsearchScanScrollTasks and clear scrolls
		
		//shutdown the executor
		if (executor != null) {
			if (endpoint != null && getEndpoint().getCamelContext() != null) {
				endpoint.getCamelContext().getExecutorServiceManager()
						.shutdownNow(executor);
			} else {
				executor.shutdownNow();
			}
		}
	}

	@Override
	public Endpoint getEndpoint() {
		return endpoint;
	}

}
