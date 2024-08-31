package application;

public class Path {

	private String from; 
	private String to; 
	private int petrol_cost;
	private int hotel_cost;
	
	public Path(String from, String to, int petrol_cost, int hotel_cost) {
		super();
		this.from = from;
		this.to = to;
		this.petrol_cost = petrol_cost;
		this.hotel_cost = hotel_cost;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public int getPetrol_cost() {
		return petrol_cost;
	}

	public void setPetrol_cost(int petrol_cost) {
		this.petrol_cost = petrol_cost;
	}

	public int getHotel_cost() {
		return hotel_cost;
	}

	public void setHotel_cost(int hotel_cost) {
		this.hotel_cost = hotel_cost;
	}

	@Override
	public String toString() {
		return "Path [from=" + from + ", to=" + to + ", petrol_cost=" + petrol_cost + ", hotel_cost=" + hotel_cost
				+ "]";
	}
}
