package civgame.view.game;

import civgame.model.game.GameSession;
import civgame.model.game.TribeDiplomacyService;
import civgame.model.game.TribeGiftService;
import civgame.model.game.TribeMissionService;
import civgame.model.game.TribeTradeService;
import civgame.model.resource.ResourceType;
import civgame.model.tribe.MissionStatus;
import civgame.model.tribe.Tribe;
import civgame.model.tribe.TribeMission;
import civgame.model.tribe.TribeMissionDefinitionProvider;
import civgame.model.tribe.TribeRelationState;
import civgame.util.Constants;
import civgame.util.TribeRewardDescriptionProvider;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TribeMenuFactory {

    private final TribeGiftService tribeGiftService;
    private final TribeTradeService tribeTradeService;
    private final TribeDiplomacyService tribeDiplomacyService;
    private final TribeMissionService tribeMissionService;

    public TribeMenuFactory(TribeGiftService tribeGiftService, TribeTradeService tribeTradeService, TribeDiplomacyService tribeDiplomacyService, TribeMissionService tribeMissionService) {
        this.tribeGiftService = tribeGiftService;
        this.tribeTradeService = tribeTradeService;
        this.tribeDiplomacyService = tribeDiplomacyService;
        this.tribeMissionService = tribeMissionService;
    }

    public JPopupMenu buildMenu(GameSession gameSession, Tribe tribe, Runnable onActionSuccess) {
        JPopupMenu popupMenu = new JPopupMenu();
        PopupMenuStyler.style(popupMenu);

        TribeRelationState state = tribe.getRelationState();
        String infoText = tribe.getName() + " (" + tribe.getTribeType().name() + ")  |  Relation: " + tribe.getRelationshipValue() + " (" + state.getName() + ")";
        String campText = "Camp at (" + tribe.getCampCoordinate().getQ() + ", " + tribe.getCampCoordinate().getR() + ")";
        popupMenu.add(StyledMenuItemFactory.createItem(null, infoText, campText, "", false, null));
        popupMenu.add(new JSeparator());

        popupMenu.add(createGiftMenu(gameSession, tribe, onActionSuccess));
        popupMenu.add(createTradeMenu(gameSession, tribe, onActionSuccess));
        popupMenu.add(new JSeparator());
        tribeMissionService.refreshMissionCondition(gameSession, tribe);
        popupMenu.add(createMissionItem(gameSession, tribe, onActionSuccess));
        popupMenu.add(createCancelMissionItem(gameSession, tribe, onActionSuccess));
        popupMenu.add(new JSeparator());
        popupMenu.add(createDeclareWarItem(gameSession, tribe, onActionSuccess));
        popupMenu.add(createRequestPeaceItem(gameSession, tribe, onActionSuccess));
        popupMenu.add(createRequestAllianceItem(gameSession, tribe, onActionSuccess));
        popupMenu.add(createViewRewardsItem(tribe));

        return popupMenu;
    }

    private JMenuItem createMissionItem(GameSession gameSession, Tribe tribe, Runnable onActionSuccess) {
        TribeMission mission = tribe.getActiveMission();

        if (mission == null) {
            boolean canAccept = tribeMissionService.canAcceptMission(gameSession, tribe);
            String reason = canAccept ? "" : "Relationship too low, at war, or on cooldown";

            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    boolean success = tribeMissionService.acceptMission(gameSession, tribe);
                    if (success && onActionSuccess != null) {
                        onActionSuccess.run();
                    }
                }
            };

            return StyledMenuItemFactory.createItem(null, "Accept Mission", TribeMissionDefinitionProvider.getDescription(tribe.getTribeType()), reason, canAccept, actionListener);
        }

        if (mission.getStatus() == MissionStatus.ACTIVE && tribeMissionService.isResourcePaymentMission(tribe.getTribeType())) {
            boolean canPay = tribeMissionService.canPayResourceMissionCost(gameSession, tribe);
            String reason = canPay ? "" : "Not enough resources";

            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    boolean success = tribeMissionService.payResourceMissionCost(gameSession, tribe);
                    if (success && onActionSuccess != null) {
                        onActionSuccess.run();
                    }
                }
            };

            return StyledMenuItemFactory.createItem(null, "Pay Mission Cost", mission.getTitle() + " (deadline: turn " + mission.getDeadlineTurn() + ")", reason, canPay, actionListener);
        }

        boolean canDeliver = tribeMissionService.canDeliverMission(gameSession, tribe);
        String reason = canDeliver ? "" : buildDeliverBlockedReason(mission);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = tribeMissionService.deliverMission(gameSession, tribe);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(null, "Deliver Mission", mission.getTitle(), reason, canDeliver, actionListener);
    }

    private JMenu createGiftMenu(GameSession gameSession, Tribe tribe, Runnable onActionSuccess) {
        JMenu giftMenu = new JMenu("Send Gift");
        boolean allowed = tribe.getRelationState().isGiftAllowed();

        ResourceType[] resourceTypes = ResourceType.values();
        for (int i = 0; i < resourceTypes.length; i++) {
            giftMenu.add(createGiftItem(gameSession, tribe, resourceTypes[i], allowed, onActionSuccess));
        }

        giftMenu.setEnabled(allowed);
        return giftMenu;
    }

    private JMenuItem createGiftItem(GameSession gameSession, Tribe tribe, ResourceType resourceType, boolean allowed, Runnable onActionSuccess) {
        String reason = allowed ? "" : "Cannot gift while at war";

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                promptAmountAndSendGift(gameSession, tribe, resourceType, onActionSuccess);
            }
        };

        return StyledMenuItemFactory.createItem(null, resourceType.name(), "Choose an amount to gift", reason, allowed, actionListener);
    }

    private void promptAmountAndSendGift(GameSession gameSession, Tribe tribe, ResourceType resourceType, Runnable onActionSuccess) {
        String input = JOptionPane.showInputDialog(null, "How much " + resourceType.name() + " do you want to gift?", "Send Gift", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(input.trim());
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(null, "Please enter a whole number.");
            return;
        }

        boolean success = tribeGiftService.sendGift(gameSession, tribe, resourceType, amount);
        if (success && onActionSuccess != null) {
            onActionSuccess.run();
        } else if (!success) {
            JOptionPane.showMessageDialog(null, "Gift failed. Check your resources and the tribe's relationship state.");
        }
    }

    private JMenu createTradeMenu(GameSession gameSession, Tribe tribe, Runnable onActionSuccess) {
        boolean allowed = tribe.getRelationState().isTradeAllowed() && tribe.getRelationshipValue() >= Constants.TRIBE_TRADE_MIN_RELATIONSHIP && !tribe.hasTradedThisTurn();
        JMenu tradeMenu = new JMenu("Trade");

        ResourceType[] resourceTypes = ResourceType.values();
        for (int i = 0; i < resourceTypes.length; i++) {
            tradeMenu.add(createOfferedResourceMenu(gameSession, tribe, resourceTypes[i], allowed, onActionSuccess));
        }

        tradeMenu.setEnabled(allowed);
        return tradeMenu;
    }

    private JMenu createOfferedResourceMenu(GameSession gameSession, Tribe tribe, ResourceType offeredType, boolean allowed, Runnable onActionSuccess) {
        JMenu offeredMenu = new JMenu("Sell " + offeredType.name());

        ResourceType[] resourceTypes = ResourceType.values();
        for (int i = 0; i < resourceTypes.length; i++) {
            ResourceType requestedType = resourceTypes[i];
            if (requestedType == offeredType) {
                continue;
            }
            offeredMenu.add(createTradeItem(gameSession, tribe, offeredType, requestedType, allowed, onActionSuccess));
        }

        return offeredMenu;
    }

    private JMenuItem createTradeItem(GameSession gameSession, Tribe tribe, ResourceType offeredType, ResourceType requestedType, boolean allowed, Runnable onActionSuccess) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                promptAmountAndTrade(gameSession, tribe, offeredType, requestedType, onActionSuccess);
            }
        };

        boolean canOfferThisPair = allowed && tribeTradeService.canTrade(gameSession, tribe, offeredType, requestedType, 1);
        return StyledMenuItemFactory.createItem(null, "For " + requestedType.name(), "Choose an amount to sell", "", canOfferThisPair, actionListener);
    }

    private void promptAmountAndTrade(GameSession gameSession, Tribe tribe, ResourceType offeredType, ResourceType requestedType, Runnable onActionSuccess) {
        String input = JOptionPane.showInputDialog(null, "How much " + offeredType.name() + " do you want to sell?", "Trade with " + tribe.getName(), JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        int offeredAmount;
        try {
            offeredAmount = Integer.parseInt(input.trim());
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(null, "Please enter a whole number.");
            return;
        }

        boolean success = tribeTradeService.trade(gameSession, tribe, offeredType, requestedType, offeredAmount);
        if (success && onActionSuccess != null) {
            onActionSuccess.run();
        } else if (!success) {
            JOptionPane.showMessageDialog(null, "Trade failed. Check the amount and your resources.");
        }
    }

    private String buildDeliverBlockedReason(civgame.model.tribe.TribeMission mission) {
        if (mission.getStatus() != civgame.model.tribe.MissionStatus.READY_TO_DELIVER) {
            return "Condition not met yet (deadline: turn " + mission.getDeadlineTurn() + ")";
        }
        return "Not enough storage space for the reward";
    }

    private JMenuItem createDeclareWarItem(GameSession gameSession, Tribe tribe, Runnable onActionSuccess) {
        boolean allowed = !tribe.getRelationState().isAtWar();
        String reason = allowed ? "" : "Already at war";

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int confirmation = JOptionPane.showConfirmDialog(null, "Declare war on " + tribe.getName() + "? This cannot be undone.", "Declare War", JOptionPane.YES_NO_OPTION);
                if (confirmation != JOptionPane.YES_OPTION) {
                    return;
                }
                boolean success = tribeDiplomacyService.declareWar(gameSession, tribe);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(null, "Declare War", "Relationship drops to -100 immediately", reason, allowed, actionListener);
    }

    private JMenuItem createRequestPeaceItem(GameSession gameSession, Tribe tribe, Runnable onActionSuccess) {
        boolean allowed = tribeDiplomacyService.canRequestPeace(gameSession, tribe);
        String reason = buildPeaceUnavailabilityReason(tribe, allowed);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = tribeDiplomacyService.requestPeace(gameSession, tribe);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        String detail = "Cost: " + Constants.TRIBE_PEACE_FOOD_COST + " Food, " + Constants.TRIBE_PEACE_WOOD_COST + " Wood, " + Constants.TRIBE_PEACE_IRON_COST + " Iron";
        return StyledMenuItemFactory.createItem(null, "Request Peace", detail, reason, allowed, actionListener);
    }

    private String buildPeaceUnavailabilityReason(Tribe tribe, boolean allowed) {
        if (allowed) {
            return "";
        }
        if (!tribe.getRelationState().isAtWar()) {
            return "Not at war with this tribe";
        }
        return "Not enough resources";
    }

    private JMenuItem createRequestAllianceItem(GameSession gameSession, Tribe tribe, Runnable onActionSuccess) {
        boolean allowed = tribeDiplomacyService.canRequestAlliance(gameSession, tribe);
        String reason = allowed ? "" : "Relationship must be at least " + Constants.TRIBE_ALLIANCE_MIN_RELATIONSHIP + ", and no conflicting alliance";

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = tribeDiplomacyService.requestAlliance(gameSession, tribe);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(null, "Request Alliance", "Unlocks this tribe's permanent bonus", reason, allowed, actionListener);
    }

    private JMenuItem createViewRewardsItem(Tribe tribe) {
        String rewardsText = TribeRewardDescriptionProvider.describe(tribe.getTribeType());
        return StyledMenuItemFactory.createItem(null, "View Tribe Rewards", rewardsText, "", true, null);
    }

    private JMenuItem createCancelMissionItem(GameSession gameSession, Tribe tribe, Runnable onActionSuccess) {
        boolean hasMission = tribe.getActiveMission() != null;

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean success = tribeMissionService.cancelMission(gameSession, tribe);
                if (success && onActionSuccess != null) {
                    onActionSuccess.run();
                }
            }
        };

        return StyledMenuItemFactory.createItem(null, "Cancel Mission", "Relationship: -" + (-Constants.MISSION_CANCEL_RELATIONSHIP_PENALTY), hasMission ? "" : "No active mission", hasMission, actionListener);
    }
}