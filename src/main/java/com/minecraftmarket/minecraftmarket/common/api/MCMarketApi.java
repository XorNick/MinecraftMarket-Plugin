package com.minecraftmarket.minecraftmarket.common.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public abstract class MCMarketApi {
    private final String BASE_URL = "https://minecraftmarket.com/api/1.5/";
    private final String API_KEY;
    protected final boolean DEBUG;

    public MCMarketApi(String apiKey, boolean debug) {
        API_KEY = apiKey;
        DEBUG = debug;
    }

    public abstract boolean authAPI();

    public abstract List<Category> getCategories();

    public abstract List<RecentDonor> getRecentDonors();

    public abstract List<PendingPurchase> getPendingPurchases();

    public abstract List<ExpiredPurchase> getExpiredPurchases();

    public abstract void setExecuted(long itemID, boolean repeatOnError);

    protected BufferedReader makeRequest(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + API_KEY + url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setUseCaches(false);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        return new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    public class Category {
        private final long id;
        private final String name;
        private final String icon;
        private final long order;
        private final List<Item> items;

        public Category(long id, String name, String icon, long order, List<Item> items) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.order = order;
            this.items = items;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }

        public long getOrder() {
            return order;
        }

        public List<Item> getItems() {
            return items;
        }
    }

    public class Item {
        private final long id;
        private final String name;
        private final String icon;
        private final String description;
        private final String url;
        private final String price;
        private final String currency;
        private final String category;
        private final long categoryID;

        public Item(long id, String name, String icon, String description, String url, String price, String currency, String category, long categoryID) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.description = description;
            this.url = url;
            this.price = price;
            this.currency = currency;
            this.category = category;
            this.categoryID = categoryID;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }

        public String getDescription() {
            return description;
        }

        public String getUrl() {
            return url;
        }

        public String getPrice() {
            return price;
        }

        public String getCurrency() {
            return currency;
        }

        public String getCategory() {
            return category;
        }

        public long getCategoryID() {
            return categoryID;
        }
    }

    public class RecentDonor {
        private final long id;
        private final String user;
        private final String item;
        private final String price;
        private final String currency;
        private final String date;

        public RecentDonor(long id, String user, String item, String price, String currency, String date) {
            this.id = id;
            this.user = user;
            this.item = item;
            this.price = price;
            this.currency = currency;
            this.date = date;
        }

        public long getId() {
            return id;
        }

        public String getUser() {
            return user;
        }

        public String getItem() {
            return item;
        }

        public String getPrice() {
            return price;
        }

        public String getCurrency() {
            return currency;
        }

        public String getDate() {
            return date;
        }
    }

    public class PendingPurchase {
        private final long id;
        private final String user;
        private final List<Command> commands;

        public PendingPurchase(long id, String user, List<Command> commands) {
            this.id = id;
            this.user = user;
            this.commands = commands;
        }

        public long getId() {
            return id;
        }

        public String getUser() {
            return user;
        }

        public List<Command> getCommands() {
            return commands;
        }
    }

    public class ExpiredPurchase {
        private final long id;
        private final String user;
        private final List<Command> commands;

        public ExpiredPurchase(long id, String user, List<Command> commands) {
            this.id = id;
            this.user = user;
            this.commands = commands;
        }

        public long getId() {
            return id;
        }

        public String getUser() {
            return user;
        }

        public List<Command> getCommands() {
            return commands;
        }
    }

    public class Command {
        private final long id;
        private final String command;
        private final long delay;
        private final boolean online;
        private final long slots;
        private final boolean repeat;
        private final long period;
        private final long cycles;

        public Command(long id, String command, long delay, boolean online, long slots, boolean repeat, long period, long cycles) {
            this.id = id;
            this.command = command;
            this.delay = delay;
            this.online = online;
            this.slots = slots;
            this.repeat = repeat;
            this.period = period;
            this.cycles = cycles;
        }

        public long getId() {
            return id;
        }

        public String getCommand() {
            return command;
        }

        public long getDelay() {
            return delay;
        }

        public boolean isOnline() {
            return online;
        }

        public long getSlots() {
            return slots;
        }

        public boolean isRepeat() {
            return repeat;
        }

        public long getPeriod() {
            return period;
        }

        public long getCycles() {
            return cycles;
        }
    }
}