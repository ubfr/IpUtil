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
		
		assertTrue(EqualsBuilder.reflectionEquals(
				IpAddress.parseIpAddress(2229672342l),
				IpAddress.parseIpAddress("132.230.25.150")));

		assertEquals(2229672342l, ((Ipv4Address)IpAddress.parseIpAddress("132.230.25.150")).longValue());

		//		assertEquals(3232236042l, IpAddress.parseIpAddress("192.168.2.10").longValue());
//		assertEquals(3232236042l, IpAddress.parseIpAddress("192.168.002.10").longValue());
//		assertEquals(4294967295l, IpAddress.parseIpAddress("255.255.255.255").longValue());
//
//		assertThrows(NumberFormatException.class, () -> {
//			IpAddress.parseIpAddress("192.168.2.510").longValue();
//		});
//		assertThrows(NumberFormatException.class, () -> {
//			IpAddress.parseIpAddress("192.168.2.a").longValue();
//		});
//		assertThrows(NumberFormatException.class, () -> {
//			IpAddress.parseIpAddress("192..2.510").longValue();
//		});
//		assertThrows(InvalidIpAddressException.class, () -> {
//			IpAddress.parseIpAddress("132.230.2.510.10").longValue();
//		});
//		assertThrows(InvalidIpAddressException.class, () -> {
//			IpAddress.parseIpAddress("132.230..510.10").longValue();
//		});
//		assertThrows(InvalidIpAddressException.class, () -> {
//			IpAddress.parseIpAddress("132.230").longValue();
//		});
//		assertThrows(NumberFormatException.class, () -> {
//			IpAddress.parseIpAddress("132.230.*.30").longValue();
//		});
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

}
