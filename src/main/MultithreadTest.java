package main;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class MultithreadTest {
	static final Object lock = new Object();
	
	public static void runTest(int[] inputData) throws InterruptedException {
		long startTime = new Date().getTime();
		String[] outputResult = new String[inputData.length];
		Semaphore semaphore = new Semaphore(Integer.MAX_VALUE);
		for(int i = 0; i < inputData.length; i++) {
			semaphore.acquire(1);
			new Thread((new ThreadByRunnable(i, semaphore) {
				@Override
				public void run() {
					try {
						outputResult[step] = Multihash(SingleHash(inputData[step]));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sem.release();
				}
			})).start();
		}
		semaphore.acquire(Integer.MAX_VALUE);
		long elapsedTime = new Date().getTime() - startTime;
		//System.out.println(CombineResults(outputResult));
		System.out.printf("Elapsed time: %.2f sec\n", elapsedTime / 1000f);
	}
	
	public static String SingleHash(int value) throws InterruptedException {
		String data = String.valueOf(value);
		//System.out.printf("%d SingleHash data %s\n", value, data);
		String[] buffer = new String[2];
		Semaphore localSemaphore = new Semaphore(Integer.MAX_VALUE);
		localSemaphore.acquire(1);
		new Thread(new ThreadByRunnable(1, localSemaphore) {
			@Override
			public void run() {
				String md5 = "";
				synchronized (lock) {
					md5 = Utils.dataSignerMD5(data);
				}
				//System.out.printf("%d SingleHash md5(data) %s\n", value, md5);
				
				String crc32md5 = Utils.dataSignerCrc32(md5);
				//System.out.printf("%d SingleHash crc32(md5(data)) %s\n", value, crc32md5);
				buffer[step] = crc32md5;
				sem.release();
			}
		}).start();
		buffer[0] = Utils.dataSignerCrc32(data);
		//System.out.printf("%d SingleHash crc32(data) %s\n", value, buffer[0]);
		localSemaphore.acquire(Integer.MAX_VALUE);
		
		String result = String.format("%s~%s", buffer[0], buffer[1]);
		//System.out.printf("%d SingleHash result %s\n", value, result);
		return result;
	}
	
	public static String Multihash(String value) throws InterruptedException {
		StringBuilder result = new StringBuilder();
		String[] buffer = new String[6];
		Semaphore localSemaphore = new Semaphore(Integer.MAX_VALUE);
		for (int i = 0; i < 6; i++) {
			localSemaphore.acquire(1);
			new Thread(new ThreadByRunnable(i, localSemaphore) {
				@Override
				public void run() {
					String crc32 = Utils.dataSignerCrc32(step + value);
					//System.out.printf("%s MultiHash: crc32(%2$d+data) %2$d %3$s\n", value, step, crc32);
					buffer[step] = crc32;
					sem.release();
				}
			}).start();
			
		}
		localSemaphore.acquire(Integer.MAX_VALUE);
		for (String buf : buffer)
			result.append(buf);
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
	
	private static class ThreadByRunnable implements Runnable {
		int step;
		Semaphore sem;
		ThreadByRunnable(int i, Semaphore semaphore) {
			this.step = i;
			this.sem = semaphore;
		}
		
		@Override
		public void run() {
		
		}
	}
}
