package ubfr;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import ubfr.Exception.InvalidBlockException;
import ubfr.Exception.InvalidIpAddressException;
import ubfr.Exception.InvalidRangeException;

public class IpRange {

	public IpAddress upperLimit;
	public IpAddress lowerLimit;

	public static void main(String[] args) {
		IpRange range = IpRange.parseIpRange("132.230.25.10");
		System.out.println(range.toCidr());
	}
	
	public IpRange(IpAddress lowerLimit, IpAddress upperLimit) {
		try {
			lowerLimit.isGreater(upperLimit);
		} catch (InvalidIpAddressException e) {
			throw new InvalidRangeException();
		}
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
	}
	
	public static IpRange parseIpRange(String s) throws InvalidRangeException {
		// remove all withspace characters
		s = StringUtils.removeAll(s, "\\s");

		String[] parts = decodeCidrNotation(s);

		// 132.230.250.234 - 132.230.250.255
		String[] limits = s.split("-");
		if (limits.length == 2) {
			try {
				IpAddress lowerLimit = IpAddress.parseIpAddress(limits[0]);
				IpAddress upperLimit = IpAddress.parseIpAddress(limits[1]);
				return new IpRange(lowerLimit, upperLimit);
			} catch (InvalidIpAddressException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		try {
			IpRange result = getRange(s);
			return result;
		} catch (InvalidBlockException e) {
			throw new InvalidRangeException();
		}
	}

	private static String[] decodeCidrNotation(String s) {
		String[] parts = s.split("/");

		String ipAddr = "";
		String cidrSuffix = "";

		switch (parts.length) {
		case 1:
			ipAddr = parts[0];
			break;
		case 2:
			ipAddr = parts[0];
			cidrSuffix = parts[1];
			break;
		}

		return new String[] { ipAddr, cidrSuffix };
	}

	public static String[] getBlocks(String ipAddr) throws InvalidBlockException, InvalidRangeException {
		String[] blocks = ipAddr.split("\\.");

		String blockA = "";
		String blockB = "";
		String blockC = "";
		String blockD = "";

		switch (blocks.length) {
		case 3:
			if (ipAddr.endsWith(".*")) {
				blockA = blocks[0];
				blockB = blocks[1];
				blockC = blocks[2];
				blockD = "*";
			} else {
				throw new InvalidBlockException();
			}
			break;
		case 4:
			if (!ipAddr.endsWith(".")) {
				blockA = blocks[0];
				blockB = blocks[1];
				blockC = blocks[2];
				blockD = blocks[3];
			}
			break;
		default:
			throw new InvalidRangeException();
		}

		short value = Short.parseShort(blockA);
		if (value < 0 || 255 < value) {
			throw new NumberFormatException();
		}

		value = Short.parseShort(blockB);
		if (value < 0 || 255 < value) {
			throw new NumberFormatException();
		}

		boolean moreChecksNeeded = true;
		String[] parts = blockC.split("-");
		if (parts.length == 2) {
			short highC = Short.parseShort(parts[1]);
			short lowC = Short.parseShort(parts[0]);
			if (!(0 <= lowC && lowC < highC && highC <= 255)) {
				throw new NumberFormatException();
			}
			moreChecksNeeded = false;
		}

		if (moreChecksNeeded && blockC.equals("*")) {
			moreChecksNeeded = false;
		}

		if (moreChecksNeeded) {
			value = Short.parseShort(blockC);
			if (value < 0 || 255 < value) {
				throw new NumberFormatException();
			}
		}

		moreChecksNeeded = true;
		parts = blockD.split("-");
		if (parts.length == 2) {
			short highD = Short.parseShort(parts[1]);
			short lowD = Short.parseShort(parts[0]);
			if (!(0 <= lowD && lowD < highD && highD <= 255)) {
				throw new NumberFormatException();
			}
			moreChecksNeeded = false;
		}

		if (moreChecksNeeded && blockD.equals("*")) {
			moreChecksNeeded = false;
		}

		if (moreChecksNeeded) {
			value = Short.parseShort(blockD);
			if (value < 0 || 255 < value) {
				throw new NumberFormatException();
			}
		}

		return new String[] { blockA, blockB, blockC, blockD };
	}

	public static IpRange getRange(String s) throws InvalidBlockException, InvalidRangeException {

		String[] blocks = getBlocks(s);

		short blockA = Short.parseShort(blocks[0]);
		if (blockA < 0 || 255 < blockA) {
			throw new NumberFormatException();
		}

		short blockB = Short.parseShort(blocks[1]);
		if (blockB < 0 || 255 < blockB) {
			throw new NumberFormatException();
		}

		// allowed formats for blockC:
		// * number between 0 and 255,
		// Examples: 213, 234, 1, 99
		// * wildcard *
		// Examples: *
		// * two numbers between 0 and 255 separated by "-", first number must be
		// smaller or equal than the second number
		// Examples: 132-232, 232-255, 1-58

		Short blockC = null;
		try {
			blockC = Short.parseShort(blocks[2]);
			if (blockC < 0 || 255 < blockC) {
				throw new NumberFormatException();
			}
		} catch (Exception e) {
		}

		Short highC = null;
		Short lowC = null;
		if (blocks[2].equals("*")) {
			highC = 255;
			lowC = 0;
		}

		try {
			String[] parts = blocks[2].split("-");
			if (parts.length == 2) {
				highC = Short.parseShort(parts[1]);
				lowC = Short.parseShort(parts[0]);
				if (!(0 <= lowC && lowC < highC && highC <= 255)) {
					throw new NumberFormatException();
				}
			}
		} catch (Exception e) {
		}

		// handle block D
		Short blockD = null;
		try {
			blockD = Short.parseShort(blocks[3]);
			if (0 < blockD || blockC > 255) {
				throw new NumberFormatException();
			}
		} catch (Exception e) {
		}

		Short highD = null;
		Short lowD = null;
		if (blocks[3].equals("*")) {
			highD = 255;
			lowD = 0;
		}

		try {
			String[] parts = blocks[3].split("-");
			if (parts.length == 2) {
				highD = Short.parseShort(parts[1]);
				lowD = Short.parseShort(parts[0]);
				if (!(0 <= lowD && lowD < highD && highD <= 255)) {
					throw new NumberFormatException();
				}
			}
		} catch (Exception e) {
		}

		String resA = String.valueOf(blockA);
		String resB = String.valueOf(blockB);
		String resHighC = "";
		String resLowC = "";
		String resHighD = "";
		String resLowD = "";

		if (blockC == null) {
			if (blocks[3].equals("*")) {
				resHighC = String.valueOf(highC);
				resLowC = String.valueOf(lowC);
				resHighD = "255";
				resLowD = "0";
			} else
				throw new InvalidBlockException();
		}

		if (blockC != null) {
			resHighC = String.valueOf(blockC);
			resLowC = String.valueOf(blockC);
			if (blockD == null) {
				resHighD = String.valueOf(highD);
				resLowD = String.valueOf(lowD);
			} else {
				resHighD = String.valueOf(blockD);
				resLowD = String.valueOf(blockD);
			}
		}

		String end = String.valueOf(resA) + "." + String.valueOf(resB) + "." + String.valueOf(resHighC) + "."
				+ String.valueOf(resHighD);

		String start = String.valueOf(resA) + "." + String.valueOf(resB) + "." + String.valueOf(resLowC) + "."
				+ String.valueOf(resLowD);

		try {
			IpAddress lower = IpAddress.parseIpAddress(start);
			IpAddress upper = IpAddress.parseIpAddress(end);
			return new IpRange(lower, upper);
		} catch (InvalidIpAddressException e) {
			throw new InvalidRangeException();
		}

	}

	public String getString() throws InvalidIpAddressException {
		String lower = lowerLimit.toString();
		String upper = upperLimit.toString();
		String ipRange = lower + "-" + upper;

		return ipRange;
	}

	public IpAddress getLowerLimit() throws InvalidIpAddressException {
		return this.lowerLimit;
	}

	public IpAddress getUpperLimit() throws InvalidIpAddressException {
		return this.upperLimit;
	}

	public List<String> toCidr() {
		Map<Long, long[]> cidrRanges = getCidr(lowerLimit, this.upperLimit);

		// String[] result = new String[cidrRanges.size()];

		List<String> result = new LinkedList<String>();

		for (long[] cidrRange : cidrRanges.values()) {
			try {
				result.add(new Ipv4Address(cidrRange[0]).toString() + "/" + (32 - cidrRange[2] + 1));
			} catch (InvalidIpAddressException e) {

			}
		}

		return result;
	}

	private static Map<Long, long[]> getCidr(IpAddress lowerAddr, IpAddress upperAddr) {
		long lower = ((Ipv4Address) lowerAddr).longValue(); 
		long upper = ((Ipv4Address) upperAddr).longValue(); 

		return getCidr(31l, lower, upper, new TreeMap<Long, long[]>());
	}

	private static Map<Long, long[]> getCidr(long n, long lower, long upper, Map<Long, long[]> allRanges) {
		if (lower > upper) {
			return allRanges;
		}

		long highBlockUpper = (lower | (~(-1 << n )));
		long highBlockLower = (highBlockUpper & (-1 << n-1));

		long lowBlockLower = (lower & (-1 << n));
		long lowBlockUpper = (lowBlockLower | (~(-1 << n-1)));

		long[] result = new long[3];
		if (upper >= highBlockUpper && highBlockLower >= lower) {
			result[0] = highBlockLower;
			result[1] = highBlockUpper;
			result[2] = n;
		}

		if (upper >= lowBlockUpper && lowBlockLower >= lower) {
			result[0] = lowBlockLower;
			result[1] = lowBlockUpper;
			result[2] = n;
		}

		if (result[0] == 0 & result[1] == 0) {
			allRanges = IpRange.getCidr(n - 1, lower, upper, allRanges);
		} else {
			allRanges = IpRange.getCidr(n, lower, result[0] - 1, allRanges);
			allRanges = IpRange.getCidr(n, result[1] + 1, upper, allRanges);
			allRanges.put(result[0], result);
		}

		return allRanges;
	}
}