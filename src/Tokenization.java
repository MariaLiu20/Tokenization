import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.Map;

public class Tokenization {
    public static void main(String[] args) throws IOException {
        Path pathA = FileSystems.getDefault().getPath("tokenization-input-part-A.txt");
        Stream<String> linesA = Files.lines(pathA);
        FileWriter tokenized = new FileWriter("tokenized.txt");
        PrintWriter pwA = new PrintWriter(tokenized);
        ArrayList<String> resultA = new ArrayList<>();
        // Read stopwords into a set
        Path pathStop = FileSystems.getDefault().getPath("stopwords.txt");
        Stream<String> linesStop = Files.lines(pathStop);
        Set<String> stopwords = new HashSet<>();
        linesStop.forEach(stopwords::add);
        // Tokenizer, stopword removal, stemmer
        linesA.forEach(s -> splitStop(abbreviate(s.toLowerCase()).replace("'", ""), stopwords, resultA));
        for (String tok : resultA) {
            tok = step1b(step1a(tok));
            pwA.println(tok);
        }
        tokenized.close();
        // Part B
        Path pathB = FileSystems.getDefault().getPath("tokenization-input-part-B.txt");
        Stream<String> linesB = Files.lines(pathB);
        FileWriter terms = new FileWriter("terms.txt");
        PrintWriter pwB = new PrintWriter(terms);
        ArrayList<String> resultB = new ArrayList<>();
        linesB.forEach(s -> splitStop(abbreviate(s.toLowerCase()).replace("'", ""), stopwords, resultB));
        ArrayList<Integer> vocab = new ArrayList<>();
        vocab.add(0);
        ArrayList<Integer> collection = new ArrayList<>();
        collection.add(0);
        Map<String, Integer> topTerms = new HashMap<>();
        FileWriter graphWriter = new FileWriter("graphData.csv");
        PrintWriter pwData = new PrintWriter(graphWriter);
        int collectionCount = 0;
        // Calculate frequency of all tokenized words & put into HashMap
        for (String tok : resultB) {
            tok = step1b(step1a(tok));
            if (topTerms.containsKey(tok))
                topTerms.put(tok, topTerms.get(tok) + 1);
            else
                topTerms.put(tok, 1);
            collectionCount++;
            graphWriter.write(collectionCount + " " + topTerms.size() + "\n");
        }
        graphWriter.close();
        // Get first k frequent terms and put it into a priority queue
        PriorityQueue<Map.Entry<String, Integer>> result = new PriorityQueue<>(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()));
        for (Map.Entry<String,Integer> entry : topTerms.entrySet()) {
            result.add(new AbstractMap.SimpleEntry<String, Integer>(entry.getKey(), entry.getValue()));
            if (result.size() > 300)
                result.poll();
        }
        // Add frequent terms into an ArrayList and sort by descending value
        ArrayList<Map.Entry<String, Integer>> scores = new ArrayList<>();
        scores.addAll(result);
        scores.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()));
        //Write term & frequency into file
        for (Map.Entry<String, Integer> entry : scores) {
            pwData.println(entry.getKey() + "," + entry.getValue());
        }
        pwData.close();
    }
    public static String abbreviate(String s) {
        Pattern p = Pattern.compile("\\b(?:[a-zA-Z]\\.){2,}" );
        Matcher m = p.matcher(s);
        while (m.find()) {
            s = s.replace(m.group(), m.group().replace(".", ""));
        }
        return s;
    }
    public static void splitStop(String s, Set<String> stopwords, ArrayList<String> result) {
        String[] tokens = s.split("\\p{Punct}| ");
        for (String tok : tokens)
            if (!stopwords.contains(tok) && tok.length() > 0)
                result.add(tok);
    }
    public static String step1a(String tok) {
        if (tok.endsWith("sses")) {
            tok = tok.substring(0, tok.length()-2);
        }
        else if (tok.endsWith("ied") || tok.endsWith("ies")) {
            tok = tok.substring(0, tok.length()-3);
            if (tok.length() == 1) {
                tok += "ie";
            }
            else {
                tok += "i";
            }
        }
        else if (tok.endsWith("ss") || tok.endsWith("us")) {
        }
        else if (tok.endsWith("s")) {
            if (tok.length() > 2) {
                String stem = tok.substring(0, tok.length() - 2);
                if (stem.contains("a") || stem.contains("e") || stem.contains("i") ||
                        stem.contains("o") || stem.contains("u")) {
                    tok = tok.substring(0, tok.length() - 1);
                }
            }
        }
        return tok;
    }
    public static String step1b(String tok) {
        String vowels = "aeiou";
        int end = tok.length();
        if (tok.endsWith("eedly") || tok.endsWith("eed")) {
            if (tok.endsWith("eedly")) {
                end -= 5;
            }
            else {
                end -= 3;
            }
            for (int i = 0; i < end; i++) {
                if (vowels.contains(tok.substring(i, i+1)) && !vowels.contains(tok.substring(i+1, i+2))) {
                    tok = tok.substring(0, end);
                    tok += "ee";
                    return tok;
                }
            }
        }
        else if (tok.endsWith("ed") || tok.endsWith("edly") || tok.endsWith("ing") || tok.endsWith("ingly")) {
            if (tok.endsWith("ed"))
                end -= 2;
            else if (tok.endsWith("edly"))
                end -= 4;
            else if (tok.endsWith("ing"))
                end -= 3;
            else
                end -= 5;
            String stem = tok.substring(0, end);
            if (stem.contains("a") || stem.contains("e") || stem.contains("i") ||
                    stem.contains("o") || stem.contains("u")) {
                tok = stem;
                if (tok.endsWith("at") || tok.endsWith("bl") || tok.endsWith("iz")) {
                    tok += "e";
                }
                else if (tok.length() > 1 && tok.charAt(tok.length()-1) == tok.charAt(tok.length()-2)) {
                    char lastChar = tok.charAt(tok.length()-1);
                    if (lastChar != 'l' && lastChar != 's' && lastChar != 'z') {
                        tok = tok.substring(0, tok.length()-1);
                    }
                }
                else if (tok.length() <= 3) {
                    tok += "e";
                }
            }
        }
        return tok;
    }
    public String convertToCSV(String[] data) {
        return Stream.of(data).collect(Collectors.joining(","));
    }
}
