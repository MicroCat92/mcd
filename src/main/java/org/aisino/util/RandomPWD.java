package org.aisino.util;

import java.util.Random;

public class RandomPWD {
	
	public static Random random = new Random();
	
	/**
	 * 获取随机密码
	 * @return
	 */
	public static String getRandomPWD() {
		return "" + (char)(random.nextInt(26)+65) + (char)(random.nextInt(10)+48) + (char)(random.nextInt(90)+33)
				+ (char)(random.nextInt(26)+97) + (char)(random.nextInt(90)+33) + (char)(random.nextInt(90)+33)
				+ (char)(random.nextInt(15)+33) + (char)(random.nextInt(90)+33);
	}
	
	public static void main(String[] args) {
		for(int i=0; i<10; i++) {
			System.out.println(RandomPWD.getRandomPWD());
		}
	}

}
