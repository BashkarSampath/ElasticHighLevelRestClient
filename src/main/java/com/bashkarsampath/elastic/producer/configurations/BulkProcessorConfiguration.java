package com.bashkarsampath.elastic.producer.configurations;

import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class BulkProcessorConfiguration {
	@Autowired(required = true)
	private RestHighLevelClient restClient;

	private static final String DEV_RAW_INDEX = "dev-raw-index";
	BulkProcessor.Listener bulkProcessorListner = new BulkProcessor.Listener() {
		@Override
		public void beforeBulk(long executionId, BulkRequest request) {
			int numberOfActions = request.numberOfActions();
			log.info(String.format("Executing bulk %d with %d requests", executionId, numberOfActions));
		}

		@Override
		public void afterBulk(long executionId, BulkRequest request, BulkResponse bulkResponse) {
			if (bulkResponse.hasFailures()) {
				for (BulkItemResponse bulkItemResponse : bulkResponse) {
					if (bulkItemResponse.isFailed()) {
						BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
						throw new RuntimeException(
								String.format("Adding document %s to ElasticSearch failed", failure.getId()),
								failure.getCause());
					}
				}
				log.info(String.format("Bulk %d executed with failures", executionId));
			} else {
				log.info(String.format("Bulk %d completed in %d milliseconds", executionId,
						bulkResponse.getTook().getMillis()));
			}
		}

		@Override
		public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
			log.info(String.format("Failed to execute bulk %s", failure));
			throw new RuntimeException(failure);
		}
	};

	@Bean("BulkProcessor")
	public BulkProcessor configure() {
		BulkProcessor.Builder builder = BulkProcessor.builder(
				(request, bulkListener) -> restClient.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
				bulkProcessorListner);
		builder.setGlobalIndex(DEV_RAW_INDEX);
		builder.setBulkActions(100);
		builder.setBulkSize(new ByteSizeValue(4, ByteSizeUnit.MB));
		builder.setConcurrentRequests(1);
		builder.setFlushInterval(TimeValue.timeValueSeconds(5));
		builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(100), 3));
		builder.setConcurrentRequests(1);
		return builder.build();
	}

}
