public class Main {
    public static void main(String[] args) {
        StockModel model = new StockModel();
        StockView view = new StockView();
        StockController controller = new StockController(model, view);
        controller.start();
    }
}