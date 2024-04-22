package me.margotfrison.buttplugio4j.protocol.handshake;

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
public class RequestServerInfo extends Message {
	String ClientName;
	int MessageVersion;
	
	public RequestServerInfo(String clientName) {
		this.ClientName = clientName;
		this.MessageVersion = Message.LAST_SUPPORTED_VERSION;
	}

	@Override
	public String getType() {
		return "RequestServerInfo";
	}
}
