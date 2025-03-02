package lldAirlinebooking;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AirlineBookingSystem airlineBookingSystem = new AirlineBookingSystem();
		Customer admin = new Customer("Admin01", "admin01@gmail.com", 24, "9082692593", true);
		LocalDateTime departure = LocalDateTime.of(2025, 3, 1, 14, 30);
		LocalDateTime arrival = LocalDateTime.of(2025, 3, 1, 16, 30);
		airlineBookingSystem.addFlight("Flight01", "BLR", "NGP", departure, arrival, "6E ", "Indigo", admin);
		airlineBookingSystem.addFlight("Flight02", "CHE", "NGP", departure, arrival, "6E ", "Indigo", admin);
		airlineBookingSystem.search("BLR", SearchType.SRC);
		Customer bookedBy = new Customer("user1", "admin01@gmail.com", 24, "9082692593", false);
		Customer bookedFor1 = new Customer("user2", "admin01@gmail.com", 24, "9082692593", false);
		Customer bookedFor2 = new Customer("user3", "admin01@gmail.com", 24, "9082692593", false);
		List<Customer> customerList = List.of(bookedFor1, bookedFor2);
		Map<String, List<Pair>> seatsMap = new HashMap<String, List<Pair>>();
		seatsMap.put("Flight01", List.of(new Pair("A1", bookedFor1), new Pair("A2", bookedFor2)));
		seatsMap.put("Flight02", List.of(new Pair("A2", bookedFor1), new Pair("A1", bookedFor2)));
		airlineBookingSystem.bookFlight(List.of("Flight01", "Flight02"), customerList, bookedBy, seatsMap);
		airlineBookingSystem.cancelFlight("Flight02", admin);
	}

}
class Pair {
	String first;
	Customer second;
	public Pair(String first, Customer second) {
		super();
		this.first = first;
		this.second = second;
	}
	
}

interface NotificationManager {
	public void notify(List<Customer> customerList, String message);
}

class CancelNotificationManager implements NotificationManager {

	@Override
	public void notify(List<Customer> customerList, String message) {
		// TODO Auto-generated method stub
		System.out.println("cancellation logic exceuted");
		for (Customer c : customerList) {
			c.notify(message);
		}
	}
	
}

class AirlineBookingSystem {
	private FlightManager flightManager;
	private BookingManager bookingManager;
	private NotificationManager notificationManager;
	public AirlineBookingSystem() {
		flightManager = FlightManager.getInstance();
		bookingManager = new BookingManager();
		notificationManager = new CancelNotificationManager();
	}
	public void cancelFlight(String flightID, Customer admin) {
		// TODO Auto-generated method stub
		List<Flight> cur_flight = flightManager.getFlightsByID(List.of(flightID));
		if (admin.getIsAdmin()) {
			flightManager.cancelFlight(cur_flight.get(0));
		}
		List<Customer> notificationList = new ArrayList<Customer>();
		for (Entry<String, Seat> entry : cur_flight.get(0).getSeatMap().entrySet()) {
			if (entry.getValue().getIsBooked()) {
				notificationList.add(entry.getValue().getCustomer());
			}
		}
		notificationManager.notify(notificationList, cur_flight.get(0).toString() + " is cancelled");
		System.out.println("Flight : " + cur_flight.get(0).toString() + " is cancelled");
	}
	public void bookFlight(List<String> flightList, List<Customer> customerList, Customer bookedBy, 
			Map<String, List<Pair>> seatsMap) {
		// TODO Auto-generated method stub
		List<Flight> flights = flightManager.getFlightsByID(flightList);
		for (Flight flight : flights) {
			flight.bookSeat(seatsMap.get(flight.getFlightID()));
		}
		Booking newBooking = new Booking("Booking01", bookedBy, customerList, flights, PaymentStatus.PAID);
		bookingManager.addBooking(newBooking);
	}
	public void search(String key, SearchType src) {
		// TODO Auto-generated method stub
		Map<String, Flight> flightsMap = flightManager.getFlightMap();
		List<Flight> flightsResult = SearchTypeFactory.getSearchType(src).search(key, flightsMap);
		for (Flight flight : flightsResult) {
			System.out.println("Search results are : " + flight.toString());
		}
	}
	
