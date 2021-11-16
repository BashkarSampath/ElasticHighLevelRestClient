package com.bashkarsampath.elastic.producer.controller;

import java.io.IOException;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bashkarsampath.elastic.producer.configurations.SwaggerDocumentation;
import com.bashkarsampath.elastic.producer.models.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;

@RestController
@SwaggerDocumentation
public class WithBulkProcessor {

	private static final String DEV_RAW_INDEX = "dev-raw-index";
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired(required = true)
	private BulkProcessor bulkProcessor;

	@PostMapping("/bulkedp")
	public ResponseEntity<?> sendToElasticAsBulk(@RequestBody Event event) throws IOException {
		String id = Generators.timeBasedGenerator().generate().toString() + "-"
				+ Generators.randomBasedGenerator().generate().toString();
		bulkProcessor.add(new IndexRequest(DEV_RAW_INDEX).id(id).source(objectMapper.writeValueAsString(event),
				XContentType.JSON));
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

}