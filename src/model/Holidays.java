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

/**
 * Klasse für die Verwaltung der importierten Feriendaten.
 * 
 * @author Sebastian Drath
 *
 */
public class Holidays {
	private static Map<Integer, YearHolidays> holidays;
	
	/**
	 * Es werden importierte Feriendaten hinzugefügt.
	 * 
	 * Die Beschreibung liegt als Text vor. Beginn und Ende von Ferien wird mit Daten im Format dd.MM. angegeben.
	 * Einzelne Tage bzw. Ferienbereiche sind durch "/" getrennt.
	 * Beispiele: "dd.MM.-dd.MM" für Ferienbereiche, "dd.MM." für einzelne Tage oder "dd.MM.-dd.MM./dd.MM." für einen Bereich und einzelnen Tag.
	 * Das Bundesland und der Name dürfen nicht null entsprechen.
	 *
	 * @param year Das Jahr in dem die Ferien liegen
	 * @param state Das Bundesland in dem die Ferien sind
	 * @param name Der Name der Ferien
	 * @param description Die Beschreibung der Daten im oben beschriebenen Format
	 * @return true, wenn die Ferien hinzugefügt wurden, false bei fehlerhaften Parametern
	 */
	public static boolean addHoliday(int year, FederalState state, String name, String description) {
		if(name == null || state == null  || state == FederalState.DEF) return false;
		if(description == null || description.equals("-")) return true; // ist ok, weil in manchen Bundesländern bestimmte Ferien nicht existieren und durch ein - gekennzeichnet sind
		
		// analysiere Beschreibung
		// ggf. mehrere Ferienbereiche pro Beschreibung (bspw Brückentage an zwei getrennten Tagen)
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
				from = df.parse(days[0] + year + "/00:00"); // vom Beginn des ersten Tages
				to = df.parse(days[days.length-1] + year + "/23:59"); // bis zum Ende des letzten Tages
				if(to.before(from)) {
					// Wenn das Ende vor dem Beginn liegt wird die Jahreszahl des Endes inkrementiert (Weihnachtsferien zählen zu dem Jahr, in dem sie beginnen)
					to = df.parse(days[days.length-1] + (year+1) + "/23:59");
					intoNextYear = true;
				}
			} catch (ParseException e) {
				System.err.println("Could not parse date: " + part);
				return false;
			}
			addHoliday(year, state, name, from, to);
			// Wenn die Ferien über 2 Jahre gehen (bspw. Weihnachtsferien) werden sie beiden Jahren zugeordnet
			if(intoNextYear) addHoliday(year+1, state, name, from, to);
		}
		return true;
	}
	
	/**
	 * Bestimmt, ob für an dem Datum in dem Bundesland Ferien sind oder nicht
	 *
	 * @param date das Datum
	 * @param state das Bundesland
	 * @return true, wenn zur gegebenen Zeit in dem Bundesland Ferien sind, false ansonsten
	 */
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
	
	/**
	 * Überprüft, ob die hinzugefügten Daten vollständig sind, d.h. ob für jedes Jahr die Ferien für alle 16 Bundesländer importiert wurden.
	 *
	 * @return true, wenn die Feriendaten vollständig sind, false ansonsten
	 */
	public static boolean checkIntegrity() {
		boolean result = true;
		for(int year: holidays.keySet()) {
			result &= holidays.get(year).checkIntegrity();
		}
		return result;
	}
	
	/**
	 * Fügt einen Ferienbereich hinzu.
	 *
	 * @param year Jahr
	 * @param state Bundesland
	 * @param name Ferienname
	 * @param from Datum des Beginns
	 * @param to Datum des Endes
	 */
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

/**
 * Repräsentiert die Ferien innerhalb eines Jahres für alle Bundesländer.
 *
 * @author Sebastian Drath
 *
 */
class YearHolidays {
	private Map<FederalState, List<Holiday>> allHolidays;
	
	public YearHolidays() {
		allHolidays = new HashMap<>();
	}

	/**
	 * Überprüft ob für jedes Bundesland innerhalb des Jahres Ferien vorliegen.
	 *
	 * @return true, wenn alle Bundesländer importiert wurden, false ansonsten
	 */
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

/**
 * Ein Ferienbereich.
 *
 * @author Sebastian Drath
 *
 */
class Holiday {
	private String name;
	private Date from, to;
	
	/**
	 * Erstellt einen einzelnen Ferienbereich.
	 *
	 * @param name Name der Ferien
	 * @param from Datum des Beginns
	 * @param to Datum des Endes
	 */
	public Holiday(String name, Date from, Date to) {
		this.name = name;
		this.from = from;
		this.to = to;
	}
	
	/**
	 * Gibt Feriennamen zurück.
	 *
	 * @return Ferienname
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gibt zurück, ob sich das Datum innerhalb dieses Ferienbereichs befindet.
	 *
	 * @param date angefordertes Datum
	 * @return true, wenn das Datum innerhalb der Ferien liegt, false ansonsten
	 */
	public boolean isHoliday(Date date) {
		return date.after(from) && date.before(to);
	}
}
