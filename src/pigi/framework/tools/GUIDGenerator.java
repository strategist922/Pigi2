package pigi.framework.tools;

import java.util.UUID;

/**
 * Simple GUID generator
 * @author kgalecki
 *
 */
public class GUIDGenerator {
	public static String getNext() {
		return UUID.randomUUID().toString();
	}

	public static String getMaxGUID() {
		return "ffffffff-ffff-ffff-ffff-ffffffffffff";
	}

	public static String getMinGUID() {
		return "00000000-0000-0000-0000-000000000000";
	}

	public static int getGUIDLength() {
		return 36;
	}
}
