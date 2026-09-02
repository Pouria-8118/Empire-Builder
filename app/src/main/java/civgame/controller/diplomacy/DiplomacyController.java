package civgame.controller.diplomacy;

import civgame.client.GameClient;
import civgame.client.diplomacy.DiplomacyClientState;
import civgame.model.diplomacy.PlayerRelation;
import civgame.network.NetworkException;
import civgame.network.message.Broadcast;
import civgame.network.message.MessageType;
import civgame.network.message.diplomacy.AllianceEstablishedBroadcast;
import civgame.network.message.diplomacy.AllianceProposalBroadcast;
import civgame.network.message.diplomacy.AllianceResponseRequest;
import civgame.network.message.diplomacy.DeclareWarBroadcast;
import civgame.network.message.diplomacy.DeclareWarRequest;
import civgame.network.message.diplomacy.DiplomacyResponse;
import civgame.network.message.diplomacy.OfferPeaceRequest;
import civgame.network.message.diplomacy.PeaceEstablishedBroadcast;
import civgame.network.message.diplomacy.PeaceOfferBroadcast;
import civgame.network.message.diplomacy.PeaceResponseRequest;
import civgame.network.message.diplomacy.ProposeAllianceRequest;
import civgame.network.message.diplomacy.RelationUpdateBroadcast;
import civgame.network.message.game.GameStartBroadcast;
import civgame.view.diplomacy.DiplomacyDialog;
import civgame.view.diplomacy.DiplomacyPanel;
import civgame.view.game.GameFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.List;

public class DiplomacyController {
    
    private final GameFrame gameFrame;
    private final DiplomacyPanel diplomacyPanel;
    private final DiplomacyClientState state;
    private GameClient gameClient;
    private DiplomacyPanel.DiplomacyActionListener actionListener;
    
    public DiplomacyController(GameFrame gameFrame, String myClientId) {
        this.gameFrame = gameFrame;
        this.diplomacyPanel = gameFrame.getDiplomacyPanel();
        this.state = new DiplomacyClientState(myClientId);
        this.gameClient = null;
        
        setupActionListener();
    }
    
