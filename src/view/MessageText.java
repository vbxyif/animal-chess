package view;

import javax.swing.*;
import java.awt.*;

public class MessageText extends JTextArea {
    public MessageText(String text, Color color) {
        setText(text);
        this.setForeground(color);
    }
}
