package model;

public interface IPredictionStations {
	public String getName();

    public int getLength();

    public IPredictionStation get(int i);
    
    public Validation getValidation();
}
