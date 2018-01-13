package model;

import java.util.ArrayList;
import java.util.List;

public class Postalcodes {
    private static List<PostcodeRange> post2state;
    
    public static boolean isImported() {
    	return post2state != null && !post2state.isEmpty();
    }
    
    public static void addPostcodeRange(int lower, int upper, String state) {
    	if(post2state == null) {
    		post2state = new ArrayList<>();
    	}
    	post2state.add(new PostcodeRange(lower, upper, state));
    }

    public static FederalState getState(int postalCode) {
        for (PostcodeRange pc : post2state) {
            if (pc.isInArea(postalCode)) {
                return pc.getState();
            }
        }
        return null;
    }
}

class PostcodeRange {

    private int upper, lower;
    private String state;

    public PostcodeRange(int lower, int upper, String state) {
        this.upper = upper;
        this.lower = lower;
        this.state = state;
    }

    public boolean isInArea(int postcode) {
        return postcode <= upper && postcode >= lower;
    }

    public FederalState getState() {
        return FederalState.getFederalState(this.state);
    }
}