package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse, die die Zuordnung von Postleitzahlen zu Bundesländern verwaltet.
 * 
 * Einzelne Bereiche von Postleitzahlen können eindeutig zu Bundesländern zugeordnet werden.
 *
 * @author Sebastian Drath
 *
 */
public class Postalcodes {
    private static List<PostcodeRange> post2state;
    
    /**
     * Gibt zurück, ob die Zuordnung importiert ist und eine Abfrage gestartet werden kann.
     *
     * @return true, wenn eine Zuordnung importiert ist, false ansonsten
     */
    public static boolean isImported() {
    	return post2state != null && !post2state.isEmpty();
    }
    
    /**
     * Fügt einen Postleitzahlenbereich für ein Bundesland hinzu.
     *
     * @param lower untere Grenze der Postleitzahlen
     * @param upper obere Grenze der Postleitzahlen
     * @param state Bundesland
     */
    public static void addPostcodeRange(int lower, int upper, String state) {
    	if(post2state == null) {
    		post2state = new ArrayList<>();
    	}
    	post2state.add(new PostcodeRange(lower, upper, state));
    }

    /**
     * Gibt das Bundesland für eine Postleitzahl zurück.
     *
     * @param postalCode gesuchte Postleitzahl
     * @return Bundesland, oder null wenn die Postleitzahl ungültig ist bzw. keine Zuordnung vorhanden ist
     */
    public static FederalState getState(int postalCode) {
    	if(post2state == null) return null;
        for (PostcodeRange pc : post2state) {
            if (pc.isInArea(postalCode)) {
                return pc.getState();
            }
        }
        return null;
    }
}

/**
 * Repräsentation eines Postleitzahlenbereichs.
 *
 * @author Sebastian Drath
 *
 */
class PostcodeRange {

    private int upper, lower;
    private String state;

    /**
     * Erstellt einen neuen Postleitzahlenbereich.
     *
     * @param lower untere Grenze der Postleitzahlen
     * @param upper obere Grenze der Postleitzahlen
     * @param state Bundesland
     */
    public PostcodeRange(int lower, int upper, String state) {
        this.upper = upper;
        this.lower = lower;
        this.state = state;
    }

    /**
     * Gibt zurück, ob eine Postleitzahl innerhalb des Bereichs liegt und dementsprechend zu dem Bundesland dieses Bereichs gehört.
     *
     * @param postcode Postleitzahl
     * @return true, wenn die Postleitzahl innerhalb des Bereichs liegt, false ansonsten
     */
    public boolean isInArea(int postcode) {
        return postcode <= upper && postcode >= lower;
    }

    /**
     * Gibt das Bundesland dieses Bereichs zurück.
     *
     * @return Bundesland
     */
    public FederalState getState() {
        return FederalState.getFederalState(this.state);
    }
}