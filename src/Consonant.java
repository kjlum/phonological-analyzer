/*
 * Represents the linguistic properties of a consonant
 */
public class Consonant {
	public String symbol;
	public String place;
	public String manner;
	public String voicing;
	
	public Consonant(String symbol, String place, String manner, String voicing) {
		this.symbol = symbol;
		this.place = place;
		this.manner = manner;
		this.voicing = voicing;
	}
}