    private void setupActionListener() {
        this.actionListener = new DiplomacyPanel.DiplomacyActionListener() {
            @Override
            public void onActionRequested(DiplomacyClientState.OpponentInfo opponent, DiplomacyPanel.DiplomacyAction action) {
                performAction(opponent, action);
            }
            
            @Override
            public void onOpenDialogRequested() {
                openDiplomacyDialog();
            }
        };
        diplomacyPanel.setActionListener(actionListener);
    }
    
    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }
    
    public void initializeFromOpponents(List<GameStartBroadcast.OpponentInfoDTO> opponents) {
        for (int i = 0; i < opponents.size(); i++) {
            GameStartBroadcast.OpponentInfoDTO dto = opponents.get(i);
            state.addOpponent(dto.getClientId(), dto.getUsername(), dto.getPlayerIndex());
        }
        refreshPanel();
    }
    
    public void initializeFromRelations(List<GameStartBroadcast.RelationEntryDTO> relations) {
        if (relations == null) {
            return;
        }
        for (int i = 0; i < relations.size(); i++) {
            GameStartBroadcast.RelationEntryDTO dto = relations.get(i);
            try {
                PlayerRelation relation = PlayerRelation.valueOf(dto.getRelation());
                state.setRelation(dto.getPlayerAClientId(), dto.getPlayerBClientId(), relation);
            } catch (IllegalArgumentException ignored) {
            }
        }
        refreshPanel();
    }
    
    public void handleBroadcast(Broadcast broadcast) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                processBroadcast(broadcast);
            }
        });
    }
    
    public void handleDiplomacyResponse(DiplomacyResponse response) {
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(
                    gameFrame,
                    response.getReason(),
                    "Diplomacy Failed",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
    
    private void processBroadcast(Broadcast broadcast) {
        String type = broadcast.getType();
        
        if (type.equals(MessageType.DECLARE_WAR_BROADCAST)) {
            handleDeclareWarBroadcast((DeclareWarBroadcast) broadcast);
        } else if (type.equals(MessageType.PEACE_OFFER_BROADCAST)) {
            handlePeaceOfferBroadcast((PeaceOfferBroadcast) broadcast);
        } else if (type.equals(MessageType.PEACE_ESTABLISHED_BROADCAST)) {
            handlePeaceEstablishedBroadcast((PeaceEstablishedBroadcast) broadcast);
        } else if (type.equals(MessageType.ALLIANCE_PROPOSAL_BROADCAST)) {
            handleAllianceProposalBroadcast((AllianceProposalBroadcast) broadcast);
        } else if (type.equals(MessageType.ALLIANCE_ESTABLISHED_BROADCAST)) {
            handleAllianceEstablishedBroadcast((AllianceEstablishedBroadcast) broadcast);
        } else if (type.equals(MessageType.RELATION_UPDATE_BROADCAST)) {
            handleRelationUpdateBroadcast((RelationUpdateBroadcast) broadcast);
        }
    }
    
    private void handleDeclareWarBroadcast(DeclareWarBroadcast broadcast) {
        state.setRelation(broadcast.getAggressorClientId(), broadcast.getTargetClientId(), PlayerRelation.WAR);
        refreshPanel();
        JOptionPane.showMessageDialog(
                gameFrame,
                broadcast.getAggressorUsername() + " has declared war on " + broadcast.getTargetUsername() + "!",
                "War Declared",
                JOptionPane.WARNING_MESSAGE
        );
    }
    
    private void handlePeaceOfferBroadcast(PeaceOfferBroadcast broadcast) {
        if (!broadcast.getTargetClientId().equals(state.getMyClientId())) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(
                gameFrame,
                broadcast.getProposerUsername() + " offers peace. Accept?",
                "Peace Offer",
                JOptionPane.YES_NO_OPTION
        );
        boolean accepted = (choice == JOptionPane.YES_OPTION);
        sendPeaceResponse(broadcast.getProposerClientId(), accepted);
    }
    
    private void handlePeaceEstablishedBroadcast(PeaceEstablishedBroadcast broadcast) {
        state.setRelation(broadcast.getPlayerAClientId(), broadcast.getPlayerBClientId(), PlayerRelation.NEUTRAL);
        refreshPanel();
        JOptionPane.showMessageDialog(
                gameFrame,
                broadcast.getPlayerAUsername() + " and " + broadcast.getPlayerBUsername() + " have established peace.",
                "Peace Established",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void handleAllianceProposalBroadcast(AllianceProposalBroadcast broadcast) {
        if (!broadcast.getTargetClientId().equals(state.getMyClientId())) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(
                gameFrame,
                broadcast.getProposerUsername() + " proposes an alliance. Accept?",
                "Alliance Proposal",
                JOptionPane.YES_NO_OPTION
        );
        boolean accepted = (choice == JOptionPane.YES_OPTION);
        sendAllianceResponse(broadcast.getProposerClientId(), accepted);
    }
    
    private void handleAllianceEstablishedBroadcast(AllianceEstablishedBroadcast broadcast) {
        state.setRelation(broadcast.getPlayerAClientId(), broadcast.getPlayerBClientId(), PlayerRelation.ALLY);
        refreshPanel();
        JOptionPane.showMessageDialog(
                gameFrame,
                broadcast.getPlayerAUsername() + " and " + broadcast.getPlayerBUsername() + " have formed an alliance!",
                "Alliance Established",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void handleRelationUpdateBroadcast(RelationUpdateBroadcast broadcast) {
        PlayerRelation relation = broadcast.getRelationEnum();
        state.setRelation(broadcast.getPlayerAClientId(), broadcast.getPlayerBClientId(), relation);
        refreshPanel();
    }
    
    private void refreshPanel() {
        diplomacyPanel.refresh(state);
    }
    
    private void performAction(DiplomacyClientState.OpponentInfo opponent, DiplomacyPanel.DiplomacyAction action) {
        if (gameClient == null || !gameClient.isConnected()) {
            return;
        }
        
        PlayerRelation currentRelation = state.getRelationWithMe(opponent.getClientId());
        
        switch (action) {
            case DECLARE_WAR:
                confirmAndDeclareWar(opponent, currentRelation);
                break;
            case OFFER_PEACE:
                sendOfferPeace(opponent);
                break;
            case PROPOSE_ALLIANCE:
                sendProposeAlliance(opponent);
                break;
        }
    }
    
    private void confirmAndDeclareWar(DiplomacyClientState.OpponentInfo opponent, PlayerRelation currentRelation) {
        int choice = JOptionPane.showConfirmDialog(
                gameFrame,
                "Are you sure you want to declare war on " + opponent.getUsername() + "?\n"
                + "Current relation: " + currentRelation.getDisplayName(),
                "Declare War",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        
        DeclareWarRequest request = new DeclareWarRequest(state.getMyClientId(), opponent.getClientId());
        try {
            gameClient.sendMessageObject(request);
        } catch (NetworkException e) {
            JOptionPane.showMessageDialog(gameFrame, "Failed to send: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sendOfferPeace(DiplomacyClientState.OpponentInfo opponent) {
        OfferPeaceRequest request = new OfferPeaceRequest(state.getMyClientId(), opponent.getClientId());
        try {
            gameClient.sendMessageObject(request);
        } catch (NetworkException e) {
            JOptionPane.showMessageDialog(gameFrame, "Failed to send: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sendProposeAlliance(DiplomacyClientState.OpponentInfo opponent) {
        ProposeAllianceRequest request = new ProposeAllianceRequest(state.getMyClientId(), opponent.getClientId());
        try {
            gameClient.sendMessageObject(request);
        } catch (NetworkException e) {
            JOptionPane.showMessageDialog(gameFrame, "Failed to send: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sendPeaceResponse(String proposerClientId, boolean accepted) {
        if (gameClient == null || !gameClient.isConnected()) {
            return;
        }
        PeaceResponseRequest request = new PeaceResponseRequest(state.getMyClientId(), proposerClientId, accepted);
        try {
            gameClient.sendMessageObject(request);
        } catch (NetworkException e) {
            System.err.println("Failed to send peace response: " + e.getMessage());
        }
    }
    
    private void sendAllianceResponse(String proposerClientId, boolean accepted) {
        if (gameClient == null || !gameClient.isConnected()) {
            return;
        }
        AllianceResponseRequest request = new AllianceResponseRequest(state.getMyClientId(), proposerClientId, accepted);
        try {
            gameClient.sendMessageObject(request);
        } catch (NetworkException e) {
            System.err.println("Failed to send alliance response: " + e.getMessage());
        }
    }
    
    private void openDiplomacyDialog() {
        DiplomacyDialog dialog = new DiplomacyDialog(gameFrame, state, new DiplomacyPanel.DiplomacyActionListener() {
            @Override
            public void onActionRequested(DiplomacyClientState.OpponentInfo opponent, DiplomacyPanel.DiplomacyAction action) {
                performAction(opponent, action);
            }
            
            @Override
            public void onOpenDialogRequested() {
            }
        });
        dialog.setVisible(true);
    }
    
    public DiplomacyClientState getState() {
        return state;
    }
}