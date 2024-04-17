package me.margotfrison.buttplugio4j.protocol.enumeration;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.margotfrison.buttplugio4j.protocol.Message;
import lombok.Data;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StopScanning extends Message {
	@Override
	public String getType() {
		return "StopScanning";
	}
}
