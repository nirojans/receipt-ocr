import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by niro273 on 1/6/17.
 */
public class StringHelper {

    public static void getLineItemsViaRegex(String[] array) {
        String poundSymbol = "£";
        String[] inputStrings = {
                "CHOC. ORANGE    x         " + poundSymbol + "1.00"
                , "CHOC.    ORANGE    x         L1.00",
                "NUMBER   I W'xwxmwxxx‘xxx7089  ICC",
                "CHOC. Niro    x         $1.00",
                "CHOC.    ORANGE    x         L1.0"
        };

        String regex = "(?<description>.+)"
                + "\\s{2,}"                             // two or more white space
                + "(?<currency>"+poundSymbol+"|\\w)"    // Pound symbol may be mis-reaad
                + "(?<amount>\\d+\\.\\d{2})";
        Pattern p = Pattern.compile(regex);
        for (String inputString : inputStrings) {
            Matcher m = p.matcher(inputString);
            if (m.find()) {
                String description  = m.group("description");
                String currency     = m.group("currency");
                String amountString = m.group("amount");

                System.out.format("Desciption: %s%n" + "Currency: %s%n" + "Amount: %s%n", description.trim() , currency
                        , amountString);
            }
        }
    }

    public static final int distance(final String s1, final String s2) {
        if (s1.equals(s2)) {
            return 0;
        }

        if (s1.length() == 0) {
            return s2.length();
        }

        if (s2.length() == 0) {
            return s1.length();
        }

        // create two work vectors of integer distances
        int[] v0 = new int[s2.length() + 1];
        int[] v1 = new int[s2.length() + 1];
        int[] vtemp;

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }

        for (int i = 0; i < s1.length(); i++) {
            // calculate v1 (current row distances) from the previous row v0
            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < s2.length(); j++) {
                int cost = 1;
                if (s1.charAt(i) == s2.charAt(j)) {
                    cost = 0;
                }
                v1[j + 1] = Math.min(
                        v1[j] + 1,              // Cost of insertion
                        Math.min(
                                v0[j + 1] + 1,  // Cost of remove
                                v0[j] + cost)); // Cost of substitution
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            //System.arraycopy(v1, 0, v0, 0, v0.length);

            // Flip references to current and previous row
            vtemp = v0;
            v0 = v1;
            v1 = vtemp;

        }

        return v0[s2.length()];
    }
}
