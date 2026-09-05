package civgame.view.chat;

import civgame.network.message.chat.ChatMessageBroadcast;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatPanel extends javax.swing.JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30, 210);
    private static final Color BORDER_COLOR = new Color(200, 170, 90);
    private static final Color TEXT_COLOR = new Color(235, 235, 235);
    private static final Color USERNAME_COLOR = new Color(120, 180, 255);
    private static final Color TIMESTAMP_COLOR = new Color(160, 160, 160);
    private static final Color SYSTEM_COLOR = new Color(255, 200, 80);
    private static final Color INPUT_BACKGROUND = new Color(40, 40, 50, 220);
    private static final Font MESSAGE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final int MAX_MESSAGES = 100;
    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 280;
    private static final int INPUT_HEIGHT = 30;
    
    private final JTextPane messagesPane;
    private final JTextField inputField;
    private final JScrollPane scrollPane;
    private final StyledDocument styledDocument;
    private final SimpleDateFormat timeFormat;
    private ChatMessageListener messageListener;
    private int messageCount;
    
    public ChatPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 5));
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        
        this.messagesPane = new JTextPane();
        this.inputField = new JTextField();
        this.scrollPane = new JScrollPane(messagesPane);
        this.styledDocument = messagesPane.getStyledDocument();
        this.timeFormat = new SimpleDateFormat("HH:mm");
        this.messageCount = 0;
        
        setupMessagesPane();
        setupInputField();
        setupLayout();
    }
    
    private void setupMessagesPane() {
        messagesPane.setEditable(false);
        messagesPane.setFont(MESSAGE_FONT);
        messagesPane.setForeground(TEXT_COLOR);
        messagesPane.setBackground(new Color(0, 0, 0, 0));
        messagesPane.setMargin(new java.awt.Insets(8, 8, 8, 8));
        
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }
    
    private void setupInputField() {
        inputField.setFont(INPUT_FONT);
        inputField.setForeground(TEXT_COLOR);
        inputField.setCaretColor(TEXT_COLOR);
        inputField.setBackground(INPUT_BACKGROUND);
        inputField.setPreferredSize(new Dimension(PANEL_WIDTH - 10, INPUT_HEIGHT));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                handleSendMessage();
            }
        });
    }
    
    private void setupLayout() {
        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        graphics2D.setColor(BACKGROUND_COLOR);
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        
        graphics2D.setColor(BORDER_COLOR);
        graphics2D.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        graphics2D.dispose();

    }
    
    private void handleSendMessage() {
        String content = inputField.getText().trim();
        if (content.isEmpty()) {
            return;
        }
        
        if (messageListener != null) {
            messageListener.onMessageSent(content);
        }
        
        inputField.setText("");
    }
    
    public void appendChatMessage(ChatMessageBroadcast broadcast) {
        String timeString = timeFormat.format(new Date(broadcast.getSentAt()));
        
        SimpleAttributeSet timestampStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(timestampStyle, TIMESTAMP_COLOR);
        
        SimpleAttributeSet usernameStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(usernameStyle, USERNAME_COLOR);
        StyleConstants.setBold(usernameStyle, true);
        
        SimpleAttributeSet messageStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(messageStyle, TEXT_COLOR);
        
        try {
            styledDocument.insertString(styledDocument.getLength(), "[" + timeString + "] ", timestampStyle);
            styledDocument.insertString(styledDocument.getLength(), broadcast.getUsername(), usernameStyle);
            styledDocument.insertString(styledDocument.getLength(), ": " + broadcast.getContent() + "\n", messageStyle);
        } catch (BadLocationException e) {
            System.err.println("Failed to append chat message: " + e.getMessage());
        }
        
        messageCount++;
        
        if (messageCount > MAX_MESSAGES) {
            trimOldMessages();
        }
        
        messagesPane.setCaretPosition(styledDocument.getLength());
    }
    
    public void appendSystemMessage(String message) {
        String timeString = timeFormat.format(new Date());
        
        SimpleAttributeSet timestampStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(timestampStyle, TIMESTAMP_COLOR);
        
        SimpleAttributeSet systemStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(systemStyle, SYSTEM_COLOR);
        StyleConstants.setBold(systemStyle, true);
        
        try {
            styledDocument.insertString(styledDocument.getLength(), "[" + timeString + "] ", timestampStyle);
            styledDocument.insertString(styledDocument.getLength(), "[System] " + message + "\n", systemStyle);
        } catch (BadLocationException e) {
            System.err.println("Failed to append system message: " + e.getMessage());
        }
        
        messagesPane.setCaretPosition(styledDocument.getLength());
    }
    
    private void trimOldMessages() {
        try {
            String text = styledDocument.getText(0, styledDocument.getLength());
            int firstNewline = text.indexOf('\n');
            if (firstNewline >= 0) {
                styledDocument.remove(0, firstNewline + 1);
                messageCount--;
            }
        } catch (BadLocationException e) {
            System.err.println("Failed to trim messages: " + e.getMessage());
        }
    }
    
    public void clearMessages() {
        try {
            styledDocument.remove(0, styledDocument.getLength());
            messageCount = 0;
        } catch (BadLocationException e) {
            System.err.println("Failed to clear messages: " + e.getMessage());
        }
    }
    
    public void setChatMessageListener(ChatMessageListener listener) {
        this.messageListener = listener;
    }
    
    public JTextField getInputField() {
        return inputField;
    }
    
    public interface ChatMessageListener {
        void onMessageSent(String content);
    }
}