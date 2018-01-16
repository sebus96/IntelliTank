package model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Holidays {
	private static Map<Integer, YearHolidays> holidays;
	
	public static boolean addHoliday(int year, FederalState state, String name, String description) {
		if(name == null) return false;
		if(description == null || description.equals("-") || state == null  || state == FederalState.DEF) return true;
		/*if(state == null || state == FederalState.DEF) {
			System.err.println("Invalid state name DEF");
			return false;
		}*/
		// parse description
		for(String part: description.split("/")) {
			String[] days = part.split("-");
			if(days.length < 1 || days.length > 2) {
				System.err.println("invalid holiday format: " + part);
				return false;
			}
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy/HH:mm");
			Date from, to;
			boolean intoNextYear = false;
			try {
				from = df.parse(days[0] + year + "/00:00");
				to = df.parse(days[days.length-1] + year + "/23:59");
				if(to.before(from)) {
					to = df.parse(days[days.length-1] + (year+1) + "/23:59");
					intoNextYear = true;
				}
			} catch (ParseException e) {
				System.err.println("Could not parse date: " + part);
				return false;
			}
			addHoliday(year, state, name, from, to);
			if(intoNextYear) addHoliday(year+1, state, name, from, to);
		}
		return true;
	}
	
	public static boolean isHoliday(Date date, FederalState state) {
		if(date == null || state == null || state == FederalState.DEF || holidays == null) return false;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		//System.out.println("questioning: " + year);
		if (holidays.containsKey(year)) {
			return holidays.get(year).isHoliday(date, state);
		} else {
			System.err.println("No holiday Data available for " + year);
			return false;
		}
	}
	
	public static boolean checkIntegrity() {
		boolean result = true;
		for(int year: holidays.keySet()) {
			result &= holidays.get(year).checkIntegrity();
		}
		return result;
	}
	
	private static void addHoliday(int year, FederalState state, String name, Date from, Date to) {
		if(holidays == null) {
			holidays = new HashMap<>();
		}
		if(!holidays.containsKey(year)) {
			//System.out.println("Year " + year + " added");
			holidays.put(year, new YearHolidays());
		}
		holidays.get(year).addHoliday(state, name, from, to);
	}
}

class YearHolidays {
	private Map<FederalState, List<Holiday>> allHolidays;
	
	public YearHolidays() {
		allHolidays = new HashMap<>();
	}

	public boolean checkIntegrity() {
		return allHolidays.size() == 16;
	}

	public void addHoliday(FederalState state, String name, Date from, Date to) {
		if(!allHolidays.containsKey(state)) {
			List<Holiday> h = new ArrayList<>();
			allHolidays.put(state, h);
		}
		allHolidays.get(state).add(new Holiday(name, from, to));
	}
	
	public boolean isHoliday(Date date, FederalState state) {
		if(allHolidays.containsKey(state)) {
			for(Holiday h: allHolidays.get(state)) {
				if(h.isHoliday(date)) return true;
			}
			return false;
		} else {
			System.err.println("No holiday Data available for " + state);
			return false;
		}
	}
}

class Holiday {
	private String name;
	private Date from, to;
	
	public Holiday(String name, Date from, Date to) {
		this.name = name;
		this.from = from;
		this.to = to;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isHoliday(Date date) {
//		System.out.println("from: " + from);
//		System.out.println("req: " + date);
//		System.out.println("to: " + to);
		return date.after(from) && date.before(to);
	}
}
