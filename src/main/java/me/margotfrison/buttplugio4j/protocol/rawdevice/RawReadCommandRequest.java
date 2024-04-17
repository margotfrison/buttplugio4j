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
public class RawReadCommandRequest extends Message {
	int DeviceIndex;
	String Endpoint;
	/** 0 by default to read all. See the <a href="https://buttplug-spec.docs.buttplug.io/docs/spec/raw#rawreadcmd">official documentation</a>. */
	int ExpectedLength = 0;
	boolean WaitForData;

	@Override
	public String getType() {
		return "RawReadCmd";
	}
}
