package ubfr;

public class Ipv6Address extends IpAddress {

	protected static final short MAX_CIDR_SUFFIX = 128;
	
	protected long highBits;
	protected long lowBits;
	protected short max_cidr_suffix;
	
	public Ipv6Address(long highBits, long lowBits) {
		this.max_cidr_suffix=128;
		this.highBits = highBits;
		this.lowBits = lowBits;
	}

	public static IpAddress parseIpAddress(String s) {

		String[] blocks = s.split(":");

		long high = 0L;
		long low = 0L;

		for (int i = 0; i < 8; i++) {
			long longValue = Long.parseLong(blocks[i], 16);
			if (0 <= i && i < 4) {
				high |= (longValue << ((4 - i - 1) * 16));
			} else {
				low |= (longValue << ((4 - i - 1) * 16));
			}
		}

		return new Ipv6Address(high, low);
	}

	private short[] toShortArray() {
		int N_SHORTS = 8;
		final short[] shorts = new short[N_SHORTS];

		for (int i = 0; i < N_SHORTS; i++) {
			if (0 <= i && i < 4)
				shorts[i] = (short) (((highBits << i * 16) >>> 16 * (N_SHORTS - 1)) & 0xFFFF);
			else
				shorts[i] = (short) (((lowBits << i * 16) >>> 16 * (N_SHORTS - 1)) & 0xFFFF);
		}

		return shorts;
	}

	private String[] toArrayOfZeroPaddedstrings() {
		final short[] shorts = toShortArray();
		final String[] strings = new String[shorts.length];
		for (int i = 0; i < shorts.length; i++) {
			strings[i] = String.format("%04x", shorts[i]);
		}
		return strings;
	}

	public String toString() {
		return String.join(":", toArrayOfZeroPaddedstrings());
	}

	public boolean isGreater(IpAddress ipAddr) {
		Ipv6Address ipv6Addr = (Ipv6Address) ipAddr;

		if (this.highBits == ipv6Addr.highBits) {
			return this.lowBits > ipv6Addr.lowBits;
		} else {
			return this.highBits > ipv6Addr.highBits;
		}
	}

	public boolean isGreaterEqual(Ipv6Address ipAddr) {
		Ipv6Address ipv6Addr = (Ipv6Address) ipAddr;

		if (this.highBits == ipv6Addr.highBits) {
			return this.lowBits >= ipv6Addr.lowBits;
		} else {
			return this.highBits > ipv6Addr.highBits;
		}
	}

	public Ipv6Address next() {
		return new Ipv6Address(highBits, lowBits);
	}
	
	public Ipv6Address prev() {
		return new Ipv6Address(highBits, lowBits);
	}

	public long highBits() {
		return highBits;
	}

	public long lowBits() {
		return lowBits;
	}

	public IpAddress getUpperLimit(int cidrSuffix) {
		long lowBitsUpper = 0l;
		long highBitsUpper = 0l;

		if (cidrSuffix > 64) {
			highBitsUpper = highBits;
			lowBitsUpper = (lowBits | (~(-1 << MAX_CIDR_SUFFIX - cidrSuffix)));
		} else {
			lowBitsUpper = Long.MAX_VALUE;
			highBitsUpper = (highBits | (~(-1 << MAX_CIDR_SUFFIX - cidrSuffix)));
		}

		return new Ipv6Address(highBitsUpper, lowBitsUpper);
	}

	public IpAddress getLowerLimit(int cidrSuffix) {
		long lowBitsLower = 0l;
		long highBitsLower = 0l;

		if (cidrSuffix > 64) {
			highBitsLower = highBits;
			lowBitsLower = (lowBits & (-1 << MAX_CIDR_SUFFIX - cidrSuffix));
		} else {
			lowBitsLower = 0l;
			highBitsLower = (highBits & (-1 << MAX_CIDR_SUFFIX - cidrSuffix));
		}

		return new Ipv6Address(highBitsLower, lowBitsLower);
	}
	
	public short parseCidrSuffix(String s) {
		short cidrSuffix = Short.parseShort(s);
		if (cidrSuffix < 0 || cidrSuffix > MAX_CIDR_SUFFIX) {
			throw new NumberFormatException();
		}
		return cidrSuffix;
	}
}
