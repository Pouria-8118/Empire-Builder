package civgame.view.hud;

public class NotificationEntry {

    private final String iconPath;
    private final String text;
    private final String deltaText;
    private final boolean deltaPositive;

    public NotificationEntry(String iconPath, String text, String deltaText, boolean deltaPositive) {
        this.iconPath = iconPath;
        this.text = text;
        this.deltaText = deltaText;
        this.deltaPositive = deltaPositive;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getText() {
        return text;
    }

    public String getDeltaText() {
        return deltaText;
    }

    public boolean isDeltaPositive() {
        return deltaPositive;
    }
}