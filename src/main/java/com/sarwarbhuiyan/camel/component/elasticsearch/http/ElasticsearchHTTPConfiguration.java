package com.sarwarbhuiyan.camel.component.elasticsearch.http;

import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;
import org.apache.camel.spi.UriPath;

@UriParams
public class ElasticsearchHTTPConfiguration {

    private boolean local;

    @UriPath @Metadata(required = "false")
    private String clusterName;
    
    @UriParam(enums = "INDEX,UPDATE,BULK,BULK_INDEX,GET_BY_ID,MULTIGET,DELETE,EXISTS,SEARCH,MULTISEARCH")
    private String operation;

    @UriParam
    private String indexName;
    @UriParam
    private String indexType;

    @UriParam
    private Boolean data;

    @UriParam
    private String ip;
    @UriParam
    private String transportAddresses;
    @UriParam(defaultValue = "9200")
    private int port = ElasticsearchConstants.DEFAULT_PORT;
    @UriParam(defaultValue = "true")
    private Boolean clientTransportSniff = true;

    @UriParam(defaultValue = "DEFAULT")
    private String consistencyLevel = ElasticsearchConstants.DEFAULT_CONSISTENCY_LEVEL;
    
    /**                                                                                                                                                                              
     * Name of cluster or use local for local mode                                                                                                                                   
     */
    public String getClusterName() {
        return clusterName;
    }
    
    /**
     * The write consistency level to use with INDEX and BULK operations (can be any of ONE, QUORUM, ALL or DEFAULT)
     */
    public String getConsistencyLevel() {
        return consistencyLevel;
    }
    
    /**
     * What operation to perform
     */
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * The name of the index to act against
     */
    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    /**
     * The type of the index to act against
     */
    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    /**
     * Is the node going to be allowed to allocate data (shards) to it or not. This setting map to the <tt>node.data</tt> setting.
     */
    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }

    /**
     * The TransportClient remote host ip to use
     */
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Comma separated list with ip:port formatted remote transport addresses to use.
     * The ip and port options must be left blank for transportAddresses to be considered instead.
     */
    public String getTransportAddresses() {
        return transportAddresses;
    }

    public void setTransportAddresses(String transportAddresses) {
        this.transportAddresses = transportAddresses;
    }

    /**
     * The TransportClient remote port to use (defaults to 9300)
     */
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Is the client allowed to sniff the rest of the cluster or not (default true). This setting map to the <tt>client.transport.sniff</tt> setting.
     */
    public Boolean getClientTransportSniff() {
        return clientTransportSniff;
    }

    public void setClientTransportSniff(Boolean clientTransportSniff) {
        this.clientTransportSniff = clientTransportSniff;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }
}
