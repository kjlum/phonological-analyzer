import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

/*
 * Test the components of PhonologicalAnalyzer
 */
public class PhonologicalAnalyzerTest {

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
}
