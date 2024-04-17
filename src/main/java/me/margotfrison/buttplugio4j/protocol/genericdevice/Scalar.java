package me.margotfrison.buttplugio4j.protocol.genericdevice;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
public class Scalar {
	int Index;
	double Scalar;
	String ActuatorType;
}
