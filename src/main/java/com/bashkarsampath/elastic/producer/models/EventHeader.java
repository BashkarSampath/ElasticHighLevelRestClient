package com.bashkarsampath.elastic.producer.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventHeader {
	@JsonProperty("source_event_id")
	public String sourceEventId;
	@JsonProperty("source_application_code")
	public String sourceApplicationCode;
	@JsonProperty("source_system_name")
	public String sourceSystemName;
	@JsonProperty("event_timestamp")
	public String eventTimestamp;
	@JsonProperty("event_channel_type")
	public String eventChannelType;
	@JsonProperty("event_activity_type")
	public String eventActivityType;
}
