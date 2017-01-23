import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/*
 * Runs a phonological analysis on features of a language based on input words to see
 * if the features are in complementary or contrastive distribution.
 */
public class PhonologicalAnalyzer {
	private static final String CONSONANT_FILE = "consonants.txt";
	private static final String VOWEL_FILE = "vowels.txt";
	private static final String BLANK_ENVIRONMENT = "";
	private static final String INITIALIZED_ENVIRONMENT = "INIT";
	
	/*
	 * Parses the consonants file.
	 * Returns a map of consonants to their linguistic properties.
	 */
	private static HashMap<String, Consonant> parseConsonants() throws IOException {
		System.out.print("Parsing consonants...");
		HashMap<String, Consonant> results = new HashMap<String, Consonant>();
		BufferedReader br = new BufferedReader(new FileReader(CONSONANT_FILE));
		String line;
		while((line = br.readLine()) != null) {
			String[] parts = line.split(",");
			results.put(parts[0], new Consonant(parts[0], parts[1], parts[2], parts[3]));
		}
		br.close();
		System.out.println("done.");
		return results;
	}
	
	/*
	 * Parses the vowels file.
	 * Returns a map of vowels to their linguistic properties.
	 */
	private static HashMap<String, Vowel> parseVowels() throws IOException {
		System.out.print("Parsing consonants...");
		HashMap<String, Vowel> results = new HashMap<String, Vowel>();
		BufferedReader br = new BufferedReader(new FileReader(VOWEL_FILE));
		String line;
		while((line = br.readLine()) != null) {
			String[] parts = line.split(",");
			results.put(parts[0], new Vowel(parts[0], parts[1], parts[2], parts[3], parts[4]));
		}
		br.close();
		System.out.println("done.");
		return results;
	}
	
	/*
	 * Parses the input words to analyze.
	 * Returns a list of words.
	 */
	public static ArrayList<String> parseWordFile(String filename) throws IOException {
		ArrayList<String> words = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while((line = br.readLine()) != null) {
			words.add(line);
		}
		br.close();
		return words;
		
	}
	
	/*
	 * Detects minimal pairs among the provided words, where the pair(s) differ by the provided features.
	 * Returns the first minimal pair found, or null.
	 */
	public static MinimalPair detectMinimalPair(ArrayList<String> words, String feature1,
			String feature2) {
		int diffAllowed = 1;
		for(int i = 0; i < words.size(); i++) {
			String word1 = words.get(i);
			outerloop:
			for(int j = i + 1; j < words.size(); j++) {
				String word2 = words.get(j);
				int diff = 0;
				
				if(word1.length() == word2.length()) {
					for(int k = 0; k < word1.length(); k++) {
						if(word1.charAt(k) != word2.charAt(k)) {
							if(word1.charAt(k) == feature1.charAt(0) && word2.charAt(k) == feature2.charAt(0) || 
									word1.charAt(k) == feature2.charAt(0) && word2.charAt(k) == feature1.charAt(0)) {
								diff++;
								if(diff > diffAllowed) {
									// break to next word2
									continue outerloop;
								}
							} else {
								// break to next word2
								continue outerloop;
							}
						}
					}
					// return true
					return new MinimalPair(word1, word2);
				}
			}
		}
		return null;
	}
	
