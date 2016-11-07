package com.sarwarbhuiyan.camel.component.elasticsearch.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class BulkReindexStrategy implements AggregationStrategy {

	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

		try {
			String id = (String) newExchange.getIn().getHeader(ElasticsearchConstants.PARAM_INDEX_ID);
			Object newBody = newExchange.getIn().getBody();

			Map<String, Object> map = null;
			if (oldExchange == null) {
				map = new HashMap<String, Object>();
				map.put(id, newBody);
				newExchange.getIn().setBody(map);
				return newExchange;
			} else {
				map = oldExchange.getIn().getBody(Map.class);
				map.put(id, newBody);
				return oldExchange;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

}
