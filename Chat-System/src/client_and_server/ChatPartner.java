import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.net.SocketFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// Client Socket 2
@SuppressWarnings("serial")
public class ChatPartner extends JFrame {

	private Container container;
	private JTextArea chatArea;
	private JScrollPane scrollPane;
	private BorderLayout borderLayout;
	private JTextField messageField;
	private FlowLayout flowLayout;
	private Container flowContainer;
	private JButton sendButton;
	private Socket socket;
	private OutputStream outputStream;
	private PrintWriter printWriter;

	public ChatPartner(int port) {

		// Initialisieren
		container = this.getContentPane();
		chatArea = new JTextArea("--- Chat-System started ---\n\n");
		scrollPane = new JScrollPane(chatArea);
		borderLayout = new BorderLayout();
		messageField = new JTextField();
		flowLayout = new FlowLayout();
		flowContainer = new Container();
		sendButton = new JButton("Send");
		try {
			socket = SocketFactory.getDefault().createSocket("localhost", port);
		} catch (IOException e) {
			System.err.println("Socket konnte nicht gestartet werden");
		}

		// Styling
		sendButton.setFont(new Font("Senden", Font.BOLD, 14));
		messageField.setPreferredSize(new Dimension(280, 30));
		chatArea.setEditable(false);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		/*
		 * Der Thread wartet durchgehend auf Nachrichten vom Server (vom anderen Client)
		 * und schreibt die Nachrichten in das JTextArea Feld
		 */
		new Thread(() -> {
			while (true) {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String erhalteneNachricht = reader.readLine();
					if (erhalteneNachricht != null) {
						chatArea.setText(chatArea.getText() + "\n" + erhalteneNachricht);
					}
				} catch (IOException e) {
					System.err.println("Fehler beim erhalten der Nachricht des Gesprächspartners");
				}

			}
		}).start();

		// ActionListener
		sendButton.addActionListener(event -> {
			// Eigene Nachricht anzeigen
			String nachricht = messageField.getText();
			chatArea.setText(chatArea.getText() + "\n" + ">>> " + nachricht);

			// Senden der Nachricht über den Server an Client Socket 2
			try {
				outputStream = socket.getOutputStream();
				printWriter = new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
				printWriter.println(nachricht);

			} catch (IOException e) {
				System.err.println("Fehler beim senden des Outputstream von Client Socket 2");
			}

			// Message Feld leeren
			messageField.setText("");
		});

		// Layout
		container.setLayout(borderLayout);
		flowContainer.setLayout(flowLayout);

		// Komponenten zum Fenster hinzufügen
		flowContainer.add(messageField);
		flowContainer.add(sendButton);
		container.add(scrollPane, BorderLayout.CENTER);
		container.add(flowContainer, BorderLayout.SOUTH);

	}

	public static void main(int port) {
		// Fenster erstellen und anpassen
		ChatFrame frame = new ChatFrame(port);
		frame.setTitle("Chat-System");
		frame.setVisible(true);
		frame.setSize(400, 400);
		frame.setLocation(700, 200);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

}
