package views;

import java.util.Scanner;

public class StockView {
    private Scanner scanner = new Scanner(System.in);

    public String getInputSymbol() {
        System.out.print("Enter the stock symbol: ");
        return scanner.nextLine();
    }

    public void displayStockInfo(String name, String symbol, double currentPrice, double previousClose,
                                 double percentChange, long volume) {
        System.out.println("\nCompany Name: " + name);
        System.out.println("Symbol: " + symbol);
        System.out.println("Current Price: $" + currentPrice);
        System.out.println("Previous Close: $" + previousClose);
        System.out.println("Percent Changed: " + percentChange + "%");
        System.out.println("Total Volume: " + volume);
    }

    public void showProgressBar(int progress) {
        System.out.print("\rSimulation Progress: " + progress + "%");
    }

    public void displaySimulationResults(double calculatedClosePrice, double calculatedHighPrice,
                                         double calculatedLowPrice, double estimatedError) {
        System.out.println("\n\nCalculated Close Price: $" + calculatedClosePrice);
        System.out.println("Calculated High Price: $" + calculatedHighPrice);
        System.out.println("Calculated Low Price: $" + calculatedLowPrice);
        System.out.println("Estimated Error: " + estimatedError + "%");
    }

    public void displayPercentageChanges(String[] monthlyChanges, double yearlyChange,
                                         double modelAccuracy, double bestGuess, double errorMargin) {
        System.out.println("\nMonthly Modeled Percentage Changes:");
        for (String change : monthlyChanges) {
            System.out.println(change);
        }
        System.out.println("Current Year's Modeled Percentage Change: " + yearlyChange + "%");
        System.out.println("Model's Accuracy: " + modelAccuracy + "%");
        System.out.println("Best Guess: $" + bestGuess + " ± " + errorMargin + "%");
    }
}