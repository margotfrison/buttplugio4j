package me.margotfrison.buttplugio4j.protocol.genericdevice;

import java.util.List;

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
public class LinearCommandRequest extends Message {
	int DeviceIndex;
	List<Vector> Scalars;

	@Override
	public String getType() {
		return "LinearCmd";
	}
}
