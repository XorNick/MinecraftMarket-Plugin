package com.minecraftmarket.minecraftmarket.nukkit.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MCMApi {
    private final JsonParser PARSER = new JsonParser();
    private final String BASE_URL = "https://minecraftmarket.com/api/1.5/";
    private final String API_KEY;
    private final boolean DEBUG;

    public MCMApi(String apiKey, boolean debug) {
        API_KEY = apiKey;
        DEBUG = debug;
    }

    public boolean authAPI() {
        try {
            JsonObject response = (JsonObject) makeRequest("/auth");
            JsonArray results = (JsonArray) response.get("result");
            JsonObject result = (JsonObject) results.get(0);
            String status = result.get("status").getAsString();
            if (status.equals("ok")) {
                return true;
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) makeRequest("/gui");
            String status = response.get("status").getAsString();
            if (status.equals("ok")) {
                Map<Long, List<Item>> items = new HashMap<>();
                JsonArray itemsArray = (JsonArray) response.get("result");
                for (Object itemObj : itemsArray) {
                    JsonObject item = (JsonObject) itemObj;
                    long id = item.get("id").getAsLong();
                    String name = item.get("name").getAsString();
                    String icon = item.get("iconid").getAsString();
                    String description = item.get("description").getAsString();
                    String url = item.get("url").getAsString();
                    String price = item.get("price").getAsString();
                    String currency = item.get("currency").getAsString();
                    String category = item.get("category").getAsString();
                    long categoryID = item.get("categoryid").getAsLong();
                    if (items.containsKey(categoryID)) {
                        items.get(categoryID).add(new Item(id, name, icon, description, url, price, currency, category, categoryID));
                    } else {
                        List<Item> catItems = new ArrayList<>();
                        catItems.add(new Item(id, name, icon, description, url, price, currency, category, categoryID));
                        items.put(categoryID, catItems);
                    }
                }

                JsonArray categoriesArray = (JsonArray) response.get("categories");
                for (Object categoryObj : categoriesArray) {
                    JsonObject category = (JsonObject) categoryObj;
                    long id = category.get("id").getAsLong();
                    String name = category.get("name").getAsString();
                    String icon = category.get("iconid").getAsString();
                    long order = category.get("order").getAsLong();
                    List<Item> categoryItems;
                    if (items.containsKey(id)) {
                        categoryItems = items.get(id);
                    } else {
                        categoryItems = new ArrayList<>();
                    }

                    categories.add(new Category(id, name, icon, order, categoryItems));
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return categories;
    }

    public List<RecentDonor> getRecentDonors() {
        List<RecentDonor> recentDonors = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) makeRequest("/recentdonor");
            JsonArray recentDonorsArray = (JsonArray) response.get("result");
            for (Object recentDonorObj : recentDonorsArray) {
                JsonObject recentDonor = (JsonObject) recentDonorObj;
                long id = recentDonor.get("id").getAsLong();
                String user = recentDonor.get("username").getAsString();
                String item = recentDonor.get("item").getAsString();
                String price = String.valueOf(recentDonor.get("price").getAsLong());
                String currency = recentDonor.get("currency").getAsString();
                String date = recentDonor.get("date").getAsString();
                recentDonors.add(new RecentDonor(id, user, item, price, currency, date));
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return recentDonors;
    }

    public List<PendingPurchase> getPendingPurchases() {
        List<PendingPurchase> pendingPurchases = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) makeRequest("/pending");
            String status = response.get("status").getAsString();
            if (status.equals("ok")) {
                JsonArray pendingPurchasesArray = (JsonArray) response.get("result");
                for (Object pendingPurchaseObj : pendingPurchasesArray) {
                    JsonObject pendingPurchase = (JsonObject) pendingPurchaseObj;
                    long purchaseId = pendingPurchase.get("id").getAsLong();
                    String purchaseUser = pendingPurchase.get("username").getAsString();
                    JsonArray purchaseCommands = (JsonArray) pendingPurchase.get("commands");

                    List<Command> pendingCommands = new ArrayList<>();
                    for (Object purchaseCommandObj : purchaseCommands) {
                        JsonObject purchaseCommand = (JsonObject) purchaseCommandObj;
                        long id = purchaseCommand.get("id").getAsLong();
                        String command = purchaseCommand.get("command").getAsString();
                        long delay = purchaseCommand.get("delay").getAsLong();
                        boolean online = purchaseCommand.get("online").getAsLong() >= 2;
                        long slots = purchaseCommand.get("slots").getAsLong();
                        boolean repeat = purchaseCommand.get("repeat").getAsString().equals("True");
                        long period = purchaseCommand.get("repeatperiod").getAsLong();
                        long cycles = purchaseCommand.get("repeatcycles").getAsLong();
                        pendingCommands.add(new Command(id, command, delay, online, slots, repeat, period, cycles));
                    }

                    pendingPurchases.add(new PendingPurchase(purchaseId, purchaseUser, pendingCommands));
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return pendingPurchases;
    }

    public List<ExpiredPurchase> getExpiredPurchases() {
        List<ExpiredPurchase> expiredPurchases = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) makeRequest("/expiry");
            String status = response.get("status").getAsString();
            if (status.equals("ok")) {
                JsonArray expiredPurchasesArray = (JsonArray) response.get("result");
                for (Object expiredPurchaseObj : expiredPurchasesArray) {
                    JsonObject expiredPurchase = (JsonObject) expiredPurchaseObj;
                    long purchaseId = expiredPurchase.get("id").getAsLong();
                    String purchaseUser = expiredPurchase.get("username").getAsString();
                    JsonArray purchaseCommands = (JsonArray) expiredPurchase.get("commands");

                    List<Command> expiredCommands = new ArrayList<>();
                    for (Object purchaseCommandObj : purchaseCommands) {
                        JsonObject purchaseCommand = (JsonObject) purchaseCommandObj;
                        long id = purchaseCommand.get("id").getAsLong();
                        String command = purchaseCommand.get("command").getAsString();
                        long delay = purchaseCommand.get("delay").getAsLong();
                        boolean online = purchaseCommand.get("online").getAsLong() >= 2;
                        long slots = purchaseCommand.get("slots").getAsLong();
                        boolean repeat = purchaseCommand.get("repeat").getAsString().equals("True");
                        long period = purchaseCommand.get("repeatperiod").getAsLong();
                        long cycles = purchaseCommand.get("repeatcycles").getAsLong();
                        expiredCommands.add(new Command(id, command, delay, online, slots, repeat, period, cycles));
                    }

                    expiredPurchases.add(new ExpiredPurchase(purchaseId, purchaseUser, expiredCommands));
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return expiredPurchases;
    }

    public void setExecuted(long itemID, boolean repeatOnError) {
        try {
            JsonObject response = (JsonObject) makeRequest(String.format("/executed/%s", itemID));
            String status = response.get("status").getAsString();
            if (!status.equals("ok") && repeatOnError) {
                setExecuted(itemID, false);
            }
        } catch (Exception e) {
            if (repeatOnError) {
                setExecuted(itemID, false);
            }
            if (DEBUG) {
                e.printStackTrace();
            }
        }
    }

    private Object makeRequest(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + API_KEY + url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setUseCaches(false);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        return PARSER.parse(bufferedReader);
    }

    public class Category {
        private final long id;
        private final String name;
        private final String icon;
        private final long order;
        private final List<Item> items;

        Category(long id, String name, String icon, long order, List<Item> items) {
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

        Item(long id, String name, String icon, String description, String url, String price, String currency, String category, long categoryID) {
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

        RecentDonor(long id, String user, String item, String price, String currency, String date) {
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

        PendingPurchase(long id, String user, List<Command> commands) {
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

        ExpiredPurchase(long id, String user, List<Command> commands) {
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

        Command(long id, String command, long delay, boolean online, long slots, boolean repeat, long period, long cycles) {
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