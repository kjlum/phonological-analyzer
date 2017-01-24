/*
 * Represents a single environment.
 */
public class Environment {
	public Consonant consonant;
	public Vowel vowel;
	public boolean priorContainsWordBoundary;
	public boolean postContainsWordBoundary;
	
	public Environment(Consonant c, Vowel v, boolean priorContainsWordBoundary, boolean postContainsWordBoundary) {
		consonant = c;
		vowel = v;
		this.priorContainsWordBoundary = priorContainsWordBoundary;
		this.postContainsWordBoundary = postContainsWordBoundary;
	}
}
