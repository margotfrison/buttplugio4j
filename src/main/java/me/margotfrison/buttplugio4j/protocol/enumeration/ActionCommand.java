package me.margotfrison.buttplugio4j.protocol.enumeration;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public abstract class ActionCommand {
	String FeatureDescriptor;
	int StepCount;
	String ActuatorType;
}
