package pricing;

// 4 Sealed Classes and Interfaces - subclass is final
public final class RegularPricingStrategy implements PricingStrategy {

    @Override
    public String toString() {
        return "Regular: No price change";
    }
}
