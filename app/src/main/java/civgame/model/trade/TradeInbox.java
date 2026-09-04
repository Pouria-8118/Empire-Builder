package civgame.model.trade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradeInbox implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final LocalDateTime createdAt;
    private final List<TradeOffer> pendingOffers;

    public TradeInbox() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.pendingOffers = new ArrayList<TradeOffer>();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addOffer(TradeOffer offer) {
        pendingOffers.add(offer);
    }

    public List<TradeOffer> getPendingOffers() {
        List<TradeOffer> result = new ArrayList<TradeOffer>();
        for (int i = 0; i < pendingOffers.size(); i++) {
            if (!pendingOffers.get(i).isCancelled()) {
                result.add(pendingOffers.get(i));
            }
        }
        return result;
    }

    public TradeOffer getOfferById(String offerId) {
        for (int i = 0; i < pendingOffers.size(); i++) {
            TradeOffer offer = pendingOffers.get(i);
            if (offer.getId().equals(offerId) && !offer.isCancelled()) {
                return offer;
            }
        }
        return null;
    }

    public void removeOffer(String offerId) {
        for (int i = pendingOffers.size() - 1; i >= 0; i--) {
            if (pendingOffers.get(i).getId().equals(offerId)) {
                pendingOffers.remove(i);
            }
        }
    }
}