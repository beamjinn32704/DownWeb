
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class CodeGenerator {
    //Alphabet:
    //65 - 90 & 97 - 122
    
    //Digits:
    //48 - 57
    private ArrayList<Integer> charNums;
    private ArrayList<ArrayList<Integer>> skipStates = new ArrayList<>();
    private int digits;
    private static int UPPER_START = 48;
    private static int UPPER_END = 57;
    
//    private static int LOWER_START = 97;
//    private static int LOWER_END = 122;
    
    public CodeGenerator(int tmpDigits){
        digits = tmpDigits;
        resetDigitNums();
    }
    
    public CodeGenerator(String state){
        charNums = new ArrayList<>();
        setCurrentState(state);
    }
    
    private void resetDigitNums(){
        charNums = new ArrayList<>();
        
        for(int i = 0; i < digits; i++){
            charNums.add(UPPER_START);
        }
    }
    
    public void addSkipState(ArrayList<Integer> state){
        if(compareStateTo(state) < 0){
            return;
        }
        
        int indexOf = Util.binaryIndexOf(skipStates, state, (ArrayList<Integer> o1,
                ArrayList<Integer> o2) -> CodeGenerator.compare(o1, o2));
        if(indexOf < 0){
            indexOf = -1 * (indexOf + 1);
            skipStates.add(indexOf, state);
        }
    }

    public void setCharNums(ArrayList<Integer> charNums) {
        this.charNums = charNums;
        evaluateSkipStates();
    }
    
    private void evaluateSkipStates(){
        if(skipStates.isEmpty()){
            return;
        }
        ArrayList<ArrayList<Integer>> tempSkipStates = new ArrayList<>();
        for(ArrayList<Integer> list : skipStates){
            if(compareStateTo(list) < 0){
                
            } else {
                tempSkipStates.add(list);
            }
        }
        skipStates = tempSkipStates;
    }
    
    public void setCurrentState(String state){
        ArrayList<Integer> tempCharNums = getCharState(state);
        if(!tempCharNums.isEmpty()){
            digits = tempCharNums.size();
            setCharNums(tempCharNums);
        }
    }
    
    public static ArrayList<Integer> getCharState(String state){
        ArrayList<Integer> charInts = new ArrayList<>();
        for(int i = 0; i < state.length(); i++){
            char c = state.charAt(i);
            if(c >= UPPER_START && c <= UPPER_END){
                charInts.add((int)c);
            }
        }
        return charInts;
    }
    
    private void nextState(){
        int first = charNums.size() - 1;
        Integer in = charNums.get(first);
        in++;
        charNums.set(first, in);
        
        boolean reset = false;
        for(int i = first; i >= 0; i--){
            Integer charNum = charNums.get(i);
            if(charNum > UPPER_END){
                charNum = UPPER_START;
                if(i != 0){
                    charNums.set(i-1, charNums.get(i-1)+1);
                } else {
                    reset = true;
                }
            } else {
                if(charNum < UPPER_START) {
                    charNum = UPPER_START;
                }
            }
            charNums.set(i, charNum);
        }
        if(reset){
            digits++;
            resetDigitNums();
        }
    }
    
    public static String evaluateState(ArrayList<Integer> state){
        String stateString = "";
        int first = state.size() - 1;
        for(int i = first; i >= 0; i--){
            stateString = (char)state.get(i).intValue() + stateString;
        }
        return stateString;
    }
    
    public static boolean acceptable(String string){
        return evaluateState(getCharState(string)).equals(string);
    }
    
    public String next(){
        boolean goSkipping = !skipStates.isEmpty();
        while(goSkipping){
            if(compareStateTo(skipStates.get(0)) == 0){
                skipStates.remove(0);
                nextState();
                goSkipping = !skipStates.isEmpty();
            } else {
                goSkipping = false;
            }
        }
        
        String suff = evaluateState(charNums);
        
        nextState();
        
        return suff;
    }
    
    public static int compare(ArrayList<Integer> state1, ArrayList<Integer> state2){
        if(state1.size() != state2.size()){
            return state1.size() - state2.size();
        }
        for(int i = 0; i < state1.size(); i++){
            int digit1 = state1.get(i);
            int digit2 = state2.get(i);
            if(digit1 != digit2){
                return digit1 - digit2;
            }
        }
        return 0;
    }
    
    public int compareStateTo(ArrayList<Integer> otherState){
        return compare(charNums, otherState);
    }
    
    public static CodeGenerator createCodeGeneratorBasedOnFileNames(File dir, String startState, FileFilter fileFilter){
        CodeGenerator codeGenerator = new CodeGenerator(startState);
        File[] files = dir.listFiles(fileFilter);
        if(files.length == 0){
            return codeGenerator;
        }
        for(File folder : files){
            String fileName = folder.getName();
            ArrayList<Integer> charState = CodeGenerator.getCharState(fileName);
            if(CodeGenerator.evaluateState(charState).equals(fileName)){
                codeGenerator.addSkipState(charState);
            }
        }
        return codeGenerator;
    }
}
