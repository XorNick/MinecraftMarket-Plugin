package com.minecraftmarket.minecraftmarket.common.api.types;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONApi extends MCMarketApi {
    private final JSONParser PARSER = new JSONParser();

    public JSONApi(String key, String userAgent, boolean debug) {
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest("/market", "GET", ""));
            long id = (Long) response.get("id");
            String name = (String) response.get("name");
            String url = (String) response.get("url");

            JSONObject currencyObj = (JSONObject) response.get("currency");
            long currencyID = (Long) currencyObj.get("id");
            String currencyCode = (String) currencyObj.get("code");
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest("/categories", "GET", "limit=25"));
            long count = (Long) response.get("count");
            long pages = (count / 25) + 1;

            for (int i = 1; i <= pages; i++) {
                if (i > 1) response = (JSONObject) PARSER.parse(makeRequest("/categories", "GET", "limit=25&offset=" + (25 * (i - 1))));

                JSONArray results = (JSONArray) response.get("results");
                for (Object categoryElement : results) {
                    JSONObject categoryObj = (JSONObject) categoryElement;
                    Category category = getCategory((Long) categoryObj.get("id"));
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest(String.format("/categories/%s", categoryID), "GET", ""));
            long id = (Long) response.get("id");
            String name = (String) response.get("name");
            String description = (String) response.get("gui_description");
            String icon = (String) response.get("gui_icon");
            long order = (Long) response.get("order");

            List<Category> subCategories = new ArrayList<>();
            for (Object subCategoryElement : (JSONArray) response.get("subcategories")) {
                JSONObject subCategoryObj = (JSONObject) subCategoryElement;
                Category subCategory = getCategory((Long) subCategoryObj.get("id"));
                if (subCategory != null) {
                    subCategories.add(subCategory);
                }
            }

            List<Item> items = new ArrayList<>();
            for (Object itemElement : (JSONArray) response.get("items")) {
                JSONObject itemObj = (JSONObject) itemElement;
                Item item = getItem((Long) itemObj.get("id"));
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest("/items", "GET", "limit=25"));
            long count = (Long) response.get("count");
            long pages = (count / 25) + 1;

            for (int i = 1; i <= pages; i++) {
                if (i > 1) response = (JSONObject) PARSER.parse(makeRequest("/items", "GET", "limit=25&offset=" + (25 * (i - 1))));

                JSONArray results = (JSONArray) response.get("results");
                for (Object itemElement : results) {
                    JSONObject itemObj = (JSONObject) itemElement;
                    Item item = getItem((Long) itemObj.get("id"));
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest(String.format("/items/%s", itemID), "GET", ""));
            long id = (Long) response.get("id");
            String name = (String) response.get("name");
            String description = (String) response.get("gui_description");
            String icon = (String) response.get("gui_icon");
            String url = (String) response.get("gui_url");
            String price = (String) response.get("price");
            long order = (Long) response.get("order");
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest("/transactions", "GET", "limit=25&" + query));
            long count = (Long) response.get("count");
            long pages = (count / 25) + 1;

            for (int i = 1; i <= pages; i++) {
                if (i > 1) response = (JSONObject) PARSER.parse(makeRequest("/transactions", "GET", "limit=25&offset=" + (25 * (i - 1)) + query));

                JSONArray results = (JSONArray) response.get("results");
                for (Object transactionElement : results) {
                    JSONObject transactionObj = (JSONObject) transactionElement;
                    Transaction transaction = getTransaction((Long) transactionObj.get("id"));
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest(String.format("/transactions/%s", transactionID), "GET", ""));
            long id = (Long) response.get("id");
            String status = (String) response.get("status");
            String gateway = (String) response.get("gateway");
            String transaction_id = (String) response.get("transaction_id");
            String price = (String) response.get("price");
            String date = (String) response.get("date");

            JSONObject currencyObj = (JSONObject) response.get("currency");
            long currencyID = (Long) currencyObj.get("id");
            String currencyCode = (String) currencyObj.get("code");
            Currency currency = new Currency(currencyID, currencyCode);

            JSONObject playerObj = (JSONObject) response.get("player");
            long playerID = (Long) playerObj.get("id");
            String playerName = (String) playerObj.get("username");
            String playerUUID = (String) playerObj.get("uuid");
            boolean playerVerified = (Boolean) playerObj.get("verified");
            String playerSkinUrl = (String) playerObj.get("skin_url");
            String playerSkinSource = (String) playerObj.get("skin_source");
            String playerCapeUrl = (String) playerObj.get("cape_url");
            String playerCapeSource = (String) playerObj.get("cape_source");
            MMPlayer player = new MMPlayer(playerID, playerName, playerUUID, playerVerified, playerSkinUrl, playerSkinSource, playerCapeUrl, playerCapeSource);

            List<Purchase> purchases = new ArrayList<>();
            JSONArray purchasesArray = (JSONArray) response.get("purchases");
            for (Object purchaseElement : purchasesArray) {
                JSONObject purchaseObj = (JSONObject) purchaseElement;
                Purchase purchase = getPurchase((Long) purchaseObj.get("id"));
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest("/purchases", "GET", "limit=25"));
            long count = (Long) response.get("count");
            long pages = (count / 25) + 1;

            for (int i = 1; i <= pages; i++) {
                if (i > 1) response = (JSONObject) PARSER.parse(makeRequest("/purchases", "GET", "limit=25&offset=" + (25 * (i - 1))));

                JSONArray results = (JSONArray) response.get("results");
                for (Object purchaseElement : results) {
                    JSONObject purchaseObj = (JSONObject) purchaseElement;
                    Purchase purchase = getPurchase((Long) purchaseObj.get("id"));
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest(String.format("/purchases/%s", purchaseID), "GET", ""));
            long id = (Long) response.get("id");
            String name = (String) response.get("name");
            String price = (String) response.get("price");
            String date = (String) response.get("date");

            JSONObject currencyObj = (JSONObject) response.get("currency");
            long currencyID = (Long) currencyObj.get("id");
            String currencyCode = (String) currencyObj.get("code");
            Currency currency = new Currency(currencyID, currencyCode);

            JSONObject playerObj = (JSONObject) response.get("player");
            long playerID = (Long) playerObj.get("id");
            String playerName = (String) playerObj.get("username");
            String playerUUID = (String) playerObj.get("uuid");
            boolean playerVerified = (Boolean) playerObj.get("verified");
            String playerSkinUrl = (String) playerObj.get("skin_url");
            String playerSkinSource = (String) playerObj.get("skin_source");
            String playerCapeUrl = (String) playerObj.get("cape_url");
            String playerCapeSource = (String) playerObj.get("cape_source");
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest("/commands", "GET", "limit=25&" + query));
            long count = (Long) response.get("count");
            long pages = (count / 25) + 1;

            for (int i = 1; i <= pages; i++) {
                if (i > 1) response = (JSONObject) PARSER.parse(makeRequest("/commands", "GET", "limit=25&offset=" + (25 * (i - 1)) + query));

                JSONArray results = (JSONArray) response.get("results");
                for (Object commandElement : results) {
                    JSONObject commandObj = (JSONObject) commandElement;
                    Command command = getCommand((Long) commandObj.get("id"));
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
            JSONObject response = (JSONObject) PARSER.parse(makeRequest(String.format("/commands/%s", commandID), "GET", ""));
            long id = (Long) response.get("id");
            String type = (String) response.get("type");
            String command = (String) response.get("command");
            long delay = (Long) response.get("delay");
            long requiredSlots = (Long) response.get("required_slots");
            boolean requiredOnline = (Long) response.get("required_online") > 0;
            boolean repeat = (Boolean) response.get("repeat");
            long repeatPeriod = (Long) response.get("repeat_period");
            long repeatCycles = (Long) response.get("repeat_cycles");
            boolean executed = (Boolean) response.get("executed");

            JSONObject playerObj = (JSONObject) response.get("player");
            long playerID = (Long) playerObj.get("id");
            String playerName = (String) playerObj.get("username");
            String playerUUID = (String) playerObj.get("uuid");
            boolean playerVerified = (Boolean) playerObj.get("verified");
            String playerSkinUrl = (String) playerObj.get("skin_url");
            String playerSkinSource = (String) playerObj.get("skin_source");
            String playerCapeUrl = (String) playerObj.get("cape_url");
            String playerCapeSource = (String) playerObj.get("cape_source");
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