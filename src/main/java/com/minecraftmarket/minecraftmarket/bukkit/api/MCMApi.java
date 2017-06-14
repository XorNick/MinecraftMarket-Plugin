package com.minecraftmarket.minecraftmarket.bukkit.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MCMApi {
    private final String BASE_URL = "https://minecraftmarket.com/api/1.5/";
    private final String API_KEY;
    private final boolean DEBUG;

    public MCMApi(String apiKey, boolean debug) {
        API_KEY = apiKey;
        DEBUG = debug;
    }

    public boolean authAPI() {
        try {
            JSONObject response = (JSONObject) makeRequest("/auth");
            JSONArray results = (JSONArray) response.get("result");
            JSONObject result = (JSONObject) results.get(0);
            String status = (String) result.get("status");
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
            JSONObject response = (JSONObject) makeRequest("/gui");
            String status = (String) response.get("status");
            if (status.equals("ok")) {
                Map<Long, List<Item>> items = new HashMap<>();
                JSONArray itemsArray = (JSONArray) response.get("result");
                for (Object itemObj : itemsArray) {
                    JSONObject item = (JSONObject) itemObj;
                    long id = (long) item.get("id");
                    String name = (String) item.get("name");
                    String icon = (String) item.get("iconid");
                    String description = (String) item.get("description");
                    String url = (String) item.get("url");
                    String price = (String) item.get("price");
                    String currency = (String) item.get("currency");
                    String category = (String) item.get("category");
                    long categoryID = (long) item.get("categoryid");
                    if (items.containsKey(categoryID)) {
                        items.get(categoryID).add(new Item(id, name, icon, description, url, price, currency, category, categoryID));
                    } else {
                        List<Item> catItems = new ArrayList<>();
                        catItems.add(new Item(id, name, icon, description, url, price, currency, category, categoryID));
                        items.put(categoryID, catItems);
                    }
                }

                JSONArray categoriesArray = (JSONArray) response.get("categories");
                for (Object categoryObj : categoriesArray) {
                    JSONObject category = (JSONObject) categoryObj;
                    long id = (long) category.get("id");
                    String name = (String) category.get("name");
                    String icon = (String) category.get("iconid");
                    long order = (long) category.get("order");
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
            JSONObject response = (JSONObject) makeRequest("/recentdonor");
            JSONArray recentDonorsArray = (JSONArray) response.get("result");
            for (Object recentDonorObj : recentDonorsArray) {
                JSONObject recentDonor = (JSONObject) recentDonorObj;
                long id = (long) recentDonor.get("id");
                String user = (String) recentDonor.get("username");
                String item = (String) recentDonor.get("item");
                String price = String.valueOf(recentDonor.get("price"));
                String currency = (String) recentDonor.get("currency");
                String date = (String) recentDonor.get("date");
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
            JSONObject response = (JSONObject) makeRequest("/pending");
            String status = (String) response.get("status");
            if (status.equals("ok")) {
                JSONArray pendingPurchasesArray = (JSONArray) response.get("result");
                for (Object pendingPurchaseObj : pendingPurchasesArray) {
                    JSONObject pendingPurchase = (JSONObject) pendingPurchaseObj;
                    long purchaseId = (long) pendingPurchase.get("id");
                    String purchaseUser = (String) pendingPurchase.get("username");
                    JSONArray purchaseCommands = (JSONArray) pendingPurchase.get("commands");

                    List<Command> pendingCommands = new ArrayList<>();
                    for (Object purchaseCommandObj : purchaseCommands) {
                        JSONObject purchaseCommand = (JSONObject) purchaseCommandObj;
                        long id = (long) purchaseCommand.get("id");
                        String command = (String) purchaseCommand.get("command");
                        long delay = (long) purchaseCommand.get("delay");
                        boolean online = (long) purchaseCommand.get("online") >= 2;
                        long slots = (long) purchaseCommand.get("slots");
                        boolean repeat = purchaseCommand.get("repeat").equals("True");
                        long period = (long) purchaseCommand.get("repeatperiod");
                        long cycles = (long) purchaseCommand.get("repeatcycles");
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
            JSONObject response = (JSONObject) makeRequest("/expiry");
            String status = (String) response.get("status");
            if (status.equals("ok")) {
                JSONArray expiredPurchasesArray = (JSONArray) response.get("result");
                for (Object expiredPurchaseObj : expiredPurchasesArray) {
                    JSONObject expiredPurchase = (JSONObject) expiredPurchaseObj;
                    long purchaseId = (long) expiredPurchase.get("id");
                    String purchaseUser = (String) expiredPurchase.get("username");
                    JSONArray purchaseCommands = (JSONArray) expiredPurchase.get("commands");

                    List<Command> expiredCommands = new ArrayList<>();
                    for (Object purchaseCommandObj : purchaseCommands) {
                        JSONObject purchaseCommand = (JSONObject) purchaseCommandObj;
                        long id = (long) purchaseCommand.get("id");
                        String command = (String) purchaseCommand.get("command");
                        long delay = (long) purchaseCommand.get("delay");
                        boolean online = (long) purchaseCommand.get("online") >= 2;
                        long slots = (long) purchaseCommand.get("slots");
                        boolean repeat = purchaseCommand.get("repeat").equals("True");
                        long period = (long) purchaseCommand.get("repeatperiod");
                        long cycles = (long) purchaseCommand.get("repeatcycles");
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
            JSONObject response = (JSONObject) makeRequest(String.format("/executed/%s", itemID));
            String status = (String) response.get("status");
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

    private Object makeRequest(String url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + API_KEY + url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setUseCaches(false);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        return JSONValue.parseWithException(bufferedReader);
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