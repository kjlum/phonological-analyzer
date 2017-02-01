import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

/*
 * Test the components of PhonologicalAnalyzer
 */
public class PhonologicalAnalyzerTest {
	public HashMap<String, Consonant> consonants;
	public HashMap<String, Vowel> vowels;

	public void init() {
		try {
			consonants = PhonologicalAnalyzer.parseConsonants();
			vowels = PhonologicalAnalyzer.parseVowels();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void basicMinimalPairTest() throws IOException {
		// test basic minimal pair
		ArrayList<String> words = PhonologicalAnalyzer.parseWordFile("minPairTest0.txt");
		String feature1 = "k";
		String feature2 = "x";
		MinimalPair mp = PhonologicalAnalyzer.detectMinimalPair(words, feature1, feature2);
		assertNotNull(mp);
		assertEquals("kano", mp.word1);
		assertEquals("xano", mp.word2);
	}
	
	@Test
	public void nonMinimalPairTest() throws IOException {
		// test near minimal pair
		ArrayList<String> words = PhonologicalAnalyzer.parseWordFile("minPairTest1.txt");
		String feature1 = "p";
		String feature2 = "k";
		MinimalPair mp = PhonologicalAnalyzer.detectMinimalPair(words, feature1, feature2);
		assertNull(mp);
	}
	
	@Test
	public void getEnvironmentsTest() throws IOException {
		// test getting environment
		HashSet<String> prior = new HashSet<String>();
		HashSet<String> post = new HashSet<String>();
		ArrayList<String> words = PhonologicalAnalyzer.parseWordFile("envTest0.txt");
		PhonologicalAnalyzer.getEnvironments(prior, post, words, "p");
		
		assertTrue(prior.contains("#"));
		assertTrue(prior.contains("o"));
		assertTrue(prior.contains("i"));
		assertTrue(prior.contains("a"));
		
		assertTrue(post.contains("#"));
		assertTrue(post.contains("e"));
		assertTrue(post.contains("l"));
		assertTrue(post.contains("a"));
		assertTrue(post.contains("y"));
	}
	
	@Test
	public void similarConsonantTest() {
		init();
		HashSet<String> environment = new HashSet<String>();
		// voiced stops
		environment.add("b");
		environment.add("d");
		environment.add("g");
		Consonant result = PhonologicalAnalyzer.getSimilarConsonant(this.consonants, environment);
		
		assertNull(result.place);
		assertEquals("stop", result.manner);
		assertEquals("voiced", result.voicing);
	}
	
	@Test
	public void notSimilarConsonantTest() {
		init();
		HashSet<String> environment = new HashSet<String>();
		environment.add("b");
		environment.add("d");
		environment.add("f");
		Consonant result = PhonologicalAnalyzer.getSimilarConsonant(this.consonants, environment);
		
		assertNull(result);
	}
	
	@Test
	public void similarVowelTest() {
		init();
		HashSet<String> environment = new HashSet<String>();
		// back rounded
		environment.add("ɔ");
		environment.add("o");
		environment.add("u");
		Vowel result = PhonologicalAnalyzer.getSimilarVowel(this.vowels, environment);
		
		assertNull(result.height);
		assertNull(result.tenseness);
		assertEquals("back", result.backness);
		assertEquals("rounded", result.roundness);
	}
	
	@Test
	public void notSimilarVowelTest() {
		init();
		HashSet<String> environment = new HashSet<String>();
		environment.add("a");
		environment.add("o");
		environment.add("e");
		Vowel result = PhonologicalAnalyzer.getSimilarVowel(this.vowels, environment);
		
		assertNull(result);
	}
	
	@Test
	public void wordBoundaryTest() {
		HashSet<String> environment = new HashSet<String>();
		environment.add("i");
		environment.add("f");
		
		assertFalse(PhonologicalAnalyzer.containsWordBoundary(environment));
		
		environment.add("#");
		assertTrue(PhonologicalAnalyzer.containsWordBoundary(environment));
	}
	
	@Test
	public void detectEnvironmentOverlapTest() throws IOException {
		init();
		ArrayList<String> words = PhonologicalAnalyzer.parseWordFile("overlapTest.txt");
		Environment e = PhonologicalAnalyzer.detectEnvironmentOverlap(words, "o", "e", this.consonants, this.vowels);
		
		assertNull(e.vowel);
		assertNull(e.consonant.place);
		assertEquals("stop", e.consonant.manner);
		assertEquals("voiced", e.consonant.voicing);
	}
	
}
