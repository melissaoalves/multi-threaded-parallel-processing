package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClienteApp {
    private static JTextField resultField;
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Client");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        resultField = new JTextField();
        resultField.setEditable(false);
        panel.add(resultField, BorderLayout.SOUTH);

        JButton selectFileButton = new JButton("Selecionar arquivo");
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String text = readFile(file);
                    if (text != null && !text.isEmpty()) {
                        sendTextToServer(text);
                    } else {
                        JOptionPane.showMessageDialog(frame, "O arquivo está vazio ou não pôde ser lido.");
                    }
                }
            }
        });
        panel.add(selectFileButton, BorderLayout.NORTH);

        frame.add(panel);
        frame.setVisible(true);
    }


    private static String readFile(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao ler o arquivo: " + e.getMessage());
        }
        return content.toString();
    }

    private static void sendTextToServer(String texto) {
        try {
            URL url = new URL("http://192.168.0.101:8080/processar");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = texto.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                resultField.setText(response.toString());
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar com o servidor: " + e.getMessage());
        }
    }
}
