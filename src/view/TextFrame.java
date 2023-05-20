package view;

import javax.swing.*;
import java.awt.*;

public class TextFrame extends JFrame {

    public TextFrame(int width, int height) {
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    public void addText(String text, int width, int height, Color color) {
        MessageText text1 = new MessageText(text, color);
        text1.setEditable(false);
        text1.setSize(width, height);
        text1.setFont(new Font("Rockwell", Font.BOLD, 18));
        text1.setLineWrap(true);
        text1.setWrapStyleWord(true);
        this.add(text1, BorderLayout.CENTER);
    }
}
