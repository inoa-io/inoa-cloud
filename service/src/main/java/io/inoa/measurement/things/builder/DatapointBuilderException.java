package io.inoa.measurement.things.builder;

import java.io.Serial;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception thrown if datapoint cannot be built
 *
 * @author fabian.schlegel@grayc.de
 */
@Getter
@AllArgsConstructor
public class DatapointBuilderException extends Exception {

	private UUID thingId;
	private String configName;
	private String configIssue;

	@Serial
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return String.format(
				"Thing configuration invalid [thingId='%s', configName='%s', configIssue='%s']",
				thingId, configName, configIssue);
	}
}
