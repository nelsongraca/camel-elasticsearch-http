package com.sarwarbhuiyan.camel.component.elasticsearch.http;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchScanScrollIterator implements
		Iterator<List> {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(ElasticsearchScanScrollIterator.class);

	private ElasticsearchHTTPClient client;
	private ElasticsearchHTTPConfiguration configuration;
	private String index;
	private String indexType;
	private int scrollSize = 10;
	private String scrollPeriod = "5m";
	private String scrollId = null;
	private String scanQuery = null;
	private Map currentResults = null;
	private List currentResultsHits = null;

	public ElasticsearchScanScrollIterator(ElasticsearchHTTPClient client,
			ElasticsearchHTTPConfiguration configuration) {
		this.client = client;
		this.configuration = configuration;
		this.index = configuration.getIndexName();
		this.indexType = configuration.getIndexType();
		if (configuration.getScrollSize() > 0)
			this.scrollSize = configuration.getScrollSize();
		if (configuration.getScrollPeriod() != null) {
			this.scrollPeriod = configuration.getScrollPeriod();
		}
		if (configuration.getScanQuery() != null) {
			this.scanQuery = configuration.getScanQuery();
		}

	}

	@Override
	public boolean hasNext() {
		if (this.scrollId == null) {
			currentResults = this.client.scan(index, indexType, scanQuery,
					scrollPeriod, scrollSize);
		} else {
			currentResults = this.client.scroll(scrollPeriod, this.scrollId);
		}
		this.scrollId = (String)currentResults.get("_scroll_id");
		Map hitsMap = (Map)currentResults.get("hits");
		currentResultsHits = (List)hitsMap.get("hits");
		if(this.scrollId!=null && currentResultsHits.size()>0) {
			this.scrollId = (String)currentResults.get("_scroll_id");
			return true;
		} else {
			LOG.info("Finished reading iterator");
		}
		return false;
	}

	@Override
	public List next() {
		return currentResultsHits;
	}

}
