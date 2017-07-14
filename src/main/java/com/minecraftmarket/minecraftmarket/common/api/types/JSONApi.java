package com.minecraftmarket.minecraftmarket.common.api.types;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONApi extends MCMarketApi {

    public JSONApi(String apiKey, boolean debug) {
        super(apiKey, debug);
    }

    @Override
    public boolean authAPI() {
        try {
            JSONObject response = (JSONObject) new JSONParser().parse(makeRequest("/auth"));
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

    @Override
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONParser().parse(makeRequest("/gui"));
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

    @Override
    public List<RecentDonor> getRecentDonors() {
        List<RecentDonor> recentDonors = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONParser().parse(makeRequest("/recentdonor"));
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

    @Override
    public List<PendingPurchase> getPendingPurchases() {
        List<PendingPurchase> pendingPurchases = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONParser().parse(makeRequest("/pending"));
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

    @Override
    public List<ExpiredPurchase> getExpiredPurchases() {
        List<ExpiredPurchase> expiredPurchases = new ArrayList<>();
        try {
            JSONObject response = (JSONObject) new JSONParser().parse(makeRequest("/expiry"));
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

    @Override
    public void setExecuted(long itemID, boolean repeatOnError) {
        try {
            JSONObject response = (JSONObject) new JSONParser().parse(makeRequest(String.format("/executed/%s", itemID)));
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
}