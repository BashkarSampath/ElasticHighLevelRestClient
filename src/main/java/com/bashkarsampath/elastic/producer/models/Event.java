package com.bashkarsampath.elastic.producer.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {
	@JsonProperty("eventHeader")
	public EventHeader eventHeader;
	@JsonProperty("eventAttributes")
	public EventAttributes eventAttributes;
}
