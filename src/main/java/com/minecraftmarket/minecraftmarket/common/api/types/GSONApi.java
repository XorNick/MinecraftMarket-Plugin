package com.minecraftmarket.minecraftmarket.common.api.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GSONApi extends MCMarketApi {
    private final JsonParser PARSER = new JsonParser();

    public GSONApi(String key, String userAgent, boolean debug) {
        super(key, userAgent, debug);
    }

    @Override
    public boolean authAPI() {
        try {
            makeRequest("/market", "GET", "");
            return true;
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Market getMarket() {
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/market", "GET", ""));
            long id = response.get("id").getAsLong();
            String name = response.get("name").getAsString();
            String url = response.get("url").getAsString();

            JsonObject currencyObj = response.getAsJsonObject("currency");
            long currencyID = currencyObj.get("id").getAsLong();
            String currencyCode = currencyObj.get("code").getAsString();
            Currency currency = new Currency(currencyID, currencyCode);

            return new Market(id, name, currency, url);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/categories", "GET", "limit=25"));
            long count = response.get("count").getAsLong();
            long pages = (count / 25) + 1;

            for (int i = 1; i <= pages; i++) {
                if (i > 1) response = (JsonObject) PARSER.parse(makeRequest("/categories", "GET", "limit=25&offset=" + (25 * (i - 1))));

                JsonArray results = response.getAsJsonArray("results");
                for (JsonElement categoryElement : results) {
                    JsonObject categoryObj = categoryElement.getAsJsonObject();
                    Category category = getCategory(categoryObj.get("id").getAsLong());
                    if (category != null) {
                        categories.add(category);
                    }
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
    public Category getCategory(long categoryID) {
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest(String.format("/categories/%s", categoryID), "GET", ""));
            long id = response.get("id").getAsLong();
            String name = response.get("name").getAsString();
            String description = response.get("gui_description").getAsString();
            String icon = response.get("gui_icon").getAsString();
            long order = response.get("order").getAsLong();

            List<Category> subCategories = new ArrayList<>();
            for (JsonElement subCategoryElement : response.getAsJsonArray("subcategories")) {
                JsonObject subCategoryObj = subCategoryElement.getAsJsonObject();
                Category subCategory = getCategory(subCategoryObj.get("id").getAsLong());
                if (subCategory != null) {
                    subCategories.add(subCategory);
                }
            }

            List<Item> items = new ArrayList<>();
            for (JsonElement itemElement : response.getAsJsonArray("items")) {
                JsonObject itemObj = itemElement.getAsJsonObject();
                Item item = getItem(itemObj.get("id").getAsLong());
                if (item != null) {
                    items.add(item);
                }
            }

            return new Category(id, name, description, icon, subCategories, items, order);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/items", "GET", "limit=25"));
            long count = response.get("count").getAsLong();
            long pages = (count / 25) + 1;

            for (int i = 1; i <= pages; i++) {
                if (i > 1) response = (JsonObject) PARSER.parse(makeRequest("/items", "GET", "limit=25&offset=" + (25 * (i - 1))));

                JsonArray results = response.getAsJsonArray("results");
                for (JsonElement itemElement : results) {
                    JsonObject itemObj = itemElement.getAsJsonObject();
                    Item item = getItem(itemObj.get("id").getAsLong());
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return items;
    }

    @Override
    public Item getItem(long itemID) {
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest(String.format("/items/%s", itemID), "GET", ""));
            long id = response.get("id").getAsLong();
            String name = response.get("name").getAsString();
            String description = response.get("gui_description").getAsString();
            String icon = response.get("gui_icon").getAsString();
            String url = response.get("gui_url").getAsString();
            String price = response.get("price").getAsString();
            long order = response.get("order").getAsLong();
            return new Item(id, name, description, icon, url, price, order);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<Transaction> getTransactions(Filter filter) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            String query = buildQueryFromFilters(filter);
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/transactions", "GET", "limit=25&" + query));
            long count = response.get("count").getAsLong();
            long pages = (count / 25) + 1;

            for (int i = 1; i <= pages; i++) {
                if (i > 1) response = (JsonObject) PARSER.parse(makeRequest("/transactions", "GET", "limit=25&offset=" + (25 * (i - 1)) + query));

                JsonArray results = response.getAsJsonArray("results");
                for (JsonElement transactionElement : results) {
                    JsonObject transactionObj = transactionElement.getAsJsonObject();
                    Transaction transaction = getTransaction(transactionObj.get("id").getAsLong());
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return transactions;
    }

    @Override
    public Transaction getTransaction(long transactionID) {
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest(String.format("/transactions/%s", transactionID), "GET", ""));
            long id = response.get("id").getAsLong();
            String status = response.get("status").getAsString();
            String gateway = response.get("gateway").getAsString();
            String transaction_id = response.get("transaction_id").getAsString();
            String price = response.get("price").getAsString();
            String date = response.get("date").getAsString();

            JsonObject currencyObj = response.get("currency").getAsJsonObject();
            long currencyID = currencyObj.get("id").getAsLong();
            String currencyCode = currencyObj.get("code").getAsString();
            Currency currency = new Currency(currencyID, currencyCode);

            JsonObject playerObj = response.get("player").getAsJsonObject();
            long playerID = playerObj.get("id").getAsLong();
            String playerName = playerObj.get("username").getAsString();
            String playerUUID = playerObj.get("uuid").getAsString();
            boolean playerVerified = playerObj.get("verified").getAsBoolean();
            String playerSkinUrl = playerObj.get("skin_url").getAsString();
            String playerSkinSource = playerObj.get("skin_source").getAsString();
            String playerCapeUrl = playerObj.get("cape_url").getAsString();
            String playerCapeSource = playerObj.get("cape_source").getAsString();
            MMPlayer player = new MMPlayer(playerID, playerName, playerUUID, playerVerified, playerSkinUrl, playerSkinSource, playerCapeUrl, playerCapeSource);

            List<Purchase> purchases = new ArrayList<>();
            JsonArray purchasesArray = response.getAsJsonArray("purchases");
            for (JsonElement purchaseElement : purchasesArray) {
                JsonObject purchaseObj = purchaseElement.getAsJsonObject();
                Purchase purchase = getPurchase(purchaseObj.get("id").getAsLong());
                if (purchase != null) {
                    purchases.add(purchase);
                }
            }

            return new Transaction(id, status, gateway, transaction_id, price, currency, date, player, purchases);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<Purchase> getPurchases() {
        List<Purchase> purchases = new ArrayList<>();
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/purchases", "GET", "limit=25"));
            long count = response.get("count").getAsLong();
            long pages = (count / 25) + 1;

            for (int i = 1; i <= pages; i++) {
                if (i > 1) response = (JsonObject) PARSER.parse(makeRequest("/purchases", "GET", "limit=25&offset=" + (25 * (i - 1))));

                JsonArray results = response.getAsJsonArray("results");
                for (JsonElement purchaseElement : results) {
                    JsonObject purchaseObj = purchaseElement.getAsJsonObject();
                    Purchase purchase = getPurchase(purchaseObj.get("id").getAsLong());
                    if (purchase != null) {
                        purchases.add(purchase);
                    }
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return purchases;
    }

    @Override
    public Purchase getPurchase(long purchaseID) {
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest(String.format("/purchases/%s", purchaseID), "GET", ""));
            long id = response.get("id").getAsLong();
            String name = response.get("name").getAsString();
            String price = response.get("price").getAsString();
            String date = response.get("date").getAsString();

            JsonObject currencyObj = response.get("currency").getAsJsonObject();
            long currencyID = currencyObj.get("id").getAsLong();
            String currencyCode = currencyObj.get("code").getAsString();
            Currency currency = new Currency(currencyID, currencyCode);

            JsonObject playerObj = response.get("player").getAsJsonObject();
            long playerID = playerObj.get("id").getAsLong();
            String playerName = playerObj.get("username").getAsString();
            String playerUUID = playerObj.get("uuid").getAsString();
            boolean playerVerified = playerObj.get("verified").getAsBoolean();
            String playerSkinUrl = playerObj.get("skin_url").getAsString();
            String playerSkinSource = playerObj.get("skin_source").getAsString();
            String playerCapeUrl = playerObj.get("cape_url").getAsString();
            String playerCapeSource = playerObj.get("cape_source").getAsString();
            MMPlayer player = new MMPlayer(playerID, playerName, playerUUID, playerVerified, playerSkinUrl, playerSkinSource, playerCapeUrl, playerCapeSource);

            return new Purchase(id, name, price, currency, date, player);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<Command> getCommands(Filter... filters) {
        List<Command> commands = new ArrayList<>();
        try {
            String query = buildQueryFromFilters(filters);
            JsonObject response = (JsonObject) PARSER.parse(makeRequest("/commands", "GET", "limit=25&" + query));
            long count = response.get("count").getAsLong();
            long pages = (count / 25) + 1;

            for (int i = 1; i <= pages; i++) {
                if (i > 1) response = (JsonObject) PARSER.parse(makeRequest("/commands", "GET", "limit=25&offset=" + (25 * (i - 1)) + query));

                JsonArray results = response.getAsJsonArray("results");
                for (JsonElement commandElement : results) {
                    JsonObject commandObj = commandElement.getAsJsonObject();
                    Command command = getCommand(commandObj.get("id").getAsLong());
                    if (command != null) {
                        commands.add(command);
                    }
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return commands;
    }

    @Override
    public Command getCommand(long commandID) {
        try {
            JsonObject response = (JsonObject) PARSER.parse(makeRequest(String.format("/commands/%s", commandID), "GET", ""));
            long id = response.get("id").getAsLong();
            String type = response.get("type").getAsString();
            String command = response.get("command").getAsString();
            long delay = response.get("delay").getAsLong();
            long requiredSlots = response.get("required_slots").getAsLong();
            boolean requiredOnline = response.get("required_online").getAsLong() > 0;
            boolean repeat = response.get("repeat").getAsBoolean();
            long repeatPeriod = response.get("repeat_period").getAsLong();
            long repeatCycles = response.get("repeat_cycles").getAsLong();
            boolean executed = response.get("executed").getAsBoolean();

            JsonObject playerObj = response.get("player").getAsJsonObject();
            long playerID = playerObj.get("id").getAsLong();
            String playerName = playerObj.get("username").getAsString();
            String playerUUID = playerObj.get("uuid").getAsString();
            boolean playerVerified = playerObj.get("verified").getAsBoolean();
            String playerSkinUrl = playerObj.get("skin_url").getAsString();
            String playerSkinSource = playerObj.get("skin_source").getAsString();
            String playerCapeUrl = playerObj.get("cape_url").getAsString();
            String playerCapeSource = playerObj.get("cape_source").getAsString();
            MMPlayer player = new MMPlayer(playerID, playerName, playerUUID, playerVerified, playerSkinUrl, playerSkinSource, playerCapeUrl, playerCapeSource);

            return new Command(id, player, type, command, delay, requiredSlots, requiredOnline, repeat, repeatPeriod, repeatCycles, executed);
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void setExecuted(long commandID) {
        try {
            makeRequest(String.format("/commands/%s", commandID), "PUT", "{\"executed\":1}");
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
    }
}