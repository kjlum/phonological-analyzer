import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Runs a phonological analysis on features of a language based on input words to see
 * if the features are in complementary or contrastive distribution.
 */
public class PhonologicalAnalyzer {
	private static final String CONSONANT_FILE = "consonants.txt";
	private static final String VOWEL_FILE = "vowels.txt";
	
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
	
	private static ArrayList<Consonant> parseConsonants() throws IOException {
		System.out.print("Parsing consonants...");
		ArrayList<Consonant> results = new ArrayList<Consonant>();
		BufferedReader br = new BufferedReader(new FileReader(CONSONANT_FILE));
		String line;
		while((line = br.readLine()) != null) {
			String[] parts = line.split(",");
			results.add(new Consonant(parts[0], parts[1], parts[2], parts[3]));
		}
		br.close();
		System.out.println("done.");
		return results;
	}
	
	private static ArrayList<Vowel> parseVowels() throws IOException {
		System.out.print("Parsing consonants...");
		ArrayList<Vowel> results = new ArrayList<Vowel>();
		BufferedReader br = new BufferedReader(new FileReader(VOWEL_FILE));
		String line;
		while((line = br.readLine()) != null) {
			String[] parts = line.split(",");
			results.add(new Vowel(parts[0], parts[1], parts[2], parts[3], parts[4]));
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
	
	private static void runAnalysis(ArrayList<String> words, String feature1,
			String feature2, ArrayList<Consonant> consonants,
			ArrayList<Vowel> vowels) {
		
		// Step 1: if there's a minimal pair, constrasive
		if(detectMinimalPair(words, feature1, feature2)) {
			System.out.println("Contrastive distribution, minimal pair exists.");
			return;
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
		ArrayList<Consonant> consonants = parseConsonants();
		ArrayList<Vowel> vowels = parseVowels();
		
		ArrayList<String> words = parseWordFile(filename);
		runAnalysis(words, feature1, feature2, consonants, vowels);		

//		PrintStream out = new PrintStream(System.out, true, "UTF-8");
//	    out.println();
	}

}
