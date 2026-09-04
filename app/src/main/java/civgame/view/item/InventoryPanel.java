package civgame.view.item;

import civgame.model.item.Item;
import civgame.util.ItemIconResolver;
import civgame.view.game.StyledButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

public class InventoryPanel extends JDialog {

    private final JPanel itemsPanel;
    private ItemUseListener useListener;

    public InventoryPanel(java.awt.Frame owner) {
        super(owner, "Inventory", true);

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(20, 20, 30));

        JLabel titleLabel = new JLabel("Your Items", javax.swing.SwingConstants.CENTER);
        titleLabel.setForeground(new Color(200, 200, 255));
        titleLabel.setFont(titleLabel.getFont().deriveFont(16.0f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(20, 20, 30));
        scrollPane.setPreferredSize(new Dimension(350, 250));
        add(scrollPane, BorderLayout.CENTER);

        add(createButtonPanel(), BorderLayout.SOUTH);

        setSize(400, 400);
        setLocationRelativeTo(owner);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        StyledButton closeButton = new StyledButton("Close");
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton);

        return panel;
    }

    public void updateItems(List<Item> items) {
        itemsPanel.removeAll();

        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("No items in inventory", javax.swing.SwingConstants.CENTER);
            emptyLabel.setForeground(new Color(150, 150, 150));
            emptyLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            itemsPanel.add(emptyLabel);
        } else {
            for (Item item : items) {
                ItemEntry entry = new ItemEntry(item);
                itemsPanel.add(entry);
                itemsPanel.add(Box.createVerticalStrut(8));
            }
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    public void setUseListener(ItemUseListener listener) {
        this.useListener = listener;
    }

    private class ItemEntry extends JPanel {
        public ItemEntry(Item item) {
            setLayout(new BorderLayout(10, 5));
            setBackground(new Color(30, 30, 45));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 80)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            javax.swing.Icon icon = ItemIconResolver.resolveForItem(item.getItemType());
            JLabel iconLabel = new JLabel(icon);
            add(iconLabel, BorderLayout.WEST);

            JLabel nameLabel = new JLabel(item.getDisplayName());
            nameLabel.setForeground(new Color(220, 220, 220));
            add(nameLabel, BorderLayout.CENTER);

            StyledButton useButton = new StyledButton("Use");
            final String itemId = item.getId();
            useButton.addActionListener(e -> {
                if (useListener != null) {
                    useListener.onItemUse(itemId);
                }
            });
            add(useButton, BorderLayout.EAST);
        }
    }

    public interface ItemUseListener {
        void onItemUse(String itemId);
    }
}