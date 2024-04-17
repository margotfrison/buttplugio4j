package me.margotfrison.buttplugio4j.protocol.status;

import com.google.gson.annotations.SerializedName;

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
public class Error extends Message {
	String ErrorMessage;
    @SerializedName("code")
	ErrorCode ErrorCode;

	@Override
	public String getType() {
		return "Error";
	}

	public static enum ErrorCode {
        @SerializedName("0")
		ERROR_UNKNOWN,
        @SerializedName("1")
		ERROR_INIT,
        @SerializedName("2")
		ERROR_PING,
        @SerializedName("3")
		ERROR_MSG,
        @SerializedName("4")
		ERROR_DEVICE;
	}
}
