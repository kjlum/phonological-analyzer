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
	
	public void printOverlap() {
		if(consonant != null) {
			System.out.print("Consonant: ");
			if(consonant.place != null) {
				System.out.print(consonant.place + " ");
			}
			if(consonant.manner != null) {
				System.out.print(consonant.manner + " ");
			}
			if(consonant.voicing != null) {
				System.out.print(consonant.voicing + " ");
			}
		} else if(vowel != null) {
			System.out.print("Vowel: ");
			if(vowel.backness != null) {
				System.out.print(vowel.backness + " ");
			}
			if(vowel.height != null) {
				System.out.print(vowel.height + " ");
			}
			if(vowel.tenseness != null) {
				System.out.print(vowel.tenseness + " ");
			}
			if(vowel.roundness != null) {
				System.out.print(vowel.roundness + " ");
			}
		}
	}
}
