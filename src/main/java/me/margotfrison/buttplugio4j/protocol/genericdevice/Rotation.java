package me.margotfrison.buttplugio4j.protocol.genericdevice;

import lombok.EqualsAndHashCode;
import lombok.Data;

@Data
@EqualsAndHashCode
public class Rotation {
	int Index;
	double Speed;
	boolean Clockwise;
}
