package itmo.lab7.basic.utils.strings;

import java.util.Arrays;
import java.util.Scanner;

public class CollectionPrinter {
    public static boolean printCollection(String response, int collectionSize, int curIndex) {
        System.out.println(response.substring(1, response.length() - 1));

        if ((curIndex+1)*20 >= collectionSize) return false;

        System.out.println("Do you want to continue? (y/n)");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();

        return answer.equals("y");
    }
}