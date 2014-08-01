package swen302.testprograms;

public class StringParser {


	public StringParser(String line){
		for(char c : line.toCharArray()){
			//currentC = c;
			switch(c){
			case 'a':
				methodA(c);
				break;
			case 'b':
				methodB(c);
				break;
			case 'c':
				methodC(c);
				break;
			case 'd':
				methodD(c);
				break;
			case 'e':
				methodE(c);
				break;
			case 'f':
				methodF(c);
				break;

			}

		}
	}


	public static void main(String[] args){
		new StringParser(args[0]);
	}


	public void methodA(char p){
		System.out.println(p);
	}

	public void methodB(char p){
		System.out.println(p);
	}

	public void methodC(char p){
		System.out.println(p);
	}

	public void methodD(char p){
		System.out.println(p);
	}

	public void methodE(char p){
		System.out.println(p);
	}

	public void methodF(char p){
		System.out.println(p);
	}
}
