package me.margotfrison.buttplugio4j.protocol.genericdevice;

import lombok.EqualsAndHashCode;
import lombok.Data;

@Data
@EqualsAndHashCode
public class Vector {
	int Index;
	int Duration;
	double Position;
}
