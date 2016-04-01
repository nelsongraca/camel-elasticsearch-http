package com.sarwarbhuiyan.camel.component.elasticsearch.http;


public interface ElasticsearchConstants {

    String PARAM_OPERATION = "operation";
    String OPERATION_INDEX = "INDEX";
    String OPERATION_UPDATE = "UPDATE";
    String OPERATION_BULK = "BULK";
    String OPERATION_BULK_INDEX = "BULK_INDEX";
    String OPERATION_GET_BY_ID = "GET_BY_ID";
    String OPERATION_MULTIGET = "MULTIGET";
    String OPERATION_DELETE = "DELETE";
    String OPERATION_SEARCH = "SEARCH";
    String OPERATION_MULTISEARCH = "MULTISEARCH";
    String OPERATION_EXISTS = "EXISTS";
    String PARAM_INDEX_ID = "indexId";
    String PARAM_DATA = "data";
    String PARAM_INDEX_NAME = "indexName";
    String PARAM_INDEX_TYPE = "indexType";
    String PARAM_CONSISTENCY_LEVEL = "consistencyLevel";
    String PARENT = "parent";
    String TRANSPORT_ADDRESSES = "transportAddresses";
    String PROTOCOL = "elasticsearch";
    String LOCAL_NAME = "local";
    String IP = "ip";
    String PORT = "port";
    Integer DEFAULT_PORT = 9200;
    String DEFAULT_CONSISTENCY_LEVEL = "default";
    String TRANSPORT_ADDRESSES_SEPARATOR_REGEX = ",";
    String IP_PORT_SEPARATOR_REGEX = ":";
}