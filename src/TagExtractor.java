import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class TagExtractor {

    private Set<String> stopWords;
    private Map<String, Integer> tags;

    public TagExtractor() {
        stopWords = new TreeSet<>();
        tags = new TreeMap<>();
    }

    /**
     * Loads the stop words from a text file.
     * @param file the stop words file
     * @throws IOException if the file cannot be read
     */
    public void loadStopWords(File file) throws IOException {
        stopWords.clear();

        try (Scanner input = new Scanner(file, "UTF-8")) {
            while (input.hasNextLine()) {
                String word = input.nextLine()
                        .toLowerCase(Locale.ROOT)
                        .replaceAll("[^a-z]", "");

                if (!word.isEmpty()) {
                    stopWords.add(word);
                }
            }
        }
    }

    /**
     * Counts keywords while leaving out stop words.
     * @param file the text file to scan
     * @throws IOException if the file cannot be read
     */
    public void extractTags(File file) throws IOException {
        tags.clear();

        try (Scanner input = new Scanner(file, "UTF-8")) {
            while (input.hasNext()) {
                String word = input.next()
                        .toLowerCase(Locale.ROOT)
                        .replaceAll("[^a-z]", "");

                if (!word.isEmpty() && !stopWords.contains(word)) {
                    if (tags.containsKey(word)) {
                        tags.put(word, tags.get(word) + 1);
                    } else {
                        tags.put(word, 1);
                    }
                }
            }
        }
    }

    /**
     * Gets the keywords and their frequencies.
     * @return the keyword map
     */
    public Map<String, Integer> getTags() {
        return tags;
    }

    /**
     * Gets the number of stop words loaded.
     * @return the stop word count
     */
    public int getStopWordCount() {
        return stopWords.size();
    }
}