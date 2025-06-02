package io.inoa.measurement.things.domain;

public enum ObisId {

	// Firmware of the meter
	OBIS_METER_FIRMWARE("0.2.0"),
	// Serial number of the meter
	OBIS_METER_SERIAL("C.1.0"),
	// Environmental temperature of the meter in °C
	OBIS_METER_TEMPERATURE("S.1.1.9"),
	// Active energy consumed from the grid in kWh
	OBIS_1_8_0("1-0:1.8.0"),
	// Active energy returned to the grid in kWh
	OBIS_2_8_0("1-0:2.8.0"),
	// Positive active instantaneous power in kW
	OBIS_1_7_0("1-0:1.7.0"),
	// Negative active instantaneous power in kW
	OBIS_2_7_0("1-0:2.7.0"),
	// Instantaneous current in A
	OBIS_CURRENT("1-0:11.7.0"),
	// Instantaneous voltage in V
	OBIS_VOLTAGE("1-0:12.7.0"),
	// Frequency of signal in the installation in Hz
	OBIS_FREQUENCY("1-0:14.7.0"),
	// Gas consumed from the grid in m³
	OBIS_GAS_IN("7-20:3.0.0");

	private final String obisId;

	ObisId(String obisId) {
		this.obisId = obisId;
	}

	public String getObisId() {
		return obisId;
	}
}
