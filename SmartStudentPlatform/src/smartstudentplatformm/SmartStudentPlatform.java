package smartstudentplatformm;

import javax.swing.UIManager;
import smartstudentplatform.ui.MainFrame;

public class SmartStudentPlatform {
    public static void main(String[] args) {
        // optional: use system look & feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        javax.swing.SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
