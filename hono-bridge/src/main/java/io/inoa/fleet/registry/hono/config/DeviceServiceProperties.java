package io.inoa.fleet.registry.hono.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DeviceServiceProperties {

	private static final int DEFAULT_MAX_BCRYPT_COSTFACTOR = 10;

	private final Set<String> hashAlgorithmsAllowList = new HashSet<>();
	private int maxBcryptCostfactor = DEFAULT_MAX_BCRYPT_COSTFACTOR;

	public int getMaxBcryptCostfactor() {
		return this.maxBcryptCostfactor;
	}

	/**
	 * Sets the maximum number of iterations to use for bcrypt password hashes.
	 * <p>
	 * The default value of this property is 10.
	 *
	 * @param costfactor
	 *            The maximum number.
	 * @throws IllegalArgumentException
	 *             if iterations is &lt; 4 or &gt; 31.
	 */
	public void setMaxBcryptCostfactor(final int costfactor) {
		if (costfactor < 4 || costfactor > 31) {
			throw new IllegalArgumentException("iterations must be > 3 and < 32");
		} else {
			this.maxBcryptCostfactor = costfactor;
		}
	}

	public Set<String> getHashAlgorithmsAllowList() {
		return Collections.unmodifiableSet(this.hashAlgorithmsAllowList);
	}

	public void setHashAlgorithmsAllowList(final String[] hashAlgorithmsAllowList) {
		Objects.requireNonNull(hashAlgorithmsAllowList);
		this.hashAlgorithmsAllowList.clear();
		this.hashAlgorithmsAllowList.addAll(Arrays.asList(hashAlgorithmsAllowList));
	}

}
