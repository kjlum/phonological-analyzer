/*
 * Represents the linguistic properties of a vowel
 */
public class Vowel {
	public String symbol;
	public String backness;
	public String height;
	public String tenseness;
	public String roundness;
	
	public Vowel(String symbol, String backness, String height, String tenseness, 
			String roundness) {
		this.symbol = symbol;
		this.backness = backness;
		this.height = height;
		this.tenseness = tenseness;
		this.roundness = roundness;
	}
}
