package main;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * @author kehul on 13.08.2020
 */
public class Utils {
	private static AtomicInteger overheat = new AtomicInteger(0);
	
	private static void OverheatLock() throws InterruptedException {
		for (;;) {
			if (overheat.compareAndExchange(0, 1) == 1) {
				System.out.println("Overheat lock happened!");
				Thread.sleep(1000);
			}	else {
				break;
			}
		}
	}
	
	private static void OverheatUnlock() throws InterruptedException {
		for (;;) {
			if (overheat.compareAndExchange(1, 0) == 0) {
				System.out.println("Overheat unlock happened!");
				Thread.sleep(1000);
			} else {
				break;
			}
		}
	}
	
	public static String dataSignerMD5(String data) {
		if (data == null) data = "";
		try {
			OverheatLock();
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.reset();
				md.update(data.getBytes());
				BigInteger bigInt = new BigInteger(1, md.digest());
				String md5Hex = bigInt.toString(16);
				
				Thread.sleep(10);
				return md5Hex;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				OverheatUnlock();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String dataSignerCrc32(String data) {
		if (data == null) data = "";
		
		try {
			Checksum crc32 = new CRC32();
			crc32.update(data.getBytes());
			Thread.sleep(1000);
			return String.valueOf(crc32.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
