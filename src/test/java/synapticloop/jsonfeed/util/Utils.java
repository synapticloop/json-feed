package synapticloop.jsonfeed.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Utils {
	public static String resourceToString(InputStream inputStream) {
		Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
		String retVal = scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		try {
			inputStream.close();
		} catch (IOException e) {
			// do nothing
		}
		return(retVal);
	}
}
