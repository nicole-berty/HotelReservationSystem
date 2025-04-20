package pricing;

// 4 - Sealed Classes and Interfaces - subclass is final
public final class CorporatePricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice * 0.8; // 20% discount
    }

    @Override
    public String toString() {
        return "Corporate: 20% discount";
    }
}