// For testing correct output of test source code
import java.util.Scanner;

public class Test {
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter program number: ");
		int choice = in.nextInt();
		
		switch(choice) {
			case 5: prog5(); break;
			case 6: prog6(); break;
			case 7: prog7(); break;
			case 8: prog8(); break;
			case 10: prog10(); break;
			case 4: prog4(); break;
			default:
		}
	}
	
	public static void prog4() {
		String a = "X";
		for(int i = 0; i < 4; i++) {
			a += a;
		}
		System.out.println(a);
	}
	
	public static void prog5() {
		int a = 1;
		int b = 0;
		for(int i = 0; i < 1; i++) {
			b += a;
			a *= 2;
		}
		for(int i = 0; i < 8; i++) {
			b += a;
			a *= 2;
		}
		for(int i = 0; i < 1; i++) {
			b += a;
			a *= 2;
		}
		a += 1000;
		for(int i = 0; i < 5; i++) {
			b -= a;
			a += 2;
		}
		System.out.println(a);
		System.out.println(b);
	}
	
	public static void prog6() {
		int B = 10 ;
		int A = B ;
		int C = 3 ;
		int D = 4 ;
		int E = 100 ;
		E += C ;
		E += D ;
		A += E ;
		E += E ;
		System.out.println(A) ;
		System.out.println(B) ;
		System.out.println(C );
		System.out.println(D) ;
		System.out.println(E );
	}
	
	public static void prog7() {
		int a = 3;
		int b = 2;
		for(int i = 0; i < 5; i++) {
			b += a;
			a *= 2;
			for(int j = 0; j < 10; j++) {
				a += b;
			}
		}
		System.out.println(a);
		System.out.println(b);
	}
	
	public static void prog8() {
		int a = 1;
		for(int i = 0; i < 5; i++) {
			a += 10000;
			for(int j = 0; j < 5; j++) {
				for(int k = 0; k < 5; k++) {
					a += 1;
				}
			}
			a += 100000;
		}
		System.out.println(a);
	}
	
	public static void prog10() {
		int A = 1;
		int a = 2;
		int numItems = 0;
		int NUMITEMS = 1;
		a += A;
		System.out.println(A);
		System.out.println(a);
		System.out.println(numItems);
	}
}