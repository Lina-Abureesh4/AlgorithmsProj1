package application;

import java.util.Arrays;

public class City {

	private String city;
	private int numberOfAccessPoints;
	private String[] accessPoints;
	private int[] hotelCosts;
	private int[] petrolCosts;

	public City(String city, int numberOfAccessPoints, String[] accessPoints, int[] hotelCosts, int[] petrolCosts) {
		super();
		this.city = city;
		this.numberOfAccessPoints = numberOfAccessPoints;
		this.accessPoints = accessPoints;
		this.hotelCosts = hotelCosts;
		this.petrolCosts = petrolCosts;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getNumberOfAccessPoints() {
		return numberOfAccessPoints;
	}

	public void setNumberOfAccessPoints(int numberOfAccessPoints) {
		this.numberOfAccessPoints = numberOfAccessPoints;
	}

	public String[] getAccessPoints() {
		return accessPoints;
	}

	public void setAccessPoints(String[] accessPoints) {
		this.accessPoints = accessPoints;
	}

	public int[] getHotelCosts() {
		return hotelCosts;
	}

	public void setHotelCosts(int[] hotelCosts) {
		this.hotelCosts = hotelCosts;
	}

	public int[] getPetrolCosts() {
		return petrolCosts;
	}

	public void setPetrolCosts(int[] petrolCosts) {
		this.petrolCosts = petrolCosts;
	}

	@Override
	public String toString() {
		return "City [city=" + city + ", numberOfAccessPoints=" + numberOfAccessPoints + ", accessPoints="
				+ Arrays.toString(accessPoints) + ", hotelCosts=" + Arrays.toString(hotelCosts) + ", petrolCosts="
				+ Arrays.toString(petrolCosts) + "]";
	}

	public String getAccessPoint(int location) {
		return this.accessPoints[location];
	}

	public int[] getTotalCosts() {
		int[] totalCosts = new int[numberOfAccessPoints];
		for (int i = 0; i < numberOfAccessPoints; i++)
			totalCosts[i] = petrolCosts[i] + hotelCosts[i];
		return totalCosts;

	}

	public boolean hasConnectionWith(String city) {
		for (String acessPoint : accessPoints) {
			if (city.equalsIgnoreCase(acessPoint))
				return true;
		}
		return false;
	}

	public int getTotalCostWithCity(String cityName) {
		int index = -1;
		for (int i = 0; i < numberOfAccessPoints; i++) {
			if (accessPoints[i].equalsIgnoreCase(cityName)) {
				index = i;
				break;
			}
		}
		if (index != -1)
			return petrolCosts[index] + hotelCosts[index];

		return -1;
	}
}
