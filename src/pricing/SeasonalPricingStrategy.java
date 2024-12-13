package pricing;

public class SeasonalPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice * 1.2; // 20% increase
    }

    @Override
    public String toString() {
        return "Seasonal: 20% increase";
    }
}