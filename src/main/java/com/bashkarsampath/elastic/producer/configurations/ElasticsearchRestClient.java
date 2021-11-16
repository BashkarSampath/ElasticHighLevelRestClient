package com.bashkarsampath.elastic.producer.configurations;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

@Scope("singleton")
@Configuration
public class ElasticsearchRestClient {
	@Value("${elasticsearch.ip}")
	String ipPort;

	@Bean
	public RestClientBuilder restClientBuilder() {
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("331723643", "Qwerty#27"));
		return RestClient.builder(makeHttpsHost(ipPort)).setHttpClientConfigCallback(new HttpClientConfigCallback() {
			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				httpClientBuilder.disableAuthCaching();
				return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		});
	}

	@Primary
	@Bean(name = "RestHighLevelClient")
	public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
		return new RestHighLevelClient(restClientBuilder);
	}

	private HttpHost makeHttpsHost(String s) {
		String[] address = s.split(":");
		String ip = address[0];
		int port = Integer.parseInt(address[1]);
		return new HttpHost(ip, port, "https");
	}
}