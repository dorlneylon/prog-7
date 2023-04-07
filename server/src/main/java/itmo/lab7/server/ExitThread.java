package itmo.lab7.server;

import java.util.Scanner;


public final class ExitThread extends Thread {
    public ExitThread() {
        super(() -> {
            // Создаем объект класса Scanner для считывания данных с консоли
            Scanner scanner = new Scanner(System.in);
            // Проверяем, содержит ли введенная строка слово "exit"
            if (scanner.nextLine().contains("exit")) {
                // Завершаем программу с кодом 0
                System.exit(0);
            }
        });
    }
}