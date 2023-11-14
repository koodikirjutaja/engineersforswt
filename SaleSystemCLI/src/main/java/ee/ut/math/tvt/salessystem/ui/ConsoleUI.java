package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.FieldFormatException;
import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A simple CLI (limited functionality).
 */
public class ConsoleUI {
    private static final Logger log = LogManager.getLogger(ConsoleUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart cart;

    public ConsoleUI(SalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
    }

    public static void main(String[] args) throws Exception {
        log.info("CLI Starting");
        SalesSystemDAO dao = new InMemorySalesSystemDAO();
        log.info("CLI DAO created");
        ConsoleUI console = new ConsoleUI(dao);
        log.info("CLI console object created");
        console.run();
        log.info("CLI Started");
    }

    /**
     * Run the sales system CLI.
     */
    public void run() throws IOException {
        log.debug("CLI-run");
        System.out.println("===========================");
        System.out.println("=       Sales System      =");
        System.out.println("===========================");
        printUsage();
        log.debug("CLI-run-printUsage");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        log.debug("CLI-run: reading Input");
        while (true) {
            System.out.print("> ");
            processCommand(in.readLine().trim().toLowerCase());
            log.debug("CLI-processCommand");
            System.out.println("Done. ");
        }
    }

    private void showStock() {
        log.debug("CLI-showStock");
        List<StockItem> stockItems = dao.findStockItems();
        log.debug("CLI-showStock-findStockItems");
        System.out.println("-------------------------");
        for (StockItem si : stockItems) {
            String strItem = si.getId() + " " + si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)";
            System.out.println(strItem);
            log.debug("CLI-showStock: " + strItem);
        }
        if (stockItems.size() == 0) {
            System.out.println("\tNothing");
            log.debug("CLI-showStock: No items");
        }
        System.out.println("-------------------------");
    }
    private void addItem() {
        log.debug("CLI-addItem");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        log.debug("CLI-addItem: reading Input");
        try{
            System.out.println("Item name:");
            String name = input.readLine();
            System.out.println("Item quantity:");
            String quantity = input.readLine();
            System.out.println("Item price:");
            String price = input.readLine();
            System.out.println("Description:");
            String description = input.readLine();
            dao.addStockItem(name, description, price, quantity);
            log.debug("ClI-addItem-addStockItem: " +
                    name + ", " +
                    description + ", " +
                    price + ", " +
                    quantity);
        }
        catch (IOException ex){
            ex.printStackTrace();
            log.error("CLI-addItem-IOException: " + ex.getMessage(), ex);
        }
        catch (FieldFormatException e) {
            log.error("CLI-addItem-FieldFormatException: " + e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("CLI-addItem-Exception: " + e.getMessage(), e);
        }
    }
    private void showCart() {
        log.debug("CLI-showCart");
        System.out.println("-------------------------");
        for (SoldItem si : cart.getAll()) {
            String cartItemStr = si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)";
            System.out.println(cartItemStr);
            log.debug("CLI-showCart: "  + cartItemStr);
        }
        if (cart.getAll().size() == 0) {
            System.out.println("\tNothing");
            log.debug("CLI-showCart: No items");
        }
        System.out.println("-------------------------");
    }

