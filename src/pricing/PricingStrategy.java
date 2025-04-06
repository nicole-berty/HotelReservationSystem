package pricing;

import java.text.NumberFormat;
import java.util.Locale;

// 4 Sealed Classes and Interfaces
sealed public interface PricingStrategy permits CorporatePricingStrategy, PromotionalDiscountStrategy, RegularPricingStrategy, SeasonalPricingStrategy {

    // default method used if calculatePrice not implemented in class that implements the interface
    default double calculatePrice(double basePrice) {
        return basePrice;
    }

    // default method use to calculate additional costs such as taxes + service fees. Takes varargs additionalCosts as
    // the number of additional costs is not known and may vary
    default double calculateAdditionalCosts(int... additionalCosts) {
        double additionalCost = 0;

        for (int cost : additionalCosts) {
            additionalCost += cost;
        }

        return additionalCost;
    }


    // private method ensures currency is displayed in correct Locale format
    private String formatCurrency(double price) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        return currencyFormatter.format(price);
    }

    // default method which displays the formatted price using private formatCurrency method
    default String displayFormattedPrice(double baseRate) {
        double price = calculatePrice(baseRate);
        return formatCurrency(price);
    }

    // static method for creating PricingStrategy from a String
    static PricingStrategy fromString(String strategy) {
        return switch (strategy.toLowerCase().split(":")[0]) {
            case "regular" -> new RegularPricingStrategy();
            case "seasonal" -> new SeasonalPricingStrategy();
            case "promotional" -> new PromotionalDiscountStrategy();
            case "corporate" -> new CorporatePricingStrategy();
            default -> throw new IllegalArgumentException(STR."Unknown pricing strategy: \{strategy}");
        };
    }
}
