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
public class ScalarCommandRequest extends Message {
	int DeviceIndex;
	List<Scalar> Scalars;

	@Override
	public String getType() {
		return "ScalarCmd";
	}
}
