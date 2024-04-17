package me.margotfrison.buttplugio4j.protocol.genericsensor;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.margotfrison.buttplugio4j.protocol.Message;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SensorReadCommandRequest extends Message {
	int DeviceIndex;
	int SensorIndex;
	String SensorType;

	@Override
	public String getType() {
		return "SensorReadCmd";
	}
}
