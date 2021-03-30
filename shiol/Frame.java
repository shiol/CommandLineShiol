package shiol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;
	JTextArea textArea, result;
	JTextField textPath;
	int start, end;

	Frame() {
		setSize(800, 600);
		setTitle("Command Line Shiol");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		textArea = new JTextArea();
		textArea.setBackground(new Color(20, 20, 20));
		textArea.setForeground(Color.WHITE);
		textArea.setCaretColor(Color.WHITE);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
		textArea.setTabSize(4);
		textArea.setLineWrap(true);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setViewportView(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JCheckBox isWrap = new JCheckBox("WRAP", true);
		isWrap.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				textArea.setLineWrap(isWrap.isSelected() ? true : false);
			}
		});

		JButton button = new JButton("EXECUTE");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					start = textArea.getSelectionStart();
					end = textArea.getSelectionEnd();
					textArea.grabFocus();
					textArea.select(start, end);
					String text = textArea.getSelectedText() == null ? textArea.getText() : textArea.getSelectedText();
					ProcessBuilder builder = new ProcessBuilder().command("cmd.exe", "/c", text)
							.directory(new File(textPath.getText()));
					Process process = builder.start();
					printResults(process);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		JLabel label = new JLabel("PATH");
		textPath = new JTextField(50);
		textPath.setBackground(new Color(20, 20, 20));
		textPath.setForeground(Color.WHITE);
		textPath.setCaretColor(Color.WHITE);
		textPath.setFont(new Font("Monospaced", Font.PLAIN, 18));

		JPanel panelPath = new JPanel(new FlowLayout());
		panelPath.add(label);
		panelPath.add(textPath);

		JPanel panelTools = new JPanel(new FlowLayout());
		panelTools.add(isWrap);
		panelTools.add(button);

		result = new JTextArea();
		result.setBackground(new Color(20, 20, 20));
		result.setForeground(Color.WHITE);
		result.setCaretColor(Color.WHITE);
		result.setFont(new Font("Monospaced", Font.PLAIN, 18));
		result.setEditable(false);
		JScrollPane scrollPane2 = new JScrollPane(result);
		scrollPane2.setViewportView(result);
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel panelCenter = new JPanel(new GridLayout(1, 2));
		panelCenter.add(scrollPane);
		panelCenter.add(scrollPane2);

		add(panelTools, BorderLayout.NORTH);
		add(panelCenter, BorderLayout.CENTER);
		add(panelPath, BorderLayout.SOUTH);
		setVisible(true);
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		try {
			if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				BufferedWriter out = new BufferedWriter(new FileWriter("data"));
				out.write(textArea.getText());
				out.close();

				out = new BufferedWriter(new FileWriter("path"));
				out.write(textPath.getText());
				out.close();

				this.dispose();
			}
			if (e.getID() == WindowEvent.WINDOW_OPENED) {
				Scanner scan = new Scanner(new FileReader("data"));
				while (scan.hasNext()) {
					textArea.append(scan.nextLine() + "\n");
				}
				scan.close();

				BufferedReader in = new BufferedReader(new FileReader("path"));
				textPath.setText(in.readLine());
				in.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void printResults(Process process) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			result.append(line + "\n");
		}
	}
}
