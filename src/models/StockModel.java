package models;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StockModel {
    private String symbol;
    private String companyName;
    private double currentPrice;
    private double previousClose;
    private double percentChange;
    private long totalVolume;
    private final Map<Calendar, Double> historicalPrices = new HashMap<>();

    private static final String API_KEY = System.getenv("BTQFX8V3210IHD35");

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean fetchData() {
        try {
            fetchCurrentPrice();
            fetchHistoricalData();
            return true;
        } catch (Exception e) {
            System.out.println("An exception occurred while fetching data:");
            e.printStackTrace();
            return false;
        }
    }

    private void fetchCurrentPrice() throws IOException, InterruptedException {
        String apiUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
                + symbol + "&apikey=" + API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);

        JsonNode quoteNode = rootNode.path("Global Quote");

        if (quoteNode.isMissingNode() || quoteNode.isEmpty()) {
            System.out.println("Failed to fetch current price data.");
            throw new IOException("Current price data missing");
        }

        companyName = symbol; // Alpha Vantage does not provide company name in this endpoint
        currentPrice = Double.parseDouble(quoteNode.get("05. price").asText());
        previousClose = Double.parseDouble(quoteNode.get("08. previous close").asText());
        percentChange = Double.parseDouble(quoteNode.get("10. change percent").asText().replace("%", ""));
        totalVolume = Long.parseLong(quoteNode.get("06. volume").asText());
    }

    private void fetchHistoricalData() throws IOException, InterruptedException {
        String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="
                + symbol + "&apikey=" + API_KEY;

        System.out.println("Fetching historical data from: " + apiUrl);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("HTTP Status Code: " + response.statusCode());

        String responseBody = response.body();
        System.out.println("API Response Body: " + responseBody);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);

        // Check for API errors
        if (rootNode.has("Error Message") || rootNode.has("Note") || rootNode.has("Information")) {
            System.out.println("API Error: " + rootNode);
            throw new IOException("API returned an error");
        }

        JsonNode timeSeriesNode = rootNode.path("Time Series (Daily)");

        if (timeSeriesNode.isMissingNode() || timeSeriesNode.isNull() || timeSeriesNode.isEmpty()) {
            System.out.println("Failed to fetch historical data.");
            System.out.println("API Response: " + responseBody);
            throw new IOException("Historical data missing");
        }

        Calendar fromDate = Calendar.getInstance();
        fromDate.add(Calendar.YEAR, -3);

        for (Iterator<String> it = timeSeriesNode.fieldNames(); it.hasNext(); ) {
            String dateStr = it.next();
            Calendar date = Calendar.getInstance();
            String[] dateParts = dateStr.split("-");
            date.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
            date.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
            date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);

            if (date.before(fromDate)) {
                continue;
            }

            JsonNode dateNode = timeSeriesNode.get(dateStr);
            if (dateNode == null) {
                System.out.println("No data for date: " + dateStr);
                continue;
            }

            JsonNode closePriceNode = dateNode.get("4. close");
            if (closePriceNode == null || closePriceNode.isNull()) {
                System.out.println("Close price not found for date: " + dateStr);
                continue;
            }

            double closePrice = closePriceNode.asDouble();
            historicalPrices.put((Calendar) date.clone(), closePrice);
        }
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public long getTotalVolume() {
        return totalVolume;
    }

    public Map<Calendar, Double> getHistoricalPrices() {
        return historicalPrices;
    }
}