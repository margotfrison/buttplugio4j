package me.margotfrison.buttplugio4j.protocol.genericsensor;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.margotfrison.buttplugio4j.protocol.Message;
import lombok.Data;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SensorReading extends Message {
	int DeviceIndex;
	int SensorIndex;
	List<Integer> Data;

	@Override
	public String getType() {
		return "SensorReading";
	}
}