	public void addFlight(String flightID, String src, String dest, LocalDateTime departureDateTime,
			LocalDateTime arrivalDateTime, String aircraftID, String providerID, Customer admin) {
		Map<String, Seat> seatMap = new HashMap<String, Seat>();
		seatMap.put("A1", new Seat(false, "A1", 3550.0, SeatType.BUISNESS, null));
		seatMap.put("A2", new Seat(false, "A2", 2000.0, SeatType.ECONOMY, null));
		seatMap.put("A3", new Seat(false, "A3", 2500.0, SeatType.PREMIUM_ECONOMY, null));
		Flight newFlight = new Flight(flightID, src, dest, departureDateTime, arrivalDateTime, 
				aircraftID, providerID, seatMap);
		
		if (admin.getIsAdmin()) {
			flightManager.addFlight(newFlight);
			System.out.println("New flight added. Flight details : " + newFlight.toString());
		}
	}
	
	
}

enum PaymentStatus {
	PAID,
	PENDING,
	FAILED,
	CANCELLED
}

class BookingManager {
	private Map<String, Booking> bookingsMap;
	BookingManager() {
		this.bookingsMap = new HashMap<String, Booking>();
	}
	public void addBooking(Booking booking) {
		bookingsMap.put(booking.getBookingID(), booking);
		System.out.println("Booking completed : " + booking.toString());
	}
}

class Booking {
	private String bookingID;
	private Customer bookedBy;
	private List<Customer> bookedFor;
	private List<Flight> flightsBooked;
	private PaymentStatus paymentStatus;
	public Booking(String bookingID, Customer bookedBy, List<Customer> bookedFor, List<Flight> flightsBooked,
			PaymentStatus paymentStatus) {
		super();
		this.bookingID = bookingID;
		this.bookedBy = bookedBy;
		this.bookedFor = bookedFor;
		this.flightsBooked = flightsBooked;
		this.paymentStatus = paymentStatus;
	}
	public Customer getBookedBy() {
		return bookedBy;
	}
	public void setBookedBy(Customer bookedBy) {
		this.bookedBy = bookedBy;
	}
	public List<Customer> getBookedFor() {
		return bookedFor;
	}
	public void setBookedFor(List<Customer> bookedFor) {
		this.bookedFor = bookedFor;
	}
	public List<Flight> getFlightsBooked() {
		return flightsBooked;
	}
	public void setFlightsBooked(List<Flight> flightsBooked) {
		this.flightsBooked = flightsBooked;
	}
	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getBookingID() {
		return bookingID;
	}
	public void setBookingID(String bookingID) {
		this.bookingID = bookingID;
	}
	
}

enum SeatType {
	BUISNESS,
	ECONOMY,
	PREMIUM_ECONOMY
}

enum SearchType {
	DATE,
	SRC,
	DEST
}

interface ISearchStrategy {
	public List<Flight> search(String keyword, Map<String, Flight> flightMap);
}

class searchBySRC implements ISearchStrategy {

	@Override
	public List<Flight> search(String keyword, Map<String, Flight> flightMap) {
		// TODO Auto-generated method stub
		List<Flight> result = new ArrayList<Flight>();
		for (Entry<String, Flight> entry : flightMap.entrySet()) {
			if (entry.getValue().getSrc().contains(keyword)) {
				result.add(entry.getValue());
			}
		}
		return result;
	}
	
}

class SearchTypeFactory {
	public static ISearchStrategy getSearchType(SearchType searchType) {
		switch(searchType) {
		case SRC :
			return new searchBySRC();
		case DEST:
		case DATE : 
		}
		return null;
	}
}

