package me.margotfrison.buttplugio4j.protocol.enumeration;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public abstract class SensorCommand {
	String SensorType;
	int[][] SensorRange;
}
