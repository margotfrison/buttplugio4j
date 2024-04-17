package me.margotfrison.buttplugio4j.protocol.enumeration;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Data;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SensorReadCommand extends SensorCommand {
	String FeatureDescriptor;
}
