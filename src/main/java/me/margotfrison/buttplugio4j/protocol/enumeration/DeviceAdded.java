package me.margotfrison.buttplugio4j.protocol.enumeration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.margotfrison.buttplugio4j.protocol.Message;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DeviceAdded extends Message {
	String DeviceName;
	int DeviceIndex;
	int DeviceMessageTimingGap;
	String DeviceDisplayName;
	DeviceMessages DeviceMessages;
	
	@Override
	public String getType() {
		return "DeviceAdded";
	}
}
