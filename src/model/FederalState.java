package model;

public enum FederalState {
	BW, BY, BE, BB, HB, HH, HE, MV, NI, NW, RP, SL, SN, ST, SH, TH, DEF;
	
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