class Customer {
	private String customerID;
	private String email;
	private int age;
	private String mobileNo;
	private Boolean isAdmin;
	public Customer(String customerID, String email, int age, String mobileNo, Boolean isAdmin) {
		super();
		this.customerID = customerID;
		this.email = email;
		this.age = age;
		this.mobileNo = mobileNo;
		this.isAdmin = isAdmin;
	}
	public void notify(String message) {
		// TODO Auto-generated method stub
		System.out.println(this.toString() + " is notified. Message is : " + message);
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public Boolean getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
}

class Seat {
	private Boolean isBooked;
	private String seatID;
	private Double price;
	private SeatType type;
	private Customer customer;
	public Seat(Boolean isBooked, String seatID, Double price, SeatType type, Customer customer) {
		super();
		this.isBooked = isBooked;
		this.seatID = seatID;
		this.price = price;
		this.type = type;
		this.customer = customer;
	}
	public Boolean getIsBooked() {
		return isBooked;
	}
	public void setIsBooked(Boolean isBooked) {
		this.isBooked = isBooked;
	}
	public String getSeatID() {
		return seatID;
	}
	public void setSeatID(String seatID) {
		this.seatID = seatID;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public SeatType getType() {
		return type;
	}
	public void setType(SeatType type) {
		this.type = type;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}

class FlightManager {
	private static FlightManager instance;
	private Map<String, Flight> flightMap;
	private FlightManager() {
		this.flightMap = new HashMap<String, Flight>();
	}
	public void cancelFlight(Flight flight) {
		// TODO Auto-generated method stub
		flight.setStatus(FlightStatus.CANCELLED);
	}
	public static FlightManager getInstance() {
		if (instance == null) {
			synchronized(FlightManager.class) {
				if (instance == null) {
					instance = new FlightManager();
				}
			}
		}
		return instance;
	}
	
	public void addFlight(Flight flight) {
		flightMap.putIfAbsent(flight.getFlightID(), flight);
	}
	public Map<String, Flight> getFlightMap() {
		return flightMap;
	}
	public void setFlightMap(Map<String, Flight> flightMap) {
		this.flightMap = flightMap;
	}
	public List<Flight> getFlightsByID(List<String> flightIDs) {
		List<Flight> flights = new ArrayList<Flight>();
		for (String ID : flightIDs) {
			if (flightMap.containsKey(ID)) {
				flights.add(flightMap.get(ID));
			}
		}
		return flights;
	}
	
}

enum FlightStatus {
	CANCELLED,
	SCHEDULED,
	POSTPONED,
	DELAYED
}

class Flight {
	private String flightID;
	private String src;
	private String dest;
	private LocalDateTime departureDateTime;
	private LocalDateTime arrivalDateTime;
	private String aircraftID;
	private String providerID;
	private FlightStatus status;
	private Map<String, Seat> seatMap;
	public Flight(String flightID, String src, String dest, LocalDateTime departureDateTime,
			LocalDateTime arrivalDateTime, String aircraftID, String providerID, Map<String, Seat> seatMap) {
		super();
		this.flightID = flightID;
		this.src = src;
		this.dest = dest;
		this.departureDateTime = departureDateTime;
		this.arrivalDateTime = arrivalDateTime;
		this.aircraftID = aircraftID;
		this.providerID = providerID;
		this.seatMap = seatMap;
	}
	public void bookSeat(List<Pair> list) {
		// TODO Auto-generated method stub
		for (Pair entry : list) {
			Seat seat = seatMap.get(entry.first);
			seat.setCustomer(entry.second);
			seat.setIsBooked(true);
		}
		
	}
	public String getFlightID() {
		return flightID;
	}
	public void setFlightID(String flightID) {
		this.flightID = flightID;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public LocalDateTime getDepartureDateTime() {
		return departureDateTime;
	}
	public void setDepartureDateTime(LocalDateTime departureDateTime) {
		this.departureDateTime = departureDateTime;
	}
	public LocalDateTime getArrivalDateTime() {
		return arrivalDateTime;
	}
	public void setArrivalDateTime(LocalDateTime arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}
	public String getAircraftID() {
		return aircraftID;
	}
	public void setAircraftID(String aircraftID) {
		this.aircraftID = aircraftID;
	}
	public String getProviderID() {
		return providerID;
	}
	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}
	public Map<String, Seat> getSeatMap() {
		return seatMap;
	}
	public void setSeatMap(Map<String, Seat> seatMap) {
		this.seatMap = seatMap;
	}
	
	public FlightStatus getStatus() {
		return status;
	}
	public void setStatus(FlightStatus status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Flight [flightID=" + flightID + ", src=" + src + ", dest=" + dest + ", departureDateTime="
				+ departureDateTime + ", arrivalDateTime=" + arrivalDateTime + ", aircraftID=" + aircraftID
				+ ", providerID=" + providerID + ", seatMap=" + seatMap + "]";
	}
	
}
