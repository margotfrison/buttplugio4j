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
public class ServerInfo extends Message {
	String ServerName;
	int MessageVersion;
	int MaxPingTime;

	@Override
	public String getType() {
		return "ServerInfo";
	}
}
