package pricing;

// 4 - Sealed Classes and Interfaces - subclass is final
public final class SeasonalPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice * 1.2; // 20% increase
    }

    @Override
    public String toString() {
        return "Seasonal: 20% increase";
    }
}