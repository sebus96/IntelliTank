package model;

/**
 * Darstellung des Kürzels eines deutschen Bundeslandes.
 * 
 * @author Sebastian Drath
 *
 */
public enum FederalState {
	BW, BY, BE, BB, HB, HH, HE, MV, NI, NW, RP, SL, SN, ST, SH, TH, DEF;
	
	/**
	 * Parst den übergebenen Namen eines Bundeslandes und gibt das Bundesland zurück.
	 * 
	 * @param state Name eines Bundeslandes als Text
	 * @return Gibt das Kürzel des übergebenen Bundeslandes zurück,
	 * oder FederalState.DEF, wenn der Name keinem Bundesland zugeordnet werden kann.
	 */
	public static FederalState getFederalState(String state) {
		switch (state) {
        case "Baden-Württemberg":
            return BW;
        case "Bayern":
            return BY;
        case "Berlin":
            return BE;
        case "Brandenburg":
            return BB;
        case "Bremen":
            return HB;
        case "Hamburg":
            return HH;
        case "Hessen":
            return HE;
        case "Mecklenburg-Vorpommern":
            return MV;
        case "Niedersachsen":
            return NI;
        case "Nordrhein-Westfalen":
            return NW;
        case "Rheinland-Pfalz":
            return RP;
        case "Saarland":
            return SL;
        case "Sachsen":
            return SN;
        case "Sachsen-Anhalt":
            return ST;
        case "Schleswig-Holstein":
            return SH;
        case "Thüringen":
            return TH;
        default:
            return DEF;
		}
	}
}
