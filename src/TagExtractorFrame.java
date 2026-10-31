import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class TagExtractorFrame extends JFrame {

    private TagExtractor extractor;
    private File textFile;
    private File stopWordsFile;

    private JLabel textFileLabel;
    private JLabel stopWordsLabel;
    private JTextArea resultsArea;
    private JButton saveButton;

    public TagExtractorFrame() {
        extractor = new TagExtractor();

        setTitle("Tag Extractor");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        createFilePanel();
        createResultsPanel();
        createButtonPanel();
    }

    private void createFilePanel() {
        JPanel filePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        filePanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 0, 10));

        textFileLabel = new JLabel("Text file: None selected");
        stopWordsLabel = new JLabel("Stop words file: None selected");

        filePanel.add(textFileLabel);
        filePanel.add(stopWordsLabel);

        add(filePanel, BorderLayout.NORTH);
    }

    private void createResultsPanel() {
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Keywords and Frequencies"));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton textButton = new JButton("Select Text");
        JButton stopWordsButton = new JButton("Select Stop Words");
        JButton extractButton = new JButton("Extract Tags");
        saveButton = new JButton("Save Tags");
        JButton quitButton = new JButton("Quit");

        saveButton.setEnabled(false);

        textButton.addActionListener(e -> selectTextFile());
        stopWordsButton.addActionListener(e -> selectStopWordsFile());
        extractButton.addActionListener(e -> extractTags());
        saveButton.addActionListener(e -> saveTags());
        quitButton.addActionListener(e -> dispose());

        buttonPanel.add(textButton);
        buttonPanel.add(stopWordsButton);
        buttonPanel.add(extractButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void selectTextFile() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setDialogTitle("Select a Text File");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            textFile = chooser.getSelectedFile();
            textFileLabel.setText("Text file: " + textFile.getName());

            clearResults();
        }
    }

    private void selectStopWordsFile() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setDialogTitle("Select a Stop Words File");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            clearResults();

            try {
                extractor.loadStopWords(selectedFile);
                stopWordsFile = selectedFile;

                stopWordsLabel.setText(
                        "Stop words file: " + stopWordsFile.getName()
                                + " (" + extractor.getStopWordCount()
                                + " words)");
            } catch (IOException e) {
                stopWordsFile = null;
                stopWordsLabel.setText("Stop words file: None selected");

                JOptionPane.showMessageDialog(this,
                        "Could not read the stop words file.",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void extractTags() {
        if (textFile == null || stopWordsFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a text file and a stop words file first.");
            return;
        }

        clearResults();

        try {
            extractor.extractTags(textFile);

            StringBuilder results = new StringBuilder();
            results.append("File: ").append(textFile.getName()).append("\n");
            results.append("Unique keywords: ")
                    .append(extractor.getTags().size()).append("\n\n");

            results.append(String.format("%-25s %s%n",
                    "Keyword", "Frequency"));
            results.append("-------------------------------------\n");

            for (Map.Entry<String, Integer> entry
                    : extractor.getTags().entrySet()) {

                results.append(String.format("%-25s %d%n",
                        entry.getKey(), entry.getValue()));
            }

            resultsArea.setText(results.toString());
            resultsArea.setCaretPosition(0);
            saveButton.setEnabled(true);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not read the text file.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTags() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setDialogTitle("Save Extracted Tags");
        chooser.setSelectedFile(new File("ExtractedTags.txt"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File outputFile = chooser.getSelectedFile();

            if (outputFile.exists()) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "This file already exists. Replace it?",
                        "Replace File",
                        JOptionPane.YES_NO_OPTION);

                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                Files.writeString(outputFile.toPath(),
                        resultsArea.getText(), StandardCharsets.UTF_8);

                JOptionPane.showMessageDialog(this,
                        "Tags saved successfully!");

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Could not save the tags.",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearResults() {
        resultsArea.setText("");
        saveButton.setEnabled(false);
    }
}