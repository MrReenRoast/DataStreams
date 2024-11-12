import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class DataStreamGUI extends JFrame {
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton, searchButton, quitButton;
    private Path loadedFilePath;

    public DataStreamGUI() {
        setTitle("Text File Searcher");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Text Areas with scroll panes
        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        JScrollPane originalScrollPane = new JScrollPane(originalTextArea);
        JScrollPane filteredScrollPane = new JScrollPane(filteredTextArea);

        // Panel for text areas
        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        textPanel.add(originalScrollPane);
        textPanel.add(filteredScrollPane);

        // Search field and buttons
        searchField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        // Disable search button until file is loaded
        searchButton.setEnabled(false);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JLabel("Search:"));
        buttonPanel.add(searchField);
        buttonPanel.add(loadButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(quitButton);

        // Add components to main frame
        add(textPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button action listeners
        loadButton.addActionListener(new LoadFileAction());
        searchButton.addActionListener(new SearchFileAction());
        quitButton.addActionListener(e -> System.exit(0));
    }

    private class LoadFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(DataStreamGUI.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                loadedFilePath = fileChooser.getSelectedFile().toPath();
                loadFileContent();
                searchButton.setEnabled(true);
            }
        }

        private void loadFileContent() {
            originalTextArea.setText("");
            filteredTextArea.setText("");

            try (Stream<String> lines = Files.lines(loadedFilePath)) {
                lines.forEach(line -> originalTextArea.append(line + "\n"));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(DataStreamGUI.this, "Error loading file: " + ex.getMessage(),
                        "File Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SearchFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (loadedFilePath == null) {
                JOptionPane.showMessageDialog(DataStreamGUI.this, "Please load a file first.", "No File Loaded",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(DataStreamGUI.this, "Please enter a search term.", "Empty Search Term",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            filteredTextArea.setText("");

            try (Stream<String> lines = Files.lines(loadedFilePath)) {
                lines.filter(line -> line.contains(searchTerm))
                        .forEach(line -> filteredTextArea.append(line + "\n"));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(DataStreamGUI.this, "Error searching file: " + ex.getMessage(),
                        "Search Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataStreamGUI frame = new DataStreamGUI();
            frame.setVisible(true);
        });
    }
}
