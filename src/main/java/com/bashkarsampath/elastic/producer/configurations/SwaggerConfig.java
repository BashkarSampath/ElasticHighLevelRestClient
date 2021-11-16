package com.bashkarsampath.elastic.producer.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.withClassAnnotation(SwaggerDocumentation.class)).build()
				.apiInfo(apiInfo());
	}

	public ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("MPS - Greenfield CCDS").version("1.0").description(
				"Greenfield-Credit Card Disputes is a MPS change, to provide near real-time data for DNA team for analysis and further optimization of credit card dispute process. The UL90 application housed in MPS architecture provides the direct API connectivity with VISA's VROL applications via RTSI and captures daily downloads of actionable disputes. With the enhancement, the historical data of dispute cases from the UL90's back up repository along with the ongoing updates from VISA will be sent to DNA to improve further analytics and automation capabilities for Cards and Frauds Operations.")
				.termsOfServiceUrl("http://swagger.io/terms/").license("Apache 2.0")
				.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html").build();
	}
}
