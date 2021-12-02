package io.inoa.fleet.thing.modbus;

public class CRC16 {

	/**
	 * Calculate CRC16 check code
	 *
	 * @param bytes
	 *            Byte array @ return {@link byte} check code
	 */
	public byte[] getCRC(byte[] bytes) {
		// CRC registers are all 1
		int CRC = 0x0000ffff;
		// Polynomial check value
		int POLYNOMIAL = 0x0000a001;
		int i, j;
		for (i = 0; i < bytes.length; i++) {
			CRC ^= (int) bytes[i] & 0x000000ff;
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00000001) != 0) {
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC >>= 1;
				}
			}
		}
		// Result converted to hex
		String result = Integer.toHexString(CRC).toUpperCase();
		if (result.length() != 4) {
			StringBuffer sb = new StringBuffer("0000");
			result = sb.replace(4 - result.length(), 4, result).toString();
		}
		// High position in the front position in the back
		// return result.substring(2, 4) + " " + result.substring(0, 2);
		// Exchange high low, low in front, high in back

		byte[] resultByte = new byte[2];
		resultByte[1] = (byte) ((CRC >> 8) & 0xff);
		resultByte[0] = (byte) (CRC & 0xff);
		return resultByte;
	}

}
