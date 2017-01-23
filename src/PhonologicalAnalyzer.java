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
	
	static class Consonant {
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
	
	static class Vowel {
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
	
	private static ArrayList<String> parseWordFile(String filename) throws IOException {
		ArrayList<String> words = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while((line = br.readLine()) != null) {
			words.add(line);
		}
		br.close();
		return words;
		
	}
	
	private static boolean detectMinimalPair(ArrayList<String> words, String feature1,
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
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean detectEnvironmentOverlap(ArrayList<String> words,
			String feature1, String feature2, HashMap<String, Consonant> consonants, HashMap<String, Vowel> vowels) {
		// feature 1
		HashSet<String> prior1 = new HashSet<String>();
		HashSet<String> post1 = new HashSet<String>();
		for(int i = 0; i < words.size(); i++) {
			String word = words.get(i);
			while(word.contains(feature1)) {
				int index = word.indexOf(feature1);
				
				if(index - 1 < 0) {
					prior1.add("#");
				} else {
					prior1.add(word.substring(index - 1, index));
				}
				
				if(index + 1 >= word.length()) {
					post1.add("#");
				} else {
					post1.add(word.substring(index + 1, index + 2));
				}
				
				word = word.substring(index + 1, word.length());
			}
		}
		
		// feature 2
		HashSet<String> prior2 = new HashSet<String>();
		HashSet<String> post2 = new HashSet<String>();
		for(int i = 0; i < words.size(); i++) {
			String word = words.get(i);
			while(word.contains(feature2)) {
				int index = word.indexOf(feature2);
				
				if(index - 1 < 0) {
					prior2.add("#");
				} else {
					prior2.add(word.substring(index - 1, index));
				}
				
				if(index + 1 >= word.length()) {
					post2.add("#");
				} else {
					post2.add(word.substring(index + 1, index + 2));
				}
				
				word = word.substring(index + 1, word.length());
			}
		}
		
		// see if prior environments overlap by placing identical features together in a Consonant and Vowel
		Consonant priorConsonant = new Consonant(BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT);
		Vowel priorVowel = new Vowel(BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT);
		boolean priorContainsWordBoundary = false;
		for(String feature : prior1) {
			if(consonants.keySet().contains(feature)) {
				// feature is a consonant
				Consonant c = consonants.get(feature);
				if(priorConsonant.symbol == BLANK_ENVIRONMENT) {
					priorConsonant = new Consonant(INITIALIZED_ENVIRONMENT, c.place, c.manner, c.voicing);
				} else {
					if(priorConsonant.place != c.place) {
						priorConsonant.place = null;
					}
					if(priorConsonant.manner != c.manner) {
						priorConsonant.manner = null;
					}
					if(priorConsonant.voicing != c.voicing) {
						priorConsonant.voicing = null;
					}
				}
			} else if(vowels.keySet().contains(feature)) {
				// feature is a vowel
				Vowel v = vowels.get(feature);
				if(priorVowel.symbol == BLANK_ENVIRONMENT) {
					priorVowel = new Vowel(INITIALIZED_ENVIRONMENT, v.backness, v.height, v.tenseness, v.roundness);
				} else {
					if(priorVowel.backness != v.backness) {
						// different placement
						priorVowel.backness = null;
					}
					if(priorVowel.height != v.height) {
						priorVowel.height = null;
					}
					if(priorVowel.tenseness != v.tenseness) {
						priorVowel.tenseness = null;
					}
					if(priorVowel.roundness != v.roundness) {
						priorVowel.roundness = null;
					}
					
				}
			} else if(feature.equals("#")) {
				priorContainsWordBoundary = true;
			} else {
				// TODO: INVALID CHARACTER
			}
		}
		//TODO: prior2
		
		// see if post environments overlap by placing identical features together in a Consonant and Vowel
		Consonant postConsonant = new Consonant(BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT);
		Vowel postVowel = new Vowel(BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT, BLANK_ENVIRONMENT);
		boolean postContainsWordBoundary = false;
		for(String feature : prior1) {
			if(consonants.keySet().contains(feature)) {
				// feature is a consonant
				Consonant c = consonants.get(feature);
				if(postConsonant.symbol == BLANK_ENVIRONMENT) {
					postConsonant = new Consonant(INITIALIZED_ENVIRONMENT, c.place, c.manner, c.voicing);
				} else {
					if(postConsonant.place != c.place) {
						// different placement
						postConsonant.place = null;
					}
					if(postConsonant.manner != c.manner) {
						postConsonant.manner = null;
					}
					if(postConsonant.voicing != c.voicing) {
						postConsonant.voicing = null;
					}
				}
			} else if(vowels.keySet().contains(feature)) {
				// feature is a vowel
				Vowel v = vowels.get(feature);
				if(postVowel.symbol == BLANK_ENVIRONMENT) {
					postVowel = new Vowel(INITIALIZED_ENVIRONMENT, v.backness, v.height, v.tenseness, v.roundness);
				} else {
					if(postVowel.backness != v.backness) {
						// different placement
						postVowel.backness = null;
					}
					if(postVowel.height != v.height) {
						postVowel.height = null;
					}
					if(postVowel.tenseness != v.tenseness) {
						postVowel.tenseness = null;
					}
					if(postVowel.roundness != v.roundness) {
						postVowel.roundness = null;
					}
					
				}
			} else if(feature.equals("#")) {
				postContainsWordBoundary = true;
			} else {
				// TODO: INVALID CHARACTER
			}
		}
		
		return false;
	}
	
	private static void runAnalysis(ArrayList<String> words, String feature1,
			String feature2, HashMap<String, Consonant> consonants,
			HashMap<String, Vowel> vowels) {
		
		// Step 1: if there's a minimal pair, constrasive
		if(detectMinimalPair(words, feature1, feature2)) {
			System.out.println("Contrastive distribution, minimal pair exists.");
			return;
		}
		// TODO: step 2
		// list phonetic environments; 3 environ overlap = contrastive, 4. complementary
		if(detectEnvironmentOverlap(words, feature1, feature2, consonants, vowels)) {
			System.out.println("Contrastive distribution, environments overlap.");
		}
		
		
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
