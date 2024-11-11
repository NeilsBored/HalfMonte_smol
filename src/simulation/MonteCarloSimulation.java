package simulation;

import java.util.*;
import java.util.stream.Collectors;

import views.StockView;

public class MonteCarloSimulation {
    private final Map<Calendar, Double> historicalPrices;
    private final StockView view;
    private double calculatedClosePrice;
    private double calculatedHighPrice;
    private double calculatedLowPrice;
    private double estimatedError;
    private String[] monthlyPercentageChanges;
    private double yearlyPercentageChange;
    private double modelAccuracy;
    private double bestGuess;
    private double errorMargin;

    public MonteCarloSimulation(Map<Calendar, Double> historicalPrices, StockView view) {
        this.historicalPrices = historicalPrices;
        this.view = view;
    }

    public void runSimulation() {
        int numSimulations = 1000;
        int timeHorizon = 252; // Number of trading days in a year
        List<Double> finalPrices = new ArrayList<>();

        double currentPrice = historicalPrices.values().stream().max(Comparator.naturalOrder())
                .orElse(100.0);

        // Calculate monthly statistics
        Map<Integer, List<Double>> monthlyReturns = calculateMonthlyReturns();

        for (int i = 0; i < numSimulations; i++) {
            view.showProgressBar((i * 100) / numSimulations);

            double price = performSimulation(currentPrice, timeHorizon, monthlyReturns);
            finalPrices.add(price);
        }

        view.showProgressBar(100);

        calculatedClosePrice = finalPrices.stream().mapToDouble(Double::doubleValue).average().orElse(currentPrice);
        calculatedHighPrice = finalPrices.stream().mapToDouble(Double::doubleValue).max().orElse(currentPrice);
        calculatedLowPrice = finalPrices.stream().mapToDouble(Double::doubleValue).min().orElse(currentPrice);
        estimatedError = Math.abs((calculatedClosePrice - currentPrice) / currentPrice) * 100;

        // Placeholder values for demonstration
        monthlyPercentageChanges = new String[]{"January: +2%", "February: -1%", "March: +3%", "April: +1%", "May: +2%", "June: -2%", "July: +4%", "August: -1%", "September: +2%", "October: +3%", "November: -1%", "December: +2%"};
        yearlyPercentageChange = ((calculatedClosePrice - currentPrice) / currentPrice) * 100;
        modelAccuracy = 90.0; // Placeholder for actual accuracy calculation
        bestGuess = calculatedClosePrice;
        errorMargin = estimatedError;
    }

    private Map<Integer, List<Double>> calculateMonthlyReturns() {
        Map<Integer, List<Double>> monthlyReturns = new HashMap<>();

        List<Map.Entry<Calendar, Double>> sortedPrices = historicalPrices.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (int i = 1; i < sortedPrices.size(); i++) {
            Calendar date = sortedPrices.get(i).getKey();
            double priceToday = sortedPrices.get(i).getValue();
            double priceYesterday = sortedPrices.get(i - 1).getValue();
            double dailyReturn = Math.log(priceToday / priceYesterday);

            int month = date.get(Calendar.MONTH);
            monthlyReturns.computeIfAbsent(month, k -> new ArrayList<>()).add(dailyReturn);
        }

        // Duplicate each month's data to account for periodic changes
        for (int month = 0; month < 12; month++) {
            List<Double> returns = monthlyReturns.getOrDefault(month, new ArrayList<>());
            returns.addAll(new ArrayList<>(returns));
            monthlyReturns.put(month, returns);
        }

        return monthlyReturns;
    }

    private double performSimulation(double startPrice, int days, Map<Integer, List<Double>> monthlyReturns) {
        double price = startPrice;
        Random rand = new Random();
        Calendar calendar = Calendar.getInstance();

        for (int day = 0; day < days; day++) {
            int month = calendar.get(Calendar.MONTH);
            List<Double> returns = monthlyReturns.get(month);

            double mean = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double variance = returns.stream().mapToDouble(r -> Math.pow(r - mean, 2)).average().orElse(0);
            double drift = mean - (0.5 * variance);
            double stdDev = Math.sqrt(variance);
            double shock = stdDev * rand.nextGaussian();

            price = price * Math.exp(drift + shock);

            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return price;
    }

    public double getCalculatedClosePrice() {
        return calculatedClosePrice;
    }

    public double getCalculatedHighPrice() {
        return calculatedHighPrice;
    }

    public double getCalculatedLowPrice() {
        return calculatedLowPrice;
    }

    public double getEstimatedError(double currentPrice) {
        return estimatedError;
    }

    public String[] getMonthlyPercentageChanges() {
        return monthlyPercentageChanges;
    }

    public double getYearlyPercentageChange() {
        return yearlyPercentageChange;
    }

    public double getModelAccuracy() {
        return modelAccuracy;
    }

    public double getBestGuess() {
        return bestGuess;
    }

    public double getErrorMargin() {
        return errorMargin;
    }
}