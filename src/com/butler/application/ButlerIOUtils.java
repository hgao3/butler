package com.butler.application;

import java.util.Scanner;

public class ButlerIOUtils {

	public static int getInputWithIntRange(Scanner scan, int min, int max) {
		while (scan.hasNext()) {
			if (scan.hasNextInt()) {
				int value = scan.nextInt();
				// get int input from mix to max
				if (value >= min && value <= max) {
					return value;
				} else {
					System.out.println("You have to enter within a range.");
				}
			} else {
				System.out.println("You have to enter a int.");
				scan.next();
			}
		}
		return -1;
	}
	
	
}
