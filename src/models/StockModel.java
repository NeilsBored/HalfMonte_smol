import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StockModel {
    private Stock stock;
    private String symbol;
    private Map<Calendar, Double> historicalPrices = new HashMap<>();

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean fetchData() {
        try {
            stock = YahooFinance.get(symbol, true);
            if (stock == null || stock.getName() == null) {
                return false;
            }
            // Fetch historical data for the last 3 years
            Calendar from = Calendar.getInstance();
            from.add(Calendar.YEAR, -3);
            Calendar to = Calendar.getInstance();
            stock.getHistory(from, to).forEach(histQuote -> {
                historicalPrices.put(histQuote.getDate(), histQuote.getClose().doubleValue());
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getCompanyName() {
        return stock.getName();
    }

    public String getSymbol() {
        return stock.getSymbol();
    }

    public double getCurrentPrice() {
        return stock.getQuote().getPrice().doubleValue();
    }

    public double getPreviousClose() {
        return stock.getQuote().getPreviousClose().doubleValue();
    }

    public double getPercentChange() {
        return stock.getQuote().getChangeInPercent().doubleValue();
    }

    public long getTotalVolume() {
        return stock.getQuote().getVolume();
    }

    public Map<Calendar, Double> getHistoricalPrices() {
        return historicalPrices;
    }
}