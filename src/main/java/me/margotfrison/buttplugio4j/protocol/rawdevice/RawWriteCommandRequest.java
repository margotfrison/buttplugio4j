package me.margotfrison.buttplugio4j.protocol.rawdevice;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.margotfrison.buttplugio4j.protocol.Message;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RawWriteCommandRequest extends Message {
	int DeviceIndex;
	String Endpoint;
	byte[] Data;
	boolean WriteWithResponse;

	@Override
	public String getType() {
		return "RawWriteCmd";
	}
}
