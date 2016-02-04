import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;

public class Main {
	
	public static HashMap<String, String> vars = new HashMap<String, String>();
	public static int lineNum = 0;
	
	public static void main(String[] args) {
		try {
			// Read in lines from Z+- program file
			String filename = args[0];
		    BufferedReader br = new BufferedReader(new FileReader(filename));

			// read each line, one at a time, and interpret it
		    String line;
		    while((line = br.readLine()) != null) {
				lineNum++;
				parseStmt(line, lineNum);
		    }
			
		}catch (Exception e) {
			System.out.println(e);
		}
		
		
	}
	
	/**
	* Tokenizes a single line of source code, decides what kind of
	* statement it is, and calls the appropriate method
	* @param line the String of source code to analyze 
	**/
	public static void parseStmt(String line, int lineNum) {
		String[] tokens = line.split(" ");
		int numTokens = tokens.length;
		
		if(numTokens == 3) {
			analyzePrint(tokens, lineNum);			
		} else if (numTokens == 4) {
			analyzeAssignment(tokens, lineNum);			
		} else {
			analyzeFor(line, lineNum);
		}
	}
	
	/**
	* Tokenizes a PRINT statement (ex: PRINT a ;) and prints
	* the specified value or variable to the console.
	*
	* @param tokens a String array of tokens from the PRINT line we are analyzing
	* @param lineNum the line number of the line we are analyzing
	**/
	public static void analyzePrint(String[] tokens, int lineNum) {
		// Print statements have 3 tokens
		// PRINT [varName, number, or string] ;
		String varToPrint = tokens[1];

		
		// if the thing we are printing is a variable, then
		// find its value in the HashMap first
		if(findType(varToPrint) == 2) {
			// If the variable has no value, throw an error
			if(!vars.containsKey(varToPrint)) {
				System.out.println("RUNTIME ERROR: line " + lineNum);
				System.exit(0);
			} else {
				varToPrint = vars.get(varToPrint);
			}
		}
		
		// If the variable we wish to print is a String, we need to
		// remove the quotes
		// Note: If we found the type above, it may have changed
		// if this was a variable that stored a string, which is why
		// I call findType again
		if (findType(varToPrint) == 0) {
			varToPrint = stripQuotes(varToPrint);
		}		
		
		System.out.println(varToPrint);
	}
	
	/**
	* Tokenizes an assignment statement [=, +=, -=, or *=] and
	* creates or updates the appropriate variable in the HashMap of variables
	*
	* @param tokens a String array of tokens from the assignment statement under analysis
	**/
	public static void analyzeAssignment(String[] tokens, int lineNum) {
		// assignment statements
		// varName (+|-|*)= newValue ;
		String varName = tokens[0];
		String stmt = tokens[1];
		String newValue = tokens[2];
		
		// if the new value is a variable, lookup its value
		if(findType(newValue) == 2) {
			// Throw a runtime error if the variable does not exist
			if(vars.containsKey(newValue)) {
				newValue = vars.get(newValue);
			} else {
				System.out.println("RUNTIME ERROR: line " + lineNum);
				System.exit(0);
			}
			
		}
		
		// check to see what kind of statement we have (+=, -=, *=, =)
		// and perform the appropriate operation
		// TODO: only += works for Strings as well
		switch(stmt) {
			case "+=": 
				if(findType(newValue) == 0) {
					String string1 = stripQuotes(vars.get(varName));
					String string2 = stripQuotes(newValue);
					newValue = string1 + string2;
				} else {
					newValue = (Integer.parseInt(vars.get(varName)) + Integer.parseInt(newValue)) + "";
				}							
				break;
			case "-=":
				newValue = (Integer.parseInt(vars.get(varName)) - Integer.parseInt(newValue)) + "";
				break;
			case "*=":
				newValue = (Integer.parseInt(vars.get(varName)) * Integer.parseInt(newValue)) + "";
				break;
			default:
		}
		// update the HashMap with the new value for the variable
		vars.put(varName, newValue);
	}	
	
	/**
	* Tokenizes a FOR loop statement and completes the appropriate action
	* 
	* @param line the String containing the FOR statement
	* @param lineNum the line number of the FOR statement
	**/
	public static void analyzeFor(String line, int lineNum) {
		// strip off FOR and ENDFOR
		// remove leading and trailing whitespace, if any
		line = line.trim();
		line = line.substring(4, line.length() - 7);
		
		// Get the number of times the loop should execute
		// this number is now the first character in the line, and
		// we should remove it and the space that follows 
		int loopCond = Integer.parseInt(line.substring(0, 1));
		line = line.substring(2);
		
		for(int i = 0; i < loopCond; i++) {
			executeStmtList(line, lineNum);
		}
	}
	
	/**
	* Splits a statement list into the different statements, and calls
	* parseStmt to execute each statement.
	*
	* @param line the line containing the list of statements to parse and execute
	**/
	public static void executeStmtList(String line, int lineNum) {
		// Build an ArrayList(?) of statements, then 
		ArrayList<String> stmts = new ArrayList<String>();
		while(line.length() > 0) {
			line = line.trim();
			if(line.startsWith("FOR")) {
				// find end of last ENDFOR and add substring to list
				int endfor = line.lastIndexOf("ENDFOR");
				// add the statement from the space after the FOR up until the ENDFOR
				stmts.add(line.substring(3, endfor));
				// we want everything after the ENDFOR
				line = line.substring(endfor + "ENDFOR".length());
			} else {
				int semicolon = line.indexOf(";");
				stmts.add(line.substring(0, semicolon + 1));
				line = line.substring(semicolon + 1);
			}
		}
		// use a for each loop to execute each statement
		for(String s : stmts) {
			parseStmt(s, lineNum);
		}
	}
	
	/**
	* Determines the "type" of a string token and returns
	* 0 for a string, 1 for an int, and 2 for a variable name
	*
	* @param unknown a String that may represent a variable name, int, or String
	* @return 0 for String, 1 for int, 2 for a variable name
	**/
	public static int findType(String unknown) {
		// If the first character is a ", then we have a String
		if(unknown.charAt(0) == '\"') {
			return 0;
		}
		try {
			// If we can successfully parse the String as an int, we have an int
			Integer.parseInt(unknown);
			return 1;
		} catch (Exception e) {
			// If the parsing failed, we have a variable
			return 2;
		}
	}
	
	public static String stripQuotes(String str) {
		return str.substring(1, str.length() - 1);
	}

}