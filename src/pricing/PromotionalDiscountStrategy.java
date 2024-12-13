package pricing;

public class PromotionalDiscountStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice * 0.87; // 13% discount
    }

    @Override
    public String toString() {
        return "Promotional: 13% discount";
    }
}