package civgame.view.game;

import civgame.model.game.GameSaveManager;
import civgame.model.game.GameSession;
import civgame.model.game.LoadOutcome;
import civgame.model.game.SaveLoadResult;
import civgame.model.game.SaveMetadata;
import civgame.model.game.SaveSlot;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SaveLoadDialog extends JDialog {

    private final GameSaveManager saveManager;
    private final GameSession currentGameSession;
    private final LoadCallback loadCallback;

    public interface LoadCallback {
        void onGameLoaded(GameSession loadedGameSession);
    }

    public SaveLoadDialog(GameSaveManager saveManager, GameSession currentGameSession, LoadCallback loadCallback) {
        super((java.awt.Frame) null, "Save / Load", true);
        this.saveManager = saveManager;
        this.currentGameSession = currentGameSession;
        this.loadCallback = loadCallback;

        setLayout(new BorderLayout(10, 10));

        JPanel slotsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        slotsPanel.add(buildSlotRow(SaveSlot.SLOT_1));
        slotsPanel.add(buildSlotRow(SaveSlot.SLOT_2));
        slotsPanel.add(buildSlotRow(SaveSlot.SLOT_3));
        slotsPanel.add(buildSlotRow(SaveSlot.AUTOSAVE));

        add(slotsPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });
        add(closeButton, BorderLayout.SOUTH);

        setSize(520, 260);
        setLocationRelativeTo(null);
    }

    private JPanel buildSlotRow(SaveSlot slot) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        SaveMetadata metadata = saveManager.peekMetadata(slot);
        String summary = metadata == null ? (slot.name() + ": empty") : metadata.buildSummaryText();
        JLabel summaryLabel = new JLabel(summary);
        row.add(summaryLabel);
        row.add(javax.swing.Box.createHorizontalGlue());

        if (currentGameSession != null) {
            JButton saveButton = new JButton("Save");
            saveButton.setEnabled(slot != SaveSlot.AUTOSAVE);
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    handleSave(slot);
                }
            });
            row.add(saveButton);
        }

        JButton loadButton = new JButton("Load");
        loadButton.setEnabled(saveManager.slotExists(slot));
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                handleLoad(slot);
            }
        });
        row.add(loadButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(saveManager.slotExists(slot));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveManager.deleteSlot(slot);
                refreshAndReopen();
            }
        });
        row.add(deleteButton);

        return row;
    }

    private void handleSave(SaveSlot slot) {
        if (saveManager.slotExists(slot)) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Overwrite the existing save in this slot?", "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String saveName = JOptionPane.showInputDialog(this, "Save name:", "My Empire");
        if (saveName == null || saveName.trim().isEmpty()) {
            return;
        }

        SaveLoadResult result = saveManager.save(slot, saveName.trim(), currentGameSession);
        JOptionPane.showMessageDialog(this, result.getMessage());
        refreshAndReopen();
    }

    private void handleLoad(SaveSlot slot) {
        if (currentGameSession != null) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Loading will discard any unsaved progress in the current game. Continue?", "Confirm Load", JOptionPane.YES_NO_OPTION);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }
        }

        LoadOutcome outcome = saveManager.load(slot);
        if (!outcome.isSuccess()) {
            JOptionPane.showMessageDialog(this, outcome.getMessage());
            return;
        }

        dispose();
        if (loadCallback != null) {
            loadCallback.onGameLoaded(outcome.getGameSession());
        }
    }

    private void refreshAndReopen() {
        dispose();
        SaveLoadDialog refreshed = new SaveLoadDialog(saveManager, currentGameSession, loadCallback);
        refreshed.setVisible(true);
    }
}