	/*
	 * Detects environment overlap between two features based on the provided words.
	 * Returns true if overlap is detected, false otherwise.
	 */
	public static boolean detectEnvironmentOverlap(ArrayList<String> words,
			String feature1, String feature2, HashMap<String, Consonant> consonants, HashMap<String, Vowel> vowels) {
		// feature 1
		HashSet<String> prior = new HashSet<String>();
		HashSet<String> post = new HashSet<String>();
		for(int i = 0; i < words.size(); i++) {
			String word = words.get(i);
			while(word.contains(feature1)) {
				int index = word.indexOf(feature1);
				
				if(index - 1 < 0) {
					prior.add("#");
				} else {
					prior.add(word.substring(index - 1, index));
				}
				
				if(index + 1 >= word.length()) {
					post.add("#");
				} else {
					post.add(word.substring(index + 1, index + 2));
				}
				
				word = word.substring(index + 1, word.length());
			}
		}
		
		// feature 2
		for(int i = 0; i < words.size(); i++) {
			String word = words.get(i);
			while(word.contains(feature2)) {
				int index = word.indexOf(feature2);
				
				if(index - 1 < 0) {
					prior.add("#");
				} else {
					prior.add(word.substring(index - 1, index));
				}
				
				if(index + 1 >= word.length()) {
					post.add("#");
				} else {
					post.add(word.substring(index + 1, index + 2));
				}
				
				word = word.substring(index + 1, word.length());
			}
		}
		
		// see if prior environments overlap by placing identical features together in a Consonant and Vowel
		Consonant priorConsonant = new Consonant(BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT);
		Vowel priorVowel = new Vowel(BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT);
		boolean priorContainsWordBoundary = false;
		int nonNullPriorConsonantFeatures = 0;
		int nonNullPriorVowelFeatures = 0;
		for(String feature : prior) {
			if(consonants.keySet().contains(feature)) {
				// feature is a consonant
				Consonant c = consonants.get(feature);
				if(priorConsonant.symbol == BLANK_ENVIRONMENT) {
					priorConsonant = new Consonant(INITIALIZED_ENVIRONMENT, c.place, c.manner, c.voicing);
					nonNullPriorConsonantFeatures = 3;
				} else {
					if(priorConsonant.place != null && priorConsonant.place != c.place) {
						priorConsonant.place = null;
						nonNullPriorConsonantFeatures--;
					}
					if(priorConsonant.manner != null && priorConsonant.manner != c.manner) {
						priorConsonant.manner = null;
						nonNullPriorConsonantFeatures--;
					}
					if(priorConsonant.voicing != null && priorConsonant.voicing != c.voicing) {
						priorConsonant.voicing = null;
						nonNullPriorConsonantFeatures--;
					}
				}
			} else if(vowels.keySet().contains(feature)) {
				// feature is a vowel
				Vowel v = vowels.get(feature);
				if(priorVowel.symbol == BLANK_ENVIRONMENT) {
					priorVowel = new Vowel(INITIALIZED_ENVIRONMENT, v.backness, v.height, v.tenseness, v.roundness);
					nonNullPriorVowelFeatures = 4;
				} else {
					if(priorVowel.backness != null && priorVowel.backness != v.backness) {
						priorVowel.backness = null;
						nonNullPriorVowelFeatures--;
					}
					if(priorVowel.height != null && priorVowel.height != v.height) {
						priorVowel.height = null;
						nonNullPriorVowelFeatures--;
					}
					if(priorVowel.tenseness != null && priorVowel.tenseness != v.tenseness) {
						priorVowel.tenseness = null;
						nonNullPriorVowelFeatures--;
					}
					if(priorVowel.roundness != null && priorVowel.roundness != v.roundness) {
						priorVowel.roundness = null;
						nonNullPriorVowelFeatures--;
					}
					
				}
			} else if(feature.equals("#")) {
				priorContainsWordBoundary = true;
			} else {
				throw new IllegalArgumentException("Invalid character: " + feature);
			}
		}
		
		// see if post environments overlap by placing identical features together in a Consonant and Vowel
		Consonant postConsonant = new Consonant(BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT);
		Vowel postVowel = new Vowel(BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT);
		boolean postContainsWordBoundary = false;
		int nonNullPostConsonantFeatures = 0;
		int nonNullPostVowelFeatures = 0;
		for(String feature : post) {
			if(consonants.keySet().contains(feature)) {
				// feature is a consonant
				Consonant c = consonants.get(feature);
				if(postConsonant.symbol == BLANK_ENVIRONMENT) {
					postConsonant = new Consonant(INITIALIZED_ENVIRONMENT, c.place, c.manner, c.voicing);
					nonNullPostConsonantFeatures = 3;
				} else {
					if(postConsonant.place != null && postConsonant.place != c.place) {
						postConsonant.place = null;
						nonNullPostConsonantFeatures--;
					}
					if(postConsonant.manner != null && postConsonant.manner != c.manner) {
						postConsonant.manner = null;
						nonNullPostConsonantFeatures--;
					}
					if(postConsonant.voicing != null && postConsonant.voicing != c.voicing) {
						postConsonant.voicing = null;
						nonNullPostConsonantFeatures--;
					}
				}
			} else if(vowels.keySet().contains(feature)) {
				// feature is a vowel
				Vowel v = vowels.get(feature);
				if(postVowel.symbol == BLANK_ENVIRONMENT) {
					postVowel = new Vowel(INITIALIZED_ENVIRONMENT, v.backness, v.height, v.tenseness, v.roundness);
					nonNullPostVowelFeatures = 4;
				} else {
					if(postVowel.backness != null && postVowel.backness != v.backness) {
						postVowel.backness = null;
						nonNullPostVowelFeatures--;
					}
					if(postVowel.height != null && postVowel.height != v.height) {
						postVowel.height = null;
						nonNullPostVowelFeatures--;
					}
					if(postVowel.tenseness != null && postVowel.tenseness != v.tenseness) {
						postVowel.tenseness = null;
						nonNullPostVowelFeatures--;
					}
					if(postVowel.roundness != null && postVowel.roundness != v.roundness) {
						postVowel.roundness = null;
						nonNullPostVowelFeatures--;
					}
				}
			} else if(feature.equals("#")) {
				postContainsWordBoundary = true;
			} else {
				throw new IllegalArgumentException("Invalid character: " + feature);
			}
		}
		
		// overlap in prior?
		if(nonNullPriorConsonantFeatures > 0 && nonNullPostConsonantFeatures > 0) {
			// TODO: word boundary logic; possibly add onlyWordBoundaries check?
			return true;
		}
		// overlap in post?
		if(nonNullPriorVowelFeatures > 0 && nonNullPostVowelFeatures > 0) {
			return true;
		}
		// no overlap
		return false;
	}
	
