/*
 * Represents a single environment.
 */
public class Environment {
	public Consonant consonant;
	public Vowel vowel;
	public boolean containsWordBoundary;
	
	public Environment(Consonant c, Vowel v, boolean containsWordBoundary) {
		consonant = c;
		vowel = v;
		this.containsWordBoundary = containsWordBoundary;
	}
}
