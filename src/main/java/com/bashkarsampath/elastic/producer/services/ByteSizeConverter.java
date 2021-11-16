package com.bashkarsampath.elastic.producer.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ByteSizeConverter {
	public static double getInKBs(double sizeBytes) {
		return round(sizeBytes, 2);
	}

	public static double getInMBs(double sizeBytes) {
		return round(getInKBs(sizeBytes), 2);
	}

	public static double getInGBs(double sizeBytes) {
		return round(getInMBs(sizeBytes), 2);
	}

	private static double round(double sizeBytes, int precise) {
		return BigDecimal.valueOf(sizeBytes).divide(BigDecimal.valueOf(1024)).setScale(precise, RoundingMode.HALF_UP)
				.doubleValue();
	}
}