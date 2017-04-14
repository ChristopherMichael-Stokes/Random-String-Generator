package randomstrings;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

/**
 *
 * @author Christopher
 */
public class RandomStrings {
    
    
    private final JFrame frame;
    private final JPanel pane;
    private String[] inputStrings;
    private static final String[] OPTIONS = new String[]{
                "Random uppercase and lower case letters",
                    "Random words","Random words and numbers",
                    "Random words with punctuation and numbers"};
    private final List<String> dictionary;
    
    private void randomWords(Random rn, int strings){
        inputStrings = new String[strings];        
        
        for (int i = 0; i < inputStrings.length; i++){
            try (IntStream ints = rn.ints(rn.nextInt(1)+2,0,dictionary.size())){
                inputStrings[i] = ints.parallel()
                        .mapToObj(dictionary::get)
                        .map(String::toLowerCase)
                        .collect(Collectors.joining(""));
//                        .reduce("", String::concat);
            }
        }
        
    }
    
    private void randomLetters(Random rn, int strings){
        inputStrings = new String[strings];        
        
        for (int j = 0; j < inputStrings.length; j++) {
            try (IntStream ints = rn.ints(rn.nextInt(11) + 4, 0, 26)) {
                inputStrings[j] = ints.parallel()
                        .map(i -> i + (rn.nextFloat() > 0.5f ? 97 : 65))
                        .collect(StringBuilder::new,
                                StringBuilder::appendCodePoint,
                                StringBuilder::append)
                        .toString();
            }
            
        }
        
        Arrays.sort(inputStrings);
//        System.out.println(Arrays.toString(inputStrings));
    }
    
    public RandomStrings(List<String> dictionary){
        this.dictionary = dictionary;
        
        //setup swing components
        frame = new JFrame("String Generator");
        frame.setResizable(false);        
        
        pane = new JPanel();
        
        frame.add(pane);        
        frame.pack();      
        
        frame.setFocusable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAutoRequestFocus(true);
        frame.setLocationRelativeTo(null);
        
        frame.setVisible(false);        
    }

    public static int enumerateInput(int num, String input){
        if (input.equals(OPTIONS[num])) return num;
        else return enumerateInput(++num,input);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        List<String> dictionary = new ArrayList<>();
        
        try {
            String[] tmp = new String(readAllBytes(get("data/words.txt")))
                    .trim().split("\n");
            
            dictionary = Arrays.stream(tmp)
                    .parallel()
                    .filter(s -> s.length() > 4)
                    .collect(Collectors.toCollection(ArrayList<String>::new));
        } catch (java.io.IOException ex){
            System.out.println(ex);
        }
        
        System.out.println(dictionary.size());
        RandomStrings rs = new RandomStrings(dictionary);
        
        int optionInput;
        do {                        
            Object selection = JOptionPane.showInputDialog(
                    rs.pane, "Select a password style", "Select Style",
                    JOptionPane.QUESTION_MESSAGE, null,
                    OPTIONS, null);           
            
            if (selection == null) System.exit(0);
            int selectionIndex = enumerateInput(0, (String)selection);
            
            String input;
            do {
                input = JOptionPane.showInputDialog(rs.pane, 
                        "Please enter a seed for the random number generation",
                        "Set Seed", JOptionPane.QUESTION_MESSAGE);
                if (input == null) System.exit(0);
            } while (!input.matches("[0-9]+")&&!input.equals(""));
            
            Random rn = new Random();
            if (!input.equals("")) {
                rn.setSeed(Integer.parseInt(input));
            }
            int numStrings = 10;
            
            switch(selectionIndex){
                case 0 : 
                    rs.randomLetters(rn, numStrings);
                    break;
                case 1 : 
                    rs.randomWords(rn, numStrings);
                    break;
                case 2 : 
                    
                    break;
                case 3 : 
                    
                    break;
                default : System.exit(0);
            }           

            String strings = "";
            for (String string : rs.inputStrings){
                strings += string+"\n";
            }

            optionInput = JOptionPane.showOptionDialog(rs.pane, strings, 
                    "Random Strings", -1,
                    JOptionPane.INFORMATION_MESSAGE, null, null, null);

            if (optionInput == JOptionPane.CLOSED_OPTION) System.exit(0);
            
        } while (optionInput == JOptionPane.OK_OPTION);
    }    
}