	/*
	 * Runs our phonological analysis in order.
	 */
	private static void runAnalysis(ArrayList<String> words, String feature1,
			String feature2, HashMap<String, Consonant> consonants,
			HashMap<String, Vowel> vowels) {
		
		// Step 1: if there's a minimal pair, constrasive
		if(detectMinimalPair(words, feature1, feature2) != null) {
			System.out.println("Contrastive distribution, minimal pair exists.");
			return;
		}
		// Step 2: list phonetic environments; see if there is overlap (contrastive)
		if(detectEnvironmentOverlap(words, feature1, feature2, consonants, vowels)) {
			System.out.println("Contrastive distribution, environments overlap.");
		}
		// TODO: 3. complementary; write rule
		
	}

	public static void main(String[] args) throws IOException {
		boolean commandLine = false;
		String filename = "";
		String feature1 = "";
		String feature2 = "";
		
		if(args == null || args.length == 0) {
			Scanner scanner = new Scanner(System.in);
			System.out.print("Filename (file should contain words for analysis): ");
			filename = scanner.next();
			System.out.print("Feature 1: ");
			feature1 = scanner.next();
			System.out.print("Feature 2: ");
			feature2 = scanner.next();
		} else if(args.length == 3){
			commandLine = true;
			filename = args[0];
			feature1 = args[1];
			feature2 = args[2];
		} else {
			System.out.println("Proper usage is: java program filename feature1 feature2");
			System.exit(0);
		}
		
		System.out.println("Loading IPA...");
		HashMap<String, Consonant> consonants = parseConsonants();
		HashMap<String, Vowel> vowels = parseVowels();
		
		ArrayList<String> words = parseWordFile(filename);
		runAnalysis(words, feature1, feature2, consonants, vowels);		

//		PrintStream out = new PrintStream(System.out, true, "UTF-8");
//	    out.println();
	}

}
