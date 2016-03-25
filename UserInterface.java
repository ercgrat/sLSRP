import java.io.Console;
import java.util.Scanner;

/*
 * This class is used for testing packet or file transfer between routers;
 */
public class UserInterface {
	//TODO
//	Scanner reader = new Scanner(System.in);
//	System.out.println("Enter a file path: ");
//	reader.nextInt();
	
	Console console = System.console();
	String s = console.readLine();
	String text = console.readLine();
}
