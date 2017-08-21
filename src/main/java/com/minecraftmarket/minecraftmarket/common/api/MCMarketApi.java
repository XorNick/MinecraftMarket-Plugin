package com.minecraftmarket.minecraftmarket.common.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class MCMarketApi {
    private final String BASE_URL = "https://www.minecraftmarket.com/api/v1/plugin/";
    private final String API_KEY;
    private final String USER_AGENT;
    protected final boolean DEBUG;

    public MCMarketApi(String apiKey, String userAgent, boolean debug) {
        API_KEY = apiKey;
        USER_AGENT = userAgent;
        DEBUG = debug;
    }

    public String getBaseUrl() {
        return BASE_URL;
    }

    public String getApiKey() {
        return API_KEY;
    }

    public String getUserAgent() {
        return USER_AGENT;
    }

    public abstract boolean authAPI();

    public abstract Market getMarket();

    public abstract List<Category> getCategories();

    public abstract Category getCategory(long categoryID);

    public abstract List<Item> getItems();

    public abstract Item getItem(long itemID);

    public abstract List<Transaction> getTransactions(Filter filter);

    public abstract Transaction getTransaction(long transactionID);

    public abstract List<Purchase> getPurchases();

    public abstract Purchase getPurchase(long purchaseID);

    public abstract List<Command> getCommands(Filter... filters);

    public abstract Command getCommand(long commandID);

    public abstract void setExecuted(long commandID);

    protected BufferedReader makeRequest(String url, String method, String query) throws IOException {
        HttpURLConnection conn;
        if (method.equals("PUT")) {
            conn = (HttpURLConnection) new URL(BASE_URL + API_KEY + url + "/?format=json").openConnection();
            conn.setRequestMethod("PUT");
        } else {
            conn = (HttpURLConnection) new URL(BASE_URL + API_KEY + url + "/?format=json&" + query).openConnection();
            conn.setRequestMethod("GET");
        }
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        if (USER_AGENT != null) conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setUseCaches(false);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(10000);
        conn.setDoInput(true);
        if (conn.getRequestMethod().equals("PUT")) {
            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(query);
            out.close();
        }
        return new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    protected String buildQueryFromFilters(Filter... filters) {
        StringBuilder builder = new StringBuilder();
        for (Filter filter : filters) {
            if (builder.length() != 0) builder.append("&");
            builder.append(filter.getName())
                    .append("=")
                    .append(filter.getValue());
        }
        return builder.toString();
    }

    public class Market {
        private final long id;
        private final String name;
        private final Currency currency;
        private final String url;

        public Market(long id, String name, Currency currency, String url) {
            this.id = id;
            this.name = name;
            this.currency = currency;
            this.url = url;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Currency getCurrency() {
            return currency;
        }

        public String getUrl() {
            return url;
        }
    }

    public class Category {
        private final long id;
        private final String name;
        private final String description;
        private final String icon;
        private final List<Category> subCategories;
        private final List<Item> items;
        private final long order;

        public Category(long id, String name, String description, String icon, List<Category> subCategories, List<Item> items, long order) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.subCategories = subCategories;
            this.items = items;
            this.order = order;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }

        public List<Category> getSubCategories() {
            return subCategories;
        }

        public List<Item> getItems() {
            return items;
        }

        public long getOrder() {
            return order;
        }
    }

    public class Item {
        private final long id;
        private final String name;
        private final String description;
        private final String icon;
        private final String url;
        private final String price;
        private final long order;

        public Item(long id, String name, String description, String icon, String url, String price, long order) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.url = url;
            this.price = price;
            this.order = order;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }

        public String getUrl() {
            return url;
        }

        public String getPrice() {
            return price;
        }

        public long getOrder() {
            return order;
        }
    }

    public class Transaction {
        private final long id;
        private final String status;
        private final String gateway;
        private final String transactionID;
        private final String price;
        private final Currency currency;
        private final String date;
        private final MMPlayer player;
        private final List<Purchase> purchases;

        public Transaction(long id, String status, String gateway, String transactionID, String price, Currency currency, String date, MMPlayer player, List<Purchase> purchases) {
            this.id = id;
            this.status = status;
            this.gateway = gateway;
            this.transactionID = transactionID;
            this.price = price;
            this.currency = currency;
            this.date = date;
            this.player = player;
            this.purchases = purchases;
        }

        public long getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        public String getGateway() {
            return gateway;
        }

        public String getTransactionID() {
            return transactionID;
        }

        public String getPrice() {
            return price;
        }

        public Currency getCurrency() {
            return currency;
        }

        public String getDate() {
            return date;
        }

        public MMPlayer getPlayer() {
            return player;
        }

        public List<Purchase> getPurchases() {
            return purchases;
        }
    }

    public class Purchase {
        private final long id;
        private final String name;
        private final String price;
        private final Currency currency;
        private final String date;
        private final MMPlayer player;

        public Purchase(long id, String name, String price, Currency currency, String date, MMPlayer player) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.currency = currency;
            this.date = date;
            this.player = player;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public Currency getCurrency() {
            return currency;
        }

        public String getDate() {
            return date;
        }

        public MMPlayer getPlayer() {
            return player;
        }
    }

    public class Command {
        private final long id;
        private final MMPlayer player;
        private final String type;
        private final String command;
        private final long delay;
        private final long requiredSlots;
        private final boolean requiredOnline;
        private final boolean repeat;
        private final long repeatPeriod;
        private final long repeatCycles;
        private final boolean executed;

        public Command(long id, MMPlayer player, String type, String command, long delay, long requiredSlots, boolean requiredOnline, boolean repeat, long repeatPeriod, long repeatCycles, boolean executed) {
            this.id = id;
            this.player = player;
            this.type = type;
            this.command = command;
            this.delay = delay;
            this.requiredSlots = requiredSlots;
            this.requiredOnline = requiredOnline;
            this.repeat = repeat;
            this.repeatPeriod = repeatPeriod;
            this.repeatCycles = repeatCycles;
            this.executed = executed;
        }

        public long getId() {
            return id;
        }

        public MMPlayer getPlayer() {
            return player;
        }

        public String getType() {
            return type;
        }

        public String getCommand() {
            return command;
        }

        public long getDelay() {
            return delay;
        }

        public long getRequiredSlots() {
            return requiredSlots;
        }

        public boolean isRequiredOnline() {
            return requiredOnline;
        }

        public boolean isRepeat() {
            return repeat;
        }

        public long getRepeatPeriod() {
            return repeatPeriod;
        }

        public long getRepeatCycles() {
            return repeatCycles;
        }

        public boolean isExecuted() {
            return executed;
        }
    }

    public class MMPlayer {
        private final long id;
        private final String name;
        private final String uuid;
        private final boolean verified;
        private final String skinUrl;
        private final String skinSource;
        private final String capeUrl;
        private final String capeSource;

        public MMPlayer(long id, String name, String uuid, boolean verified, String skinUrl, String skinSource, String capeUrl, String capeSource) {
            this.id = id;
            this.name = name;
            this.uuid = uuid;
            this.verified = verified;
            this.skinUrl = skinUrl;
            this.skinSource = skinSource;
            this.capeUrl = capeUrl;
            this.capeSource = capeSource;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUuid() {
            return uuid;
        }

        public boolean isVerified() {
            return verified;
        }

        public String getSkinUrl() {
            return skinUrl;
        }

        public String getSkinSource() {
            return skinSource;
        }

        public String getCapeUrl() {
            return capeUrl;
        }

        public String getCapeSource() {
            return capeSource;
        }
    }

    public class Currency {
        private final long id;
        private final String code;

        public Currency(long id, String code) {
            this.id = id;
            this.code = code;
        }

        public long getId() {
            return id;
        }

        public String getCode() {
            return code;
        }
    }

    public enum TransactionStatus implements Filter {
        COMPLETED("1"),
        PENDING("2"),
        CHARGEBACK("3"),
        REFUNDED("4"),
        ERROR("5");

        private final String value;

        TransactionStatus(String value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return "status";
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public enum CommandType implements Filter {
        INITIAL("initial"),
        EXPIRY("expiry"),
        CHARGEBACK("chargeback"),
        REFUND("refund"),
        RENEWAL("renewal");

        private final String value;

        CommandType(String value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return "type";
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public enum CommandStatus implements Filter {
        EXECUTED("true"),
        NOT_EXECUTED("false");

        private final String value;

        CommandStatus(String value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return "executed";
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public interface Filter {
        String getName();
        String getValue();
    }
}