package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Driver {

	private static String file_path = "dp.txt";
	private static File file = new File(file_path);
	private static int numberOfCities;
	private static String[] cities;
	private static String start;
	private static String end;
	private static Integer[][] dtable;
	private static Integer[][] reachedFrom;
	private static City[] paths;
	private static String path = "";

	public static void main(String[] args) {
		read_data();
		
		printArray(cities);
		
		dtable = new Integer[numberOfCities][numberOfCities];
		reachedFrom = new Integer[numberOfCities][numberOfCities];
		fillDTable();
		printArray2(dtable);
		System.out
				.println("///////////////////////////////////////////////////////////////////////////////////////////");
		printArray2(reachedFrom);

		getOptimalPath(start, end);
		System.out.println(path);
		System.out.println("Optimal path cost from start to end = " + getOptimalPathCost(start, end));
		for (int i = 0; i < 5; i++)
			System.out.println();
	}

	public static void read_data() {
		try {
			Scanner sc = new Scanner(file);
			numberOfCities = sc.nextInt();
			sc.nextLine();
			cities = new String[numberOfCities];
			paths = new City[numberOfCities - 1];
			String[] c = sc.nextLine().split(",");
			start = c[0].trim();
			cities[numberOfCities - 1] = end = c[1].trim();
			int j = 0;
			while (sc.hasNext()) {
				String[] cityInfo = sc.nextLine().split(", ");
				int accessPointsNo = cityInfo.length - 1;
				String city = cityInfo[0];
				cities[j] = city;
				String[] accessPoints = new String[accessPointsNo];
				int[] petrolCosts = new int[accessPointsNo];
				int[] hotelCosts = new int[accessPointsNo];
				for (int i = 1; i < cityInfo.length; i++) {
					String[] info = cityInfo[i].replace("[", " ").replace("]", " ").trim().split(",");
					accessPoints[i - 1] = info[0];
					petrolCosts[i - 1] = Integer.parseInt(info[1]);
					hotelCosts[i - 1] = Integer.parseInt(info[2]);
				}
				City newCity = new City(city, accessPointsNo, accessPoints, hotelCosts, petrolCosts);
				paths[j] = newCity;
				j++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// fill dp table
	public static void fillDTable() {

		// initialize dtable by filling it with maxInt;
		for (int i = 0; i < dtable.length; i++)
			for (int j = 0; j < dtable[i].length; j++)
				dtable[i][j] = Integer.MAX_VALUE;

		for (int i = 0; i < dtable.length; i++)
			dtable[i][i] = 0;

		// fill the dp table with values as read from the file
		int startFrom = 1;
		for (int i = 0; i < dtable.length - 1; i++) {
			int numberOfConnections = paths[i].getNumberOfAccessPoints();
			int j = startFrom;
			for (; j < startFrom + numberOfConnections; j++) {
				dtable[i][j] = paths[i].getTotalCosts()[j - startFrom];
				reachedFrom[i][j] = i;
			}
			if (i + 1 == startFrom)
				startFrom = j;
		}

		// fill the rest of dp table by calculating the minimum paths' costs between cities
		startFrom = 1;
		int i = 0;
		int istart = 1;
		for (; i < dtable.length - 4; i++) {
			int numberOfConnections = paths[i].getNumberOfAccessPoints();
			int jConnections = paths[startFrom].getNumberOfAccessPoints();
			int j = startFrom;
			while (startFrom + numberOfConnections + jConnections <= dtable.length) {
				for (int k = startFrom + numberOfConnections; k < startFrom + numberOfConnections + jConnections; k++) {
					j = startFrom;
					for (; j < startFrom + numberOfConnections; j++) {
						int cost = dtable[i][j] + dtable[j][k];
						if (cost < dtable[i][k]) {
							dtable[i][k] = cost;
							reachedFrom[i][k] = j;
						}
					}
				}
				startFrom += numberOfConnections;
				numberOfConnections = jConnections;
				try {
					jConnections = paths[startFrom].getNumberOfAccessPoints();
				} catch (ArrayIndexOutOfBoundsException e) {

				}
			}

			if (i + 1 == istart)
				istart += paths[i].getNumberOfAccessPoints();

			startFrom = istart;
		}
	}

	public static void printArray(City[] arr) {
		for (int i = 0; i < arr.length; i++)
			System.out.println(arr[i] + " ");
	}

	public static void printArray(String[] arr) {
		for (int i = 0; i < arr.length; i++)
			System.out.print(arr[i] + ", ");
		System.out.println();
	}

	public static void printArray2(Integer[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++)
				System.out.print(arr[i][j] + " ");
			System.out.println();
		}
	}

	public static void printArray2(int[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++)
				System.out.print(arr[i][j] + " ");
			System.out.println();
		}
	}

	// this method gets the optimal path between two cities
	public static void getOptimalPath(String start, String end) {
		int startInt = findCityIndex(start);
		int endInt = findCityIndex(end);

		if (startInt != -1 && endInt != -1 && !start.equalsIgnoreCase(end) && startInt < endInt)
			getOptimalPath(startInt, endInt);
	}

	// helper recursive method
	public static void getOptimalPath(int start, int end) {
		if (start == end) {
			path += cities[start];
			return;
		}

		System.out.println(reachedFrom[start][end]);
		getOptimalPath(start, reachedFrom[start][end]);
		path += "--> " + cities[end];
	}

	// this method returns the minimum cost between two cities
	public static int getOptimalPathCost(String start, String end) {
		int startInt = findCityIndex(start);
		int endInt = findCityIndex(end);

		if (startInt == -1 || endInt == -1 || startInt > endInt) {
			return -1;
		}

		return dtable[startInt][endInt];
	}

	// find the index of a given city in the cities array
	public static int findCityIndex(String city) {
		for (int i = 0; i < cities.length; i++) {
			if (cities[i].equals(city))
				return i;
		}
		return -1;
	}

}
