package ubfr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;

import ubfr.Exception.InvalidBlockException;
import ubfr.Exception.InvalidRangeException;

public class IpRangeTest {

	@Test
	public void testParseString() throws Exception {
		// 132.230.25.15
		// 132.230.25.15-24
		// 132.230.24.0
		// 132.230.20.0/24

		// 132.230

		// assertEquals("132.230.25.15",
		// IpRange.parseString("132.230.25.*").getStartIp());
	}

	@Test
	public void testGetBlocks() throws Exception {
		assertThrows(InvalidBlockException.class, () -> {
			IpRange.getBlocks("132.230.25.");
		});
		assertThrows(InvalidBlockException.class, () -> {
			IpRange.getBlocks("132.230.25");
		});
		assertThrows(InvalidRangeException.class, () -> {
			IpRange.getBlocks("132.230");
		});
		assertThrows(InvalidRangeException.class, () -> {
			IpRange.getBlocks("132.230.");
		});

		assertThrows(NumberFormatException.class, () -> {
			IpRange.getBlocks("132.*.25.*");
		});
		assertThrows(NumberFormatException.class, () -> {
			IpRange.getBlocks("111.ss.25.*");
		});
		assertThrows(NumberFormatException.class, () -> {
			IpRange.getBlocks("111.132.25.6-4");
		});

		assertThrows(InvalidRangeException.class, () -> {
			IpRange.getBlocks("xyz");
		});

		assertArrayEquals(new String[] { "132", "230", "*", "*" }, IpRange.getBlocks("132.230.*"));
		assertArrayEquals(new String[] { "132", "230", "*", "*" }, IpRange.getBlocks("132.230.*.*"));
		assertArrayEquals(new String[] { "132", "230", "25", "*" }, IpRange.getBlocks("132.230.25.*"));
		assertArrayEquals(new String[] { "132", "230", "25", "2-10" }, IpRange.getBlocks("132.230.25.2-10"));
		assertArrayEquals(new String[] { "132", "230", "*", "2-10" }, IpRange.getBlocks("132.230.*.2-10"));
	}

