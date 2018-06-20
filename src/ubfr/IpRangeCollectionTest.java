package ubfr;

import static org.junit.Assert.assertArrayEquals;

import org.junit.jupiter.api.Test;

public class IpRangeCollectionTest {

	@Test
	public void testAdd() throws Exception {
		IpRangeCollection ipRangeCollection = new IpRangeCollection();

		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.128-132.230.25.255"));
		assertArrayEquals(new String[] {"132.230.25.128/25"}, ipRangeCollection.toCidr().toArray());

		ipRangeCollection = new IpRangeCollection();
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.0-132.230.25.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.128-132.230.25.255"));
		assertArrayEquals(new String[] {"132.230.25.0/25", "132.230.25.128/25"}, ipRangeCollection.toCidr().toArray());
	}

	@Test
	public void testCompact() throws Exception {
		IpRangeCollection ipRangeCollection = new IpRangeCollection();
		
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.0-132.230.25.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.128-132.230.25.255"));
		assertArrayEquals(new String[] {"132.230.25.0/24"}, ipRangeCollection.compact().toCidr().toArray());

		ipRangeCollection = new IpRangeCollection();
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.0-132.230.25.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.26.0-132.230.26.127"));
		assertArrayEquals(new String[] {"132.230.25.0/25", "132.230.26.0/25"}, ipRangeCollection.compact().toCidr().toArray());

		ipRangeCollection = new IpRangeCollection();
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.0-132.230.25.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.26.0-132.230.26.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.*"));
		assertArrayEquals(new String[] {"132.230.25.0/24", "132.230.26.0/25"}, ipRangeCollection.compact().toCidr().toArray());

		ipRangeCollection = new IpRangeCollection();
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.0-132.230.25.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.26.0-132.230.26.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.30.0-132.230.30.127"));
		assertArrayEquals(new String[] {"132.230.25.0/25", "132.230.26.0/25", "132.230.30.0/25"}, ipRangeCollection.compact().toCidr().toArray());

		ipRangeCollection = new IpRangeCollection();
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.0-132.230.25.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.26.128-132.230.26.255"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.30.0-132.230.30.127"));
		assertArrayEquals(new String[] {"132.230.25.0/25", "132.230.26.128/25", "132.230.30.0/25"}, ipRangeCollection.compact().toCidr().toArray());

		ipRangeCollection = new IpRangeCollection();
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.0-132.230.25.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.26.128-132.230.26.255"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.30.0-132.230.30.127"));
		assertArrayEquals(new String[] {"132.230.25.0/25", "132.230.26.128/25", "132.230.30.0/25"}, ipRangeCollection.compact().toCidr().toArray());

		ipRangeCollection = new IpRangeCollection();
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.0-132.230.25.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.26.128-132.230.26.255"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.30.0-132.230.30.128"));
		assertArrayEquals(new String[] {"132.230.25.0/25", "132.230.26.128/25", "132.230.30.0/25", "132.230.30.128/32"}, ipRangeCollection.compact().toCidr().toArray());

		ipRangeCollection = new IpRangeCollection();
		ipRangeCollection.add(IpRange.parseIpRange("132.230.25.0-132.230.25.127"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.26.128-132.230.26.255"));
		ipRangeCollection.add(IpRange.parseIpRange("132.230.30.0-132.230.30.130"));
		assertArrayEquals(new String[] {"132.230.25.0/25", "132.230.26.128/25", "132.230.30.0/25", "132.230.30.128/31", "132.230.30.130/32"}, ipRangeCollection.compact().toCidr().toArray());

		
	}

}
