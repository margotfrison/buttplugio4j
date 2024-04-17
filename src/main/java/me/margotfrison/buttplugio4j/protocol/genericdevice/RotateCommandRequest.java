package me.margotfrison.buttplugio4j.protocol.genericdevice;

import java.util.List;

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
public class RotateCommandRequest extends Message {
	int DeviceIndex;
	List<Rotation> Scalars;

	@Override
	public String getType() {
		return "RotateCmd";
	}
}
