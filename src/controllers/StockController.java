public class StockController {
    private StockModel model;
    private StockView view;

    public StockController(StockModel model, StockView view)
    {
        this.model = model;
        this.view = view;
    }

    public void start()
    {
        String symbol = view.getInputSymbol();
        model.setSymbol(symbol);

        if (!model.fetchData()) {
            System.out.println("Failed to fetch data for symbol: " + symbol);
            return;
        }

        view.displayStockInfo(
                model.getCompanyName(),
                model.getSymbol(),
                model.getCurrentPrice(),
                model.getPreviousClose(),
                model.getPercentChange(),
                model.getTotalVolume()
        );

        MonteCarloSimulation simulation = new MonteCarloSimulation(model.getHistoricalPrices(), view);
        simulation.runSimulation();

        view.displaySimulationResults(
                simulation.getCalculatedClosePrice(),
                simulation.getCalculatedHighPrice(),
                simulation.getCalculatedLowPrice(),
                simulation.getEstimatedError(model.getCurrentPrice())
        );

        view.displayPercentageChanges(
                simulation.getMonthlyPercentageChanges(),
                simulation.getYearlyPercentageChange(),
                simulation.getModelAccuracy(),
                simulation.getBestGuess(),
                simulation.getErrorMargin()
        );
    }
}