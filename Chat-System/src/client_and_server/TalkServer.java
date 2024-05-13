import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import javax.net.ServerSocketFactory;

public class TalkServer {

	public static void main(int port) {

		// Server Socket
		try (ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port)) {
			System.out.println("Server gestartet. Warte auf Verbindungen...\n");

			// Verbindung zu Client 1 herstellen
			Socket clientSocket1 = serverSocket.accept();
			System.out.println("Client 1 verbunden.");

			// Verbindung zu Client 2 herstellen
			Socket clientSocket2 = serverSocket.accept();
			System.out.println("Client 2 verbunden.\n");

			// Ein- und Ausgangsströme für Client 1
			InputStream inputStreamClient1 = clientSocket1.getInputStream();
			OutputStream outputStreamClient1 = clientSocket1.getOutputStream();

			// Ein- und Ausgangsströme für Client 2
			InputStream inputStreamClient2 = clientSocket2.getInputStream();
			OutputStream outputStreamClient2 = clientSocket2.getOutputStream();

			// Nachrichten von Client 1 zu Client 2 senden
			new Thread(() -> {
				while (true) {
					try {
						@SuppressWarnings("resource")
						Scanner scanner = new Scanner(inputStreamClient1, StandardCharsets.UTF_8);
						String nachrichtClient1 = scanner.nextLine();
						PrintWriter printWriter = new PrintWriter(outputStreamClient2, true, StandardCharsets.UTF_8);
						printWriter.println("<<< " + nachrichtClient1);
						System.out.println("Nachricht von Client 1 an Client 2 gesendet: " + nachrichtClient1);
					} catch (Exception e) {
						System.err.println(
								"Fehler beim Lesen oder Schreiben von Nachrichten von/zu Client 1: " + e.getMessage());
					}
				}
			}).start();

			// Nachrichten von Client 2 zu Client 1 senden
			new Thread(() -> {
				while (true) {
					try {
						@SuppressWarnings("resource")
						Scanner scanner = new Scanner(inputStreamClient2, StandardCharsets.UTF_8);
						String nachrichtClient2 = scanner.nextLine();
						PrintWriter printWriter = new PrintWriter(outputStreamClient1, true, StandardCharsets.UTF_8);
						printWriter.println("<<< " + nachrichtClient2);
						System.out.println("Nachricht von Client 2 an Client 1 gesendet: " + nachrichtClient2);
					} catch (Exception e) {
						System.err.println(
								"Fehler beim Lesen oder Schreiben von Nachrichten von/zu Client 2: " + e.getMessage());
					}
				}

			}).start();

		} catch (IOException e) {
			System.err.println("Server Fehler");
		}

	}

}
