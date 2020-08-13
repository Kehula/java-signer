package main;

import java.util.Arrays;
import java.util.Date;

/**
* @author kehul on 13.08.2020
*/
public class SingleThread {
	public static void runTest(int[] inputData) {
		long startTime = new Date().getTime();
		String[] outputResult = new String[inputData.length];
		for(int i = 0; i < inputData.length; i++) {
			outputResult[i] = Multihash(SingleHash(inputData[i]));
		}
		long elapsedTime = new Date().getTime() - startTime;
		//System.out.println(CombineResults(outputResult));
		System.out.printf("Elapsed time: %.2f sec\n", elapsedTime / 1000f);
	}
	
	public static String SingleHash(int value) {
		String data = String.valueOf(value);
		//System.out.printf("%d SingleHash data %s\n", value, data);
		
		String md5 = Utils.dataSignerMD5(data);
		//System.out.printf("%d SingleHash md5(data) %s\n", value, md5);
		
		String crc32md5 = Utils.dataSignerCrc32(md5);
		//System.out.printf("%d SingleHash crc32(md5(data)) %s\n", value, crc32md5);
		
		String defaultCrc32 = Utils.dataSignerCrc32(data);
		//System.out.printf("%d SingleHash crc32(data) %s\n", value, defaultCrc32);
		
		String result = String.format("%s~%s", defaultCrc32, crc32md5);
		//System.out.printf("%d SingleHash result %s\n", value, result);
		return result;
	}

	public static String Multihash(String value) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			String crc32 = Utils.dataSignerCrc32(i + value);
			//System.out.printf("%s MultiHash: crc32(%2$d+data) %2$d %3$s\n", value, i, crc32);
			result.append(crc32);
		}
		//System.out.printf("%s MultiHash result: %s\n", value, result.toString());
		return result.toString();
	}

	public static String CombineResults(String[] value) {
		Arrays.sort(value);
		StringBuilder result = new StringBuilder(value[0]);
		for (int i = 1; i < value.length; i++) {
			result.append("_").append(value[i]);
		}
		//System.out.printf("CombineResults %s\n", result.toString());
		return result.toString();
	}
}
