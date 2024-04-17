package me.margotfrison.buttplugio4j.protocol.enumeration;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Data;

@Data
@EqualsAndHashCode
public class DeviceMessages {
	List<ScalarCommand> ScalarCmd;
	List<RotateCommand> RotateCmd;
	List<LinearCommand> LinearCmd;
	List<SensorReadCommand> SensorReadCmd;
	List<SensorSubscribeCommand> SensorSubscribeCmd;
	StopDeviceCommand StopDeviceCmd;
}
