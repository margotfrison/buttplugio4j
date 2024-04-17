package me.margotfrison.buttplugio4j.protocol.status;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.margotfrison.buttplugio4j.protocol.Message;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Ping extends Message {
	@Override
	public String getType() {
		return "Ping";
	}
}
