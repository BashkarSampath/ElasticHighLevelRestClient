package com.bashkarsampath.elastic.producer.controller;

import java.io.IOException;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bashkarsampath.elastic.producer.configurations.SwaggerDocumentation;
import com.bashkarsampath.elastic.producer.models.Event;
import com.bashkarsampath.elastic.producer.services.ByteSizeConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@SwaggerDocumentation
public class ElasticController implements CommandLineRunner {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired(required = true)
	private RestHighLevelClient client;

	private static final String DEV_RAW_INDEX = "dev-raw-index";
	private BulkRequest bulkRequest = new BulkRequest();

	private boolean doesIndexExist(RestHighLevelClient client, String indexname) throws IOException {
		GetIndexRequest request = new GetIndexRequest(indexname);
		request.local(false);
		request.humanReadable(true);
		request.includeDefaults(false);
		request.indicesOptions(null);
		return client.indices().exists(request, RequestOptions.DEFAULT);
	}

	private void createIndexIfDoesNotExist() throws IOException {
		if (!doesIndexExist(client, DEV_RAW_INDEX)) {
			CreateIndexRequest request = new CreateIndexRequest(DEV_RAW_INDEX);
			request.setMasterTimeout(TimeValue.timeValueSeconds(5));
			client.indices().create(request, RequestOptions.DEFAULT);
		}
	}

	@PostMapping("/post")
	public ResponseEntity<?> sendToElastic(@RequestBody String payload) throws IOException {
		IndexRequest indexRequest = new IndexRequest(DEV_RAW_INDEX);
		indexRequest.source(payload, XContentType.JSON);
		return new ResponseEntity<>(client.index(indexRequest, RequestOptions.DEFAULT), HttpStatus.OK);
	}

	@PostMapping("/bulked")
	public ResponseEntity<?> sendToElasticAsBulk(@RequestBody Event event) throws IOException {
		String id = Generators.timeBasedGenerator().generate().toString() + "-"
				+ Generators.randomBasedGenerator().generate().toString();
		bulkRequest = checkSizeAndBulkPostIfNeeded(client, bulkRequest);
		bulkRequest.add(new IndexRequest(DEV_RAW_INDEX).id(id).source(objectMapper.writeValueAsString(event),
				XContentType.JSON));
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	private BulkRequest checkSizeAndBulkPostIfNeeded(RestHighLevelClient client, BulkRequest request)
			throws IOException {
		log.info("Number of requests present: " + request.requests().size());
		double size = ByteSizeConverter.getInKBs(request.estimatedSizeInBytes());
		if (size >= 4) {
			request.timeout(TimeValue.timeValueMinutes(2));
			BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
			if (!bulkResponse.hasFailures()) {
				log.debug("Bulk insert success: inserted " + request.requests().size() + " records");
				request = new BulkRequest();
			} else {
				log.error("Bulk request failure: " + bulkResponse.getItems().length + " records failed of total "
						+ request.getIndices().size() + " records");
				log.error(bulkResponse.buildFailureMessage());
				request = new BulkRequest();
				for (int i = 0; i < bulkResponse.getItems().length; i++) {
					BulkItemResponse itemResponse = bulkResponse.getItems()[i];
					log.error("Printing failed index records.");
					if (itemResponse.isFailed()) {
						log.info("Failure message of index: " + itemResponse.getIndex()
								+ "; Failure message of this Index: " + itemResponse.getFailureMessage());
					}
					request.add(itemResponse.getIndex().getBytes(), 0, itemResponse.getIndex().getBytes().length,
							XContentType.JSON);
				}
			}
		}
		return request;
	}

	@Override
	public void run(String... args) throws Exception {
		createIndexIfDoesNotExist();
	}
}
