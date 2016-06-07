package com.sarwarbhuiyan.camel.component.elasticsearch.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class BulkIndexStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		try {
			Object newBody = newExchange.getIn().getBody();

	        List<Object> list = null;
	        if (oldExchange == null) {
	            list = new ArrayList<Object>();
	            list.add(newBody);
	            newExchange.getIn().setBody(list);
	            return newExchange;
	        } else {
	            list = oldExchange.getIn().getBody(ArrayList.class);
	            list.add(newBody);
	            return oldExchange;
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
