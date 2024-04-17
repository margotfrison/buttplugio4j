package me.margotfrison.buttplugio4j.protocol.rawdevice;

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
public class RawUnsubscribeCommandRequest extends Message {
	int DeviceIndex;
	String Endpoint;

	@Override
	public String getType() {
		return "RawUnsubscribeCmd";
	}
}
