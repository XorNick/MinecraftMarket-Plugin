package com.minecraftmarket.minecraftmarket.common.api.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GSONApi extends MCMarketApi {
    private final JsonParser PARSER = new JsonParser();

    public GSONApi(String key, boolean debug) {
        super(key, debug);
    }

    @Override
    public boolean authAPI() {
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/auth"));
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

    @Override
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/gui"));
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

    @Override
    public List<RecentDonor> getRecentDonors() {
        List<RecentDonor> recentDonors = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/recentdonor"));
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

    @Override
    public List<PendingPurchase> getPendingPurchases() {
        List<PendingPurchase> pendingPurchases = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/pending"));
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

    @Override
    public List<ExpiredPurchase> getExpiredPurchases() {
        List<ExpiredPurchase> expiredPurchases = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/expiry"));
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

    @Override
    public void setExecuted(long itemID, boolean repeatOnError) {
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest(String.format("/executed/%s", itemID)));
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
}