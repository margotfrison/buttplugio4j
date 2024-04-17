package me.margotfrison.buttplugio4j.protocol.enumeration;

import lombok.EqualsAndHashCode;
import lombok.Data;

@Data
@EqualsAndHashCode
public class Device {
	String DeviceName;
	int DeviceIndex;
	DeviceMessages DeviceMessages;
}
