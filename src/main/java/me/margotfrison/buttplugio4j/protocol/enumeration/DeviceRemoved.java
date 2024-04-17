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
public class DeviceRemoved extends Message {
	int DeviceIndex;
	
	@Override
	public String getType() {
		return "DeviceRemoved";
	}
}