    private void showTeamInfo() {
        log.debug("CLI-showTeamInfo");
        Properties prop = new Properties();
        log.debug("CLI-showTeamInfo: Created Properties");
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                log.debug("CLI-showTeamInfo: Sorry, unable to find application.properties");
                return;
            }
            log.debug("CLI-showTeamInfo: Found application.properties");
            prop.load(input);
            log.debug("CLI-showTeamInfo: Load properties");
            System.out.println("-------------------------");
            System.out.println("Team Name: " + prop.getProperty("team.name"));
            System.out.println("Team Contact: " + prop.getProperty("team.contact"));
            System.out.println("Team Members: " + prop.getProperty("team.members"));
            System.out.println("-------------------------");
            log.debug("CLI-showTeamInfo: Loaded properties");
        } catch (IOException ex) {
            ex.printStackTrace();
            log.debug("CLI-showTeamInfo-IOException: " + ex.getMessage(), ex);
        }
    }

    private void showHistory(String logMessage, String introMessage, String exceptionMessage, List<Purchase> purchases){
        log.debug(logMessage);
        System.out.println("-------------------------");
        System.out.println(introMessage);
        for (Purchase purchase : purchases){
            System.out.println("ID: " + purchase.getId() + ", Date: "+ purchase.getPurchaseTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("Items:");
            for (SoldItem item : purchase.getItems()) {
                System.out.println("\tName: " + item.getName() + ", Quantity: " + item.getQuantity() + ", Price: " + item.getPrice());
            }
            System.out.println("Total Sum: " + purchase.calculateTotalSum());
            System.out.println("-------------------------");
        }
        if (purchases.isEmpty()) {
            System.out.println(exceptionMessage);
            System.out.println("-------------------------");
        }
    }

    private List<Purchase> fetchLastTenPurchases() {
        log.debug("CLI-showLastTenPurchases");
        List<Purchase> lastTen = dao.findPurchase().stream().sorted((p1,p2) ->
                p2.getPurchaseTime().compareTo(p1.getPurchaseTime())).limit(10).
                collect(Collectors.toList());
        return lastTen;
    }

    private List<Purchase> fetchPurchasesBetweenDates() {
        List <Purchase> endResult = new ArrayList<>();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Enter start date (yyyy-MM-dd):");
            String startDateStr = input.readLine();
            System.out.println("Enter end date (yyyy-MM-dd):");
            String endDateStr = input.readLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(startDateStr + " 00:00:00", formatter);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr + " 23:59:59", formatter);

            List<Purchase> filteredPurchases = dao.findPurchase().stream().
                    filter(purchase -> !purchase.getPurchaseTime().isBefore(startDate) && !purchase.getPurchaseTime().isAfter(endDate)).
                    collect(Collectors.toList());
            endResult = filteredPurchases;
            return endResult;

        } catch (IOException e) {
            System.out.println("Error reading input: " + e.getMessage());
        } catch (DateTimeParseException e){
            System.out.println("Invalid date format:" + e.getMessage());
        }
        return endResult;
    }

    private void printUsage() {
        log.debug("CLI-printUsage");
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("h\t\tShow this help");
        System.out.println("w\t\tShow warehouse contents");
        System.out.println("i\t\tShow purchase history");
        System.out.println("l\t\tShow last 10 purchases");
        System.out.println("d\t\tShow between dates");
        System.out.println("t\t\tShow team info");
        System.out.println("s\t\tAdd item new item to warehouse");
        System.out.println("c\t\tShow cart contents");
        System.out.println("a IDX NR \tAdd NR of stock item with index IDX to the cart");
        System.out.println("p\t\tPurchase the shopping cart");
        System.out.println("r\t\tReset the shopping cart");
        System.out.println("-------------------------");
    }

    private void processCommand(String command) {
        log.debug("CLI-processCommand");
        String[] c = command.split(" ");
        log.debug("CLI-processCommand: command split");
        if (c[0].equals("h"))
            printUsage();
        else if (c[0].equals("q"))
            System.exit(0);
        else if (c[0].equals("w"))
            showStock();
        else if (c[0].equals("c"))
            showCart();
        else if (c[0].equals("p"))
            cart.submitCurrentPurchase();
        else if (c[0].equals("r"))
            cart.cancelCurrentPurchase();
        else if (c[0].equals("t"))
            showTeamInfo();
        else if (c[0].equals("s"))
            addItem();
        else if (c[0].equals("i")){
            List<Purchase> purchases = dao.findPurchase();
            showHistory("CLI-showHistory","Purchase history:","\tNo purchase history available.",purchases);
        } else if (c[0].equals("l"))
            showHistory("CLI-showLastTenPurchases","Last 10 Purchases:","\tNo recent purchases available",fetchLastTenPurchases());
        else if (c[0].equals("d"))
            showHistory("CLI-showBetweenDates", "Purchases between these dates:", "No purchases found in the specified date range.",fetchPurchasesBetweenDates());
        else if (c[0].equals("a") && c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                log.debug("CLI-processCommand: ID");
                int amount = Integer.parseInt(c[2]);
                log.debug("CLI-processCommand: Amount");
                StockItem item = dao.findStockItem(idx);
                log.debug("CLI-processCommand-findStockItem");
                if (item != null) {
                    log.debug("CLI-processCommand: Item found");
                    cart.addItem(new SoldItem(item, Math.min(amount, item.getQuantity())));
                    log.debug("CLI-processCommand: Item sold");
                } else {
                    System.out.println("no stock item with id " + idx);
                    log.debug("CLI-processCommand: Item not found with id " + idx);
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            System.out.println("unknown command");
        }
    }



}
