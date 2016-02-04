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
				parseStmt(line);
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
	public static void parseStmt(String line) {
		String[] tokens = line.split(" ");
		int numTokens = tokens.length;
		
		if(numTokens == 3) {
			analyzePrint(tokens);			
		} else if (numTokens == 4) {
			analyzeAssignment(tokens);			
		} else {
			analyzeFor(line);
		}
	}
	
	/**
	* Tokenizes a PRINT statement (ex: PRINT a ;) and prints
	* the specified value or variable to the console.
	*
	* @param tokens a String array of tokens from the PRINT line we are analyzing
	**/
	public static void analyzePrint(String[] tokens) {
		// Print statements have 3 tokens
		// PRINT [varName, number, or string] ;
		String varToPrint = tokens[1];

		
		// if the thing we are printing is a variable, then
		// find its value in the HashMap first
		if(findType(varToPrint) == 2) {
			// If the variable has no value, throw an error
			if(!vars.containsKey(varToPrint)) {
				throwError();
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
	public static void analyzeAssignment(String[] tokens) {
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
				throwError();
			}
			
		}
		
		int newType = findType(newValue);
		
		// If the variable is already declared and we are trying to assign a new
		// value through (+|-|*)=, we need to make sure the types are compatible
		if(vars.containsKey(varName)) {
			int oldType = findType(vars.get(varName));
			if(!stmt.equals("=") && newType != oldType) {
				throwError();
			}
		}	
		
		// check to see what kind of statement we have (+=, -=, *=, =)
		// and perform the appropriate operation, updating newValue
		// with the new value to store
		// TODO: only += works for Strings as well
		switch(stmt) {
			case "+=": 
				if(newType == 0) {
					String string1 = stripQuotes(vars.get(varName));
					String string2 = stripQuotes(newValue);
					// We need to put the quotes back on, since I use quotes as a flag for a String
					newValue = "\"" + string1 + string2 + "\"";
				} else {
					newValue = (Integer.parseInt(vars.get(varName)) + Integer.parseInt(newValue)) + "";
				}							
				break;
			case "-=":
				// This operation is not allowed for Strings
				if(newType == 0) {
					throwError();
				} else {
					newValue = (Integer.parseInt(vars.get(varName)) - Integer.parseInt(newValue)) + "";
				}		
				break;
			case "*=":
				// This operation is not allowed for Strings
				if(newType == 0) {
					throwError();
				} else {
					newValue = (Integer.parseInt(vars.get(varName)) * Integer.parseInt(newValue)) + "";
				}
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
	**/
	public static void analyzeFor(String line) {
		// strip off FOR and ENDFOR
		// remove leading and trailing whitespace, if any
		line = line.trim();
		line = line.substring(4, line.length() - 7);
		
		// Get the number of times the loop should execute
		// this number is now the first token in the line, and
		// we should remove it and the space that follows 
		line = line.trim();
		int spaceAfterNum = line.indexOf(" ");
		int loopCond = Integer.parseInt(line.substring(0, spaceAfterNum));
		line = line.substring(spaceAfterNum);
		
		for(int i = 0; i < loopCond; i++) {
			executeStmtList(line);
		}
	}
	
	/**
	* Splits a statement list into the different statements, and calls
	* parseStmt to execute each statement.
	*
	* @param line the line containing the list of statements to parse and execute
	**/
	public static void executeStmtList(String line) {
		// Build an ArrayList(?) of statements, then 
		ArrayList<String> stmts = new ArrayList<String>();
		while(line.length() > 0) {
			line = line.trim();
			if(line.startsWith("FOR")) {
				// find end of last ENDFOR and add substring to list
				int endfor = line.lastIndexOf("ENDFOR");
				// add the statement from the space after the FOR up until the ENDFOR
				stmts.add(line.substring(0, endfor + "ENDFOR".length()));
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
			parseStmt(s);
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
	
	/**
	* Strips the first and last characters of a string, which in this case
	* are the quotes stored in the HashMap to indicate the element
	* is a String
	*
	* @param str the String off which to strip quotes
	* @return a substring of str without the first and last characters
	**/
	public static String stripQuotes(String str) {
		return str.substring(1, str.length() - 1);
	}
	
	/**
	* Prints a runtime error and exits the program
	**/
	public static void throwError() {
		System.out.println("RUNTIME ERROR: line " + lineNum);
		System.exit(0);
	}

}