	@Test
	public void testGetRange() throws Exception {

		String[] tests = { 
				"132.230.25.*			132.230.25.0 	132.230.25.255",
				"132.230.25.* 			132.230.25.0 	132.230.25.255",
				"132.230.25.10-15		132.230.25.10 	132.230.25.15",
				"132.230.*				132.230.0.0 	132.230.255.255",
				"132.230.*.*			132.230.0.0 	132.230.255.255",
				"132.230.23-55.*		132.230.23.0 	132.230.55.255",
				"132.230.23.10-43		132.230.23.10 	132.230.23.43"
		};

		for (String test : tests) {
			assertEquals(
					new IpRange(IpAddress.parseIpAddress(test.split("\\s+")[1]),
							IpAddress.parseIpAddress(test.split("\\s+")[2])).getString(),
					IpRange.getRange(test.split("\\s+")[0]).getString());
		}

		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.25.0"),
				IpRange.getRange("132.230.25.*").lowerLimit));

		assertThrows(InvalidBlockException.class, () -> {
			IpRange.getRange("132.230.23-55");
		});
		assertThrows(InvalidBlockException.class, () -> {
			IpRange.getRange("132.230.*.10-43");
		});
		assertThrows(InvalidBlockException.class, () -> {
			IpRange.getRange("132.230.*.");
		});
		
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("2001:4860:4860:0:0:0:0:88ff"),
				IpRange.parseIpRange("2001:4860:4860:0:0:0:0:88ff/128").getUpperLimit()));
		
		
		
	}

	@Test
	public void testGetLowerLimit() throws Exception {
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.0.34"),
				IpRange.parseIpRange("132.230.0.34").getLowerLimit()));
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.0.0"),
				IpRange.parseIpRange("132.230.*").getLowerLimit()));
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.10.23"),
				IpRange.parseIpRange("132.230.10.23-29").getLowerLimit()));
	}

	@Test
	public void testGetUpperLimit() throws Exception {
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.0.34"),
				IpRange.parseIpRange("132.230.0.34").getUpperLimit()));
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.255.255"),
				IpRange.parseIpRange("132.230.*").getUpperLimit()));
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.10.29"),
				IpRange.parseIpRange("132.230.10.23-29").getUpperLimit()));
		
		// ipv6
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("2001:4860:4860:0:0:0:0:88ff"),
				IpRange.parseIpRange("2001:4860:4860:0:0:0:0:88ff/128").getUpperLimit()));
		
	}

	@Test
	public void testToString() throws Exception {
		assertEquals("132.230.0.0-132.230.0.255", (IpRange.parseIpRange("132.230.0.*")).getString());
		assertEquals("132.230.0.0-132.230.255.255", (IpRange.parseIpRange("132.230.*")).getString());
		assertEquals("132.230.10.10-132.230.10.10", (IpRange.parseIpRange("132.230.10.10")).getString());
		assertEquals("132.230.10.17-132.230.10.210", (IpRange.parseIpRange("132.230.10.17-210")).getString());
		assertEquals("132.230.10.17-132.230.10.210",
				(IpRange.parseIpRange("132.230.10.17-132.230.10.210")).getString());
		assertEquals("132.230.10.0-132.230.12.255", (IpRange.parseIpRange("132.230.10-12.*")).getString());

		assertThrows(InvalidRangeException.class, () -> {
			IpRange.parseIpRange("132.230.10.17-132.230.10.10").getString();
		});
		assertThrows(NumberFormatException.class, () -> {
			IpRange.parseIpRange("132.230.10.117-21").getString();
		});
	}

	@Test
	public void testToCidr() throws Exception {
		assertArrayEquals(new String[] { "132.230.25.0/24" },
				(IpRange.parseIpRange("132.230.25.0-255")).toCidr().toArray());
		assertArrayEquals(new String[] { "132.230.25.128/25" },
				(IpRange.parseIpRange("132.230.25.128-255")).toCidr().toArray());
		assertArrayEquals(new String[] { "132.230.25.0/24" },
				(IpRange.parseIpRange("132.230.25.*")).toCidr().toArray());

		assertArrayEquals(new String[] { "132.230.25.0/25", "132.230.25.128/32" },
				(IpRange.parseIpRange("132.230.25.0-128")).toCidr().toArray());

		assertArrayEquals(new String[] { "132.230.0.0/16" }, (IpRange.parseIpRange("132.230.*")).toCidr().toArray());

		assertArrayEquals(new String[] { "132.230.25.0/24", "132.230.26.0/24" },
				(IpRange.parseIpRange("132.230.25-26.*")).toCidr().toArray());

		assertArrayEquals(new String[] { "132.230.25.0/24" },
				(IpRange.parseIpRange("132.230.25.0-255")).toCidr().toArray());

		assertArrayEquals(new String[] { "132.230.25.0/24", "132.230.26.0/25" },
				(IpRange.parseIpRange("132.230.25.0-132.230.26.127")).toCidr().toArray());

		assertArrayEquals(new String[] { "132.230.25.10/31", "132.230.25.12/30", "132.230.25.16/28", "132.230.25.32/27",
				"132.230.25.64/26", "132.230.25.128/25", "132.230.26.0/27", "132.230.26.32/30", "132.230.26.36/31" },
				(IpRange.parseIpRange("132.230.25.10-132.230.26.37")).toCidr().toArray());

		// https://www.ipaddressguide.com/cidr
		// first string defines the range
		// all other strings describing the resulting cidr strings
		String[] positiveTests = new String[] { "132.230.25.* 132.230.25.0/24", "132.230.*.* 132.230.0.0/16",
				"132.230.* 132.230.0.0/16",
				"132.230.25.10-132.230.26.37 132.230.25.10/31 132.230.25.12/30 132.230.25.16/28 132.230.25.32/27 132.230.25.64/26 132.230.25.128/25 132.230.26.0/27 132.230.26.32/30 132.230.26.36/31",
				"132.230.15.10-132.230.56.43 132.230.15.10/31 132.230.15.12/30 132.230.15.16/28 132.230.15.32/27 132.230.15.64/26 132.230.15.128/25 132.230.16.0/20 132.230.32.0/20 132.230.48.0/21 132.230.56.0/27 132.230.56.32/29 132.230.56.40/30",
				"145.132.23.23-149.132.33.34 145.132.23.23/32 145.132.23.24/29 145.132.23.32/27 145.132.23.64/26 145.132.23.128/25 145.132.24.0/21 145.132.32.0/19 145.132.64.0/18 145.132.128.0/17 145.133.0.0/16 145.134.0.0/15 145.136.0.0/13 145.144.0.0/12 145.160.0.0/11 145.192.0.0/10 146.0.0.0/7 148.0.0.0/8 149.0.0.0/9 149.128.0.0/14 149.132.0.0/19 149.132.32.0/24 149.132.33.0/27 149.132.33.32/31 149.132.33.34/32" };

		for (String test : positiveTests) {
			assertArrayEquals(getCidr(test), (IpRange.parseIpRange(getRange(test))).toCidr().toArray());
		}
		
		// first string is the input cidr
		// second string ist the proccessed cidr
		positiveTests = new String[] { 
				"132.230.25.0/32 132.230.25.0/32",
				"132.230.25.11/32 132.230.25.11/32",
				"132.230.25.0/31 132.230.25.0/31",
				"132.230.25.1/31 132.230.25.0/31",
				"132.230.25.17/31 132.230.25.16/31",
				"132.230.25.16/31 132.230.25.16/31",
				"132.230.25.0/30 132.230.25.0/30",
				"132.230.25.3/30 132.230.25.0/30",
				"132.230.25.5/30 132.230.25.4/30",
				"132.230.25.4/30 132.230.25.4/30",
				"132.230.123.122/17 132.230.0.0/17",
				"132.230.128.252/17 132.230.128.0/17",
				"132.230.25.0/24 132.230.25.0/24",
				"132.230.25.128/25 132.230.25.128/25",
				"132.230.25.127/25 132.230.25.0/25"
		};

		for (String test : positiveTests) {
			assertArrayEquals(getCidr(test), (IpRange.parseIpRange(getRange(test))).toCidr().toArray());
		}

		assertThrows(NumberFormatException.class, () -> {
			IpRange.parseIpRange("132.230.25.0/aa").getString();
		});
		
		assertThrows(NumberFormatException.class, () -> {
			IpRange.parseIpRange("132.230.25.0/66").getString();
		});
		
		assertThrows(NumberFormatException.class, () -> {
			IpRange.parseIpRange("132.230.25.*/66").getString();
		});
		
		assertThrows(NumberFormatException.class, () -> {
			IpRange.parseIpRange("132.230.25.2/*").getString();
		});
		
		
		assertArrayEquals(new String[] {"2001:4860:4860:0000:0000:0000:0000:88ff/128"}, IpRange.parseIpRange("2001:4860:4860:0:0:0:0:88ff/128").toCidr().toArray());
	}

	private String[] getCidr(String s) {
		String[] parts = s.split("\\s", 2);
		return parts[1].split("\\s");
	}

	private String getRange(String s) {
		String[] parts = s.split("\\s", 2);
		return parts[0];
	}

}
