package ubfr;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;

import ubfr.Exception.InvalidIpAddressException;

public class IpAddressTest {

	@Test
	public void testParseIpAddress() throws Exception {

		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress(2229672342l),
				IpAddress.parseIpAddress("132.230.25.150")));

		assertEquals(2229672342l, ((Ipv4Address) IpAddress.parseIpAddress("132.230.25.150")).longValue());

		assertEquals(3232236042l, ((Ipv4Address) IpAddress.parseIpAddress("192.168.2.10")).longValue());
		assertEquals(3232236042l, ((Ipv4Address) IpAddress.parseIpAddress("192.168.002.10")).longValue());
		assertEquals(4294967295l, ((Ipv4Address) IpAddress.parseIpAddress("255.255.255.255")).longValue());

		assertThrows(NumberFormatException.class, () -> {
			IpAddress.parseIpAddress("192.168.2.510");
		});
		assertThrows(NumberFormatException.class, () -> {
			IpAddress.parseIpAddress("192.168.2.a");
		});
		assertThrows(NumberFormatException.class, () -> {
			IpAddress.parseIpAddress("192..2.510");
		});
		assertThrows(InvalidIpAddressException.class, () -> {
			IpAddress.parseIpAddress("132.230.2.510.10");
		});
		assertThrows(InvalidIpAddressException.class, () -> {
			IpAddress.parseIpAddress("132.230..510.10");
		});
		assertThrows(InvalidIpAddressException.class, () -> {
			IpAddress.parseIpAddress("132.230");
		});
		assertThrows(NumberFormatException.class, () -> {
			IpAddress.parseIpAddress("132.230.*.30");
		});

		// IPv6 Tests
		assertEquals("2001:4860:4860:0000:0000:0000:0000:8888",
				new Ipv6Address(2306204062558715904l, 34952l).toString());

		Ipv6Address ipv6Addr = (Ipv6Address) IpAddress.parseIpAddress("2001:4860:4860:0000:0000:0000:0000:8888");
		assertEquals(2306204062558715904l, ipv6Addr.highBits());
		assertEquals(34952l, ipv6Addr.lowBits());

		ipv6Addr = (Ipv6Address) IpAddress.parseIpAddress("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
		assertEquals(2306139570357600256l, ipv6Addr.highBits());
		assertEquals(151930230829876l, ipv6Addr.lowBits());

	}

	@Test
	public void testToString() throws Exception {
		assertEquals("0.0.0.222", new Ipv4Address(222l).toString());
		assertEquals("0.0.1.0", new Ipv4Address(256l).toString());
		assertEquals("132.230.25.150", new Ipv4Address(2229672342l).toString());
		assertEquals("255.255.255.255", new Ipv4Address(4294967295l).toString());

		assertThrows(InvalidIpAddressException.class, () -> {
			new Ipv4Address(4294967296l).toString();
		});

		assertThrows(InvalidIpAddressException.class, () -> {
			new Ipv4Address(-10l).toString();
		});

	}

	@Test
	public void testGetUpperLimit() throws Exception {
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.25.0"),
				IpAddress.parseIpAddress("132.230.25.150").getLowerLimit(24)));
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.25.128"),
				IpAddress.parseIpAddress("132.230.25.150").getLowerLimit(25)));
		
		assertTrue(EqualsBuilder.reflectionEquals(new Ipv6Address(2306204062558715904l, 34952l),
				new Ipv6Address(2306204062558715904l, 34952l).getUpperLimit(128)));

		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("2001:4860:4860:0:0:0:0:88ff"),
				IpAddress.parseIpAddress("2001:4860:4860:0000:0000:0000:0000:8888").getUpperLimit(120)));
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("2001:4860:4860:0:0:0:0:88bf"),
				IpAddress.parseIpAddress("2001:4860:4860:0000:0000:0000:0000:8888").getUpperLimit(122)));

	}

	@Test
	public void testGetLowerLimit() throws Exception {
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.25.255"),
				IpAddress.parseIpAddress("132.230.25.150").getUpperLimit(24)));
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("132.230.25.255"),
				IpAddress.parseIpAddress("132.230.25.150").getUpperLimit(25)));
		
		assertTrue(EqualsBuilder.reflectionEquals(new Ipv6Address(2306204062558715904l, 34952l),
				new Ipv6Address(2306204062558715904l, 34952l).getLowerLimit(128)));
		
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("2001:4860:4860:0:0:0:0:8800"),
				IpAddress.parseIpAddress("2001:4860:4860:0000:0000:0000:0000:8888").getLowerLimit(120)));
		assertTrue(EqualsBuilder.reflectionEquals(IpAddress.parseIpAddress("2001:4860:4860:0:0:0:0:8880"),
				IpAddress.parseIpAddress("2001:4860:4860:0000:0000:0000:0000:8888").getLowerLimit(122)));
		
	}

}
