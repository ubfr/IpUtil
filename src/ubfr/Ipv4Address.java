package ubfr;

import ubfr.Exception.InvalidIpAddressException;

public class Ipv4Address extends IpAddress {
	
	protected long bits;
	
	public Ipv4Address() {
		
	}
	
	public Ipv4Address(long ipAddress) throws InvalidIpAddressException {
		if (0<= ipAddress && ipAddress <= 4294967295l) {
			this.bits = ipAddress;
		} else {
			throw new InvalidIpAddressException();
		}
	}
	
	public static IpAddress parseIpAddress(String str) throws InvalidIpAddressException, NumberFormatException {
		String[] blocks = str.split("\\.");

		if (blocks.length != 4) {
			throw new InvalidIpAddressException();
		}

		Integer result = 0;
		for (int idx = 0 ; idx <= 3; idx++) {
			Integer block = Integer.parseInt(blocks[idx]);
			if (0 <= block && block <= 255 ) {
				result |= Integer.parseInt(blocks[idx]) << ((3-idx) * 8);
			} else {
				throw new NumberFormatException();
			}
		}
		
		return new Ipv4Address(Integer.toUnsignedLong(result));
	}
	
	
	public String toString() {
		final short[] shorts = new short[4];
		for (int i = 0; i < 4; i++) {
			shorts[i] = (short) (((bits << i * 8) >>> 8 * (3)) & 0xFF);
		}
		
        final String[] strings = new String[shorts.length];
        for (int i = 0; i < shorts.length; i++)
        {
            strings[i] = String.valueOf(shorts[i]);
        }
        
        return String.join(".", strings);
	}
	
	public long longValue() {
		return this.bits;
	};
	
	@Override
	public boolean isGreater(IpAddress ipAddr) {
		return this.longValue() > ((Ipv4Address)ipAddr).longValue();
	}
	
	public boolean isGreaterEqual(IpAddress ipAddr) {
		return this.longValue() >= ((Ipv4Address)ipAddr).longValue();
	}
	
	public boolean isLesser(IpAddress ipAddr) {
		return this.longValue() < ((Ipv4Address)ipAddr).longValue();
	}
	
	public boolean isLesserEqual(IpAddress ipAddr) {
		return this.longValue() <= ((Ipv4Address)ipAddr).longValue();
	}
	
	public Ipv4Address next() {
		return new Ipv4Address(bits + 1);
	}
}
