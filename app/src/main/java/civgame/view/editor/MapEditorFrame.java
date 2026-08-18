package civgame.view.editor;

import civgame.model.game.MapBlueprintFileManager;
import civgame.model.game.SaveLoadResult;
import civgame.model.map.MapBlueprint;
import civgame.model.map.TerrainType;
import civgame.model.map.TileResourceType;
import civgame.util.Constants;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapEditorFrame extends JFrame {

    public interface PlayCallback {
        void onPlayRequested(MapBlueprint blueprint);
    }

    private final MapBlueprintFileManager mapFileManager;
    private final PlayCallback playCallback;
    private MapEditorPanel editorPanel;
    private MapBlueprint blueprint;

    public MapEditorFrame(PlayCallback playCallback) {
        super("Map Editor");
        this.playCallback = playCallback;
        this.mapFileManager = new MapBlueprintFileManager();
        this.blueprint = new MapBlueprint(Constants.MAP_EDITOR_DEFAULT_RADIUS);

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        editorPanel = new MapEditorPanel(blueprint);
        add(editorPanel, BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.NORTH);

        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JComboBox<TerrainType> terrainCombo = new JComboBox<TerrainType>(TerrainType.values());
        terrainCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editorPanel.setToolMode(MapEditorToolMode.PAINT_TERRAIN);
                editorPanel.setSelectedTerrainType((TerrainType) terrainCombo.getSelectedItem());
            }
        });

        JComboBox<TileResourceType> resourceCombo = new JComboBox<TileResourceType>(TileResourceType.values());
        resourceCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editorPanel.setSelectedResourceType((TileResourceType) resourceCombo.getSelectedItem());
            }
        });

        JButton townHallButton = new JButton("Place Town Hall");
        townHallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editorPanel.setToolMode(MapEditorToolMode.PLACE_TOWN_HALL);
            }
        });

        JButton riverButton = new JButton("Toggle River (click 2 adjacent hexes)");
        riverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editorPanel.setToolMode(MapEditorToolMode.TOGGLE_RIVER);
            }
        });

        JButton wallButton = new JButton("Toggle Wall (click 2 adjacent hexes)");
        wallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editorPanel.setToolMode(MapEditorToolMode.TOGGLE_WALL);
            }
        });

        JButton newButton = new JButton("New Blank Map");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                blueprint = new MapBlueprint(Constants.MAP_EDITOR_DEFAULT_RADIUS);
                rebuildEditorPanel();
            }
        });

        JButton saveButton = new JButton("Save Map");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                handleSave();
            }
        });

        JButton loadButton = new JButton("Load Map");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                handleLoad();
            }
        });

        JButton playButton = new JButton("Play This Map");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                handlePlay();
            }
        });

        toolbar.add(new JLabel("Terrain:"));
        toolbar.add(terrainCombo);
        toolbar.add(new JLabel("Resource:"));
        toolbar.add(resourceCombo);
        toolbar.add(townHallButton);
        toolbar.add(riverButton);
        toolbar.add(wallButton);
        toolbar.add(newButton);
        toolbar.add(saveButton);
        toolbar.add(loadButton);
        toolbar.add(playButton);

        return toolbar;
    }

    private void rebuildEditorPanel() {
        remove(editorPanel);
        editorPanel = new MapEditorPanel(blueprint);
        add(editorPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void handleSave() {
        if (blueprint.getTownHallCoordinate() == null) {
            JOptionPane.showMessageDialog(this, "Place a Town Hall before saving.");
            return;
        }

        String mapName = JOptionPane.showInputDialog(this, "Map name:", "MyMap");
        if (mapName == null || mapName.trim().isEmpty()) {
            return;
        }

        SaveLoadResult result = mapFileManager.save(mapName.trim(), blueprint);
        JOptionPane.showMessageDialog(this, result.getMessage());
    }

    private void handleLoad() {
        String[] mapNames = mapFileManager.listMapNames();
        if (mapNames.length == 0) {
            JOptionPane.showMessageDialog(this, "No saved maps found.");
            return;
        }

        String chosenName = (String) JOptionPane.showInputDialog(this, "Choose a map:", "Load Map",
                JOptionPane.QUESTION_MESSAGE, null, mapNames, mapNames[0]);
        if (chosenName == null) {
            return;
        }

        MapBlueprint loadedBlueprint = mapFileManager.load(chosenName);
        if (loadedBlueprint == null) {
            JOptionPane.showMessageDialog(this, "Could not load this map.");
            return;
        }

        blueprint = loadedBlueprint;
        rebuildEditorPanel();
    }

    private void handlePlay() {
        if (blueprint.getTownHallCoordinate() == null) {
            JOptionPane.showMessageDialog(this, "Place a Town Hall before playing this map.");
            return;
        }
        if (playCallback == null) {
            JOptionPane.showMessageDialog(this, "Playing is not available from this window.");
            return;
        }

        playCallback.onPlayRequested(blueprint);
        dispose();
    }
}