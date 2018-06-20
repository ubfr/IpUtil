package ubfr;

import java.nio.ByteBuffer;

public class Ipv6Address extends IpAddress{

	protected static final int N_SHORTS = 8;
	
	protected long highBits;
	protected long lowBits;
	
	public Ipv6Address(long highBits, long lowBits) {
		
		this.highBits = highBits;
		this.lowBits = lowBits;
	}
	
	protected IpAddress parseIpv6Address(String[] blocks) {

		int numberOfBlocks = blocks.length - 1;

		for (String block : blocks) {

		}

		return new Ipv6Address(0l, 0l);

	}

	public static byte[] toByteArray(long highBits, long lowBits) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(16).putLong(highBits).putLong(lowBits);
		return byteBuffer.array();
	}

	private short[] toShortArray() {
		final short[] shorts = new short[N_SHORTS];

		for (int i = 0; i < N_SHORTS; i++) {
			if (0 <= i && i < 4)
				shorts[i] = (short) (((highBits << i * 16) >>> 16 * (N_SHORTS - 1)) & 0xFFFF);
			else
				shorts[i] = (short) (((lowBits << i * 16) >>> 16 * (N_SHORTS - 1)) & 0xFFFF);
		}

		return shorts;
	}
	
	public String toString() {
		return "";
	}
	
	public boolean isGreater(Ipv6Address ipAddr) {
		if (this.highBits == ipAddr.highBits) {
			return this.lowBits > ipAddr.lowBits;
		} else {
			return this.highBits > ipAddr.highBits;
		}
	}
	
	public boolean isGreaterEqual(Ipv6Address ipAddr) {
		if (this.highBits == ipAddr.highBits) {
			return this.lowBits >= ipAddr.lowBits;
		} else {
			return this.highBits > ipAddr.highBits;
		}
	}
	
	public Ipv6Address next() {
		return new Ipv6Address(highBits, lowBits);
	}
	
}
