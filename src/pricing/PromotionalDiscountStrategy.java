package pricing;

// 4 Sealed Classes and Interfaces - subclass is final
public final class PromotionalDiscountStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice * 0.87; // 13% discount
    }

    @Override
    public String toString() {
        return "Promotional: 13% discount";
    }
}