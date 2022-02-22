package ua.utilix.bot;

import ua.utilix.Handlers.Handler;
import ua.utilix.model.Device;
import ua.utilix.model.Location;
import ua.utilix.model.User;

public class LocationContext {
    private final ChatBot bot;
    private final User user;
    private final Location location;
    private final String input;

    public static LocationContext of(Handler handler, ChatBot bot, User user, Location location, String text) {
        return new LocationContext(handler, bot, user, location, text);
    }

    private LocationContext(Handler handler, ChatBot bot, User user, Location location, String input) {
        this.bot = bot;
        this.user = user;
        this.location = location;
        this.input = input;
    }

    public ChatBot getBot() {
        return bot;
    }

    public Location getLocation() {
        return location;
    }

    public User getUser() {
        return user;
    }

    public String getInput() {
        return input;
    }
}
