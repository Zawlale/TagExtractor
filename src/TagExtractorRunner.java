import javax.swing.SwingUtilities;

public class TagExtractorRunner {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TagExtractorFrame frame = new TagExtractorFrame();
            frame.setVisible(true);
        });
    }
}