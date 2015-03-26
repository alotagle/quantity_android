package com.mobile.dev.quantity.util;

/**
 * Created by Oscar Álvarez on 23/03/15.
 */

        import android.util.Log;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;

/**
 * Created by ManuelOrtiz on 10/10/2014.
 */
public class Validations {
    public static final int VALIDATION_NONE = 0;
    public static final int VALIDATION_NAME = 1;
    public static final int VALIDATION_ADDRESS = 2;
    public static final int VALIDATION_MAIL = 3;
    public static final int VALIDATION_BIRTH_DATE = 4;
    public static final int VALIDATION_EXPIRATION_DATE = 5;
    public static final int VALIDATION_EXPIRATION_MONTH_YEAR = 6;
    public static final int VALIDATION_CODE = 7;
    public static final int VALIDATION_TYPE = 8;
    public static final int VALIDATION_NIF = 9;
    public static final int VALIDATION_NIE = 10;
    public static final int VALIDATION_PASSPORT = 11;
    public static final int VALIDATION_NATIONAL_ID_CARD = 12;
    public static final int VALIDATION_CIF = 13;
    public static final int VALIDATION_POSTAL_CODE = 14;
    public static final int VALIDATION_NUMBER = 15;
    public static final int VALIDATION_PHONE_NUMBER = 16;
    public static final int VALIDATION_CC_TYPE = 17;
    public static final int VALIDATION_CC_NUMBER = 18;
    public static final int VALIDATION_CC_PLACEHOLDER = 19;
    public static final int VALIDATION_CCV = 20;
    public static final int VALIDATION_CPF = 21;

    public static boolean validateDateTypes(int validationType, Date value) {
        boolean isValid = false;

        switch (validationType) {
            case VALIDATION_BIRTH_DATE:
                isValid = validateDate(value);
                break;
            case VALIDATION_EXPIRATION_DATE:
                isValid = validateDate(value);
                break;
        }

        return isValid;
    }


    public static boolean validate(int validationType, String value) {
        boolean isValid = false;

        switch (validationType) {
            case VALIDATION_NONE:
                isValid = true;
                break;
            case VALIDATION_NAME:
                isValid = validateTextFields(value, 2, 30);
                break;
            case VALIDATION_ADDRESS:
                //isValid = validateTextAndNumberFields(value, 2, 30);
                isValid = validateAddress(value, 2, 30);
                break;
            case VALIDATION_MAIL:
                isValid = validEmail(value);
                break;
            case VALIDATION_CODE:
                isValid = validateString(value);
                break;
            case VALIDATION_TYPE:
                isValid = validateString(value);
                break;
            case VALIDATION_NIF:
                isValid = validateID(value);
                break;
            case VALIDATION_NIE:
                isValid = validateNIE(value);
                break;
            case VALIDATION_PASSPORT:
                isValid = validatePassport(value);
                break;
            case VALIDATION_NATIONAL_ID_CARD:
                isValid = validateString(value);
                break;

            case VALIDATION_CIF:
                isValid = validateCIF(value);
                break;

            case VALIDATION_POSTAL_CODE:
                isValid = validateZipCode(value);
                break;
            case VALIDATION_NUMBER:
                isValid = validateNumberFields(value);
                break;
            case VALIDATION_PHONE_NUMBER:
                isValid = validatePhoneNumber(value);
                break;
            case VALIDATION_CC_TYPE:
                isValid = false;
                break;

            case VALIDATION_CC_PLACEHOLDER:
                isValid = validateCreditCardHolder(value);
                break;
            case VALIDATION_CPF:
                isValid = validateCPF(value);
                break;
            case VALIDATION_CC_NUMBER:
                isValid = validateFormatCreditCardNumber(value);
                break;
        }

        return isValid;
    }

    public static boolean validateString(String value) {
        return value != null && !value.trim().equals("");
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().equals("")) {
            return false;
        }

        if (phoneNumber.matches("[0-9]{7,15}")) {
            return true;
        }

        return false;
    }

    public static boolean validateLenghtField(String value, int minCharacters, int maxCharacters) {
        if (value == null) {
            return minCharacters == 0;
        }

        return value.length() >= minCharacters && value.length() <= maxCharacters;
    }

    public static boolean validateCharactersField(String value, String charactersNotAllowed) {
        if (value == null || charactersNotAllowed == null) {
            return true;
        }

        for (int i = 0; i < value.length(); i++) {
            if (charactersNotAllowed.toUpperCase()
                    .contains(String.valueOf(value.charAt(i)).toUpperCase())) {
                return false;
            }
        }

        return true;
    }

    public static boolean validateTextFields(String value, int minCharacters, int maxCharacters) {
        if (validateLenghtField(value, minCharacters, maxCharacters)) {
            if (value == null || validateCharactersField(value,
                    "~!@#$%^&*_+=`|(){}[]:;\"<>,.?0123456789")) {
                return true;
            }
        }

        return false;
    }

    public static boolean validateTextAndNumberFields(String value, int minCharacters, int maxCharacters) {
        if (validateLenghtField(value, minCharacters, maxCharacters)) {
            if (value == null || validateCharactersField(value,
                    "~!@#$%^&*_-+=`|(){}[]:;\"<>,.?/")) {
                return true;
            }
        }

        return false;
    }

    public static boolean validateAddress(String value, int minCharacters, int maxCharacters) {
        if (validateLenghtField(value, minCharacters, maxCharacters)) {
            if (value == null || validateCharactersField(value, "~!@#$%^&*+=`|(){}[]:;\"<>?")) {
                return true;
            }
        }

        return false;
    }

    public static boolean validatePassport(String value) {
        if (validateLenghtField(value, 1, 15)) {
            return value.toUpperCase().matches("[A-Z0-9]+");
        }

        return false;
    }

    public static boolean validateNumberFields(String value) {
        if (value == null || value.trim().equals("")) {
            return false;
        }

        if (value.matches("[0-9]+")) {
            return true;
        }

        return false;
    }

    public static boolean validateZipCode(String value) {
        if (validateLenghtField(value, 1, 10)) {
            if (validateCharactersField(value, "~!@#$%^&*+=`|(){}[]:;\"'<>,.?/")) {
                return true;
            }
        }

        return false;
    }

    public static boolean validEmail(String value) {
        if (validateLenghtField(value, 6, 64)) {
            if (value.matches(
                    "[A-Z0-9a-z_%+-]+(\\.[A-Z0-9a-z_%+-]+)*@[A-Za-z0-9]+([\\-]*[A-Za-z0-9])*(\\.[A-Za-z0-9]+([\\-]*[A-Za-z0-9])*)*\\.[A-Za-z]{2,}")) {
                return true;
            }
        }

        return false;
    }

    public static boolean validateDate(Date date) {

        if (date == null) {
            return false;
        }

        return true;
    }

    public static boolean validateID(String number) {

        boolean isValid = false;

        if (number == null || number.length() < 2) {
            return false;
        }

        while (number.length() < 9) {
            number = "0" + number;
        }

        String idNumber = number.substring(0, 8);
        String idLetter = number.substring(8);

        if (!idNumber.matches("[0-9]+")) {
            return false;
        }

        try {
            String chars = "TRWAGMYFPDXBNJZSQVHLCKE";
            int mod = Integer.parseInt(idNumber) % chars.length();
            String com = "" + chars.charAt(mod);
            isValid = idLetter.equals(com);
        } catch (Exception e) {
            Log.e("Util.java", "" + e.getMessage());
        }

        return isValid;
    }

    public static boolean validateNIE(String nieDocument) {

        boolean response = false;

        if (nieDocument == null || nieDocument.length() < 8 || nieDocument.length() > 9) {
            return false;
        }

        while (nieDocument.length() < 10) {
            nieDocument = "0" + nieDocument;
        }

        //Get the idLetter
        String idLetter = nieDocument.substring(nieDocument.length() - 1);
        String xyz = String.valueOf(nieDocument.charAt(0)).toUpperCase();

        if (xyz.equals("0")) {
            xyz = String.valueOf(nieDocument.charAt(1)).toUpperCase();
        }

        //Get idNumber
        String idNumber = nieDocument.substring(0, 8);

        String chars = "TRWAGMYFPDXBNJZSQVHLCKE";

        if ("XYZ".contains(xyz)) {
            idNumber = idNumber.replaceAll("X", "0");
            idNumber = idNumber.replaceAll("Y", "1");
            idNumber = idNumber.replaceAll("Z", "2");

            try {
                int mod = Integer.parseInt(idNumber) % chars.length();

                response = idLetter.equals(String.valueOf(chars.charAt(mod)).toUpperCase());
            } catch (Exception e) {
                Log.e("Util.java", "" + e.getMessage());
            }
        }

        return response;
    }

    public static boolean validateCIF(String value) {

        List<String> characters;
        List<String> references = Arrays
                .asList("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R",
                        "S", "V", "W");
        List<String> lastValueReferencesLetter = Arrays.asList("K", "Q", "S");
        List<String> lastValueReferencesNumber = Arrays.asList("A", "B", "E", "H");

        if (value.length() < 9 || value == null) {
            return false;
        }

        characters = new ArrayList(Arrays.asList(value.toUpperCase().split("(?<!^)")));
        String firstCaracter = characters.get(0);

        if (!isLetter(firstCaracter)) {
            return false;
        }

        if (!references.contains(firstCaracter)) {
            return false;
        }


        String lastValue = characters.get(8);

        boolean containsMustBeLetter = lastValueReferencesLetter.contains(firstCaracter);
        boolean containsMustBeNumber = lastValueReferencesNumber.contains(firstCaracter);

        if (containsMustBeLetter || containsMustBeNumber) {

            if (containsMustBeLetter && !isLetter(lastValue)) {
                return false;
            }

            if (containsMustBeNumber && !isDigit(lastValue)) {
                return false;
            }

        }

        char[] subStringToValidate = value.substring(1, 8).toCharArray();

        int pairSum = 0;
        int oddSum = 0;

        for (int i = 0; i < subStringToValidate.length; i++) {

            boolean isPairPos = ((i + 1) % 2) == 0;

            if (isPairPos) {
                pairSum += Character.getNumericValue(subStringToValidate[i]);
            }

            if (!isPairPos) {

                char[] multi = String
                        .valueOf((Character.getNumericValue(subStringToValidate[i]) * 2)).toCharArray();
                int interSum = 0;

                for (char valueChar : multi) {
                    interSum += Character.getNumericValue(valueChar);
                }

                oddSum += interSum;

            }

        }

        char[] finalSum = String.valueOf((pairSum + oddSum)).toCharArray();
        int finalDigit = Character.getNumericValue(finalSum[finalSum.length - 1]);

        return ((10 - finalDigit) == 1);

    }


    public static boolean isLetter(String value) {
        return value.matches("[a-zA-Z]+");
    }

    public static boolean isDigit(final String value) {
        final char c = value.charAt(0);
        return (c >= '0' && c <= '9');
    }

    public static boolean validateCCV(String number, String creditCardType) {
        if (number == null || !number.matches("[0-9]+")) {
            return false;
        }

        int len = 3;
        if (creditCardType.equals("AX")) {
            len = 4;
        }

        return number.length() == len;
    }

    public static boolean validateCreditCardHolder(String holder) {
        if (holder == null || holder.trim().equals("")) {
            return false;
        }

        return holder.trim().matches("[0-9A-Z\\s-]+");
    }

    public static boolean validateCPF(String number) {

        if (validateLenghtField(number, 11, 11)) {
            if (number.matches("[0-9]+(\\.[0-9]+)?")) {
                return true;
            }
        }

        return false;
    }

    public static boolean validateCreditCardOfType(String cardType, String cardNumber) {

        cardNumber = cardNumber.replaceAll("\\s", "");

        if (cardNumber == null || cardType == null) {
            return false;
        }

        if (cardNumber.length() < 12 || cardNumber.length() > 19) {
            return false;
        }

        if (cardType.contains("-")) {
            cardType = cardType.substring(0, cardType.indexOf("-"));
        }

        List<String> mastercard = Arrays
                .asList(new String[]{"CM", "CA", "RM", "MC", "MD", "M6", "M4", "MP", "EC", "MI"});
        List<String> visa = Arrays
                .asList(new String[]{"E1", "VI", "RV", "VV", "VB", "3V", "EA", "VD", "DL", "VE"});

        List<Integer> lenghts = null;
        String[] rules = null;


        if (cardType.equals("4B")) {
            return true;
        }

        if (mastercard.contains(cardType)) {
            lenghts = Arrays.asList(new Integer[]{16});
            rules = new String[]{"51", "52", "53", "54", "55"};
        } else if (visa.contains(cardType)) {
            lenghts = Arrays.asList(new Integer[]{13, 16});
            rules = new String[]{"4"};
        } else if (cardType.equals("AX")) {
            lenghts = Arrays.asList(new Integer[]{15});
            rules = new String[]{"34", "37"};
        } else if (cardType.equals("DC")) {
            lenghts = Arrays.asList(new Integer[]{14});
            rules = new String[]{"30", "36", "38"};
        } else if (cardType.equals("MA")) {
            lenghts = Arrays.asList(new Integer[]{16});
            rules = new String[]{"50", "51", "52", "53", "54", "55"};
        } else {
            return true;
        }

        if (!validateFormatCreditCardNumber(cardNumber)) {
            return false;
        }

        //The cardNumber has one of the lenghts allowed, and the cardNumber starts with one of the rules
        if (lenghts.contains(cardNumber.length())) {
            for (String headdigits : rules) {
                if (cardNumber.startsWith(headdigits)) {
                    return true;
                }
            }
        }

        return false;

    }

    public static boolean validateFormatCreditCardNumber(String cardNumber) {
        final int kMagicSubtractionNumber = 48;

        cardNumber = cardNumber.replaceAll("\\s", "");

        if (cardNumber == null) {
            return false;
        }

        int lastIndex = cardNumber.length() - 1;

        int luhn = 0;

        boolean isValid = false;

        try {
            for (int i = 0; i < cardNumber.length(); i++) {
                int doubled = Integer.parseInt(String.valueOf(cardNumber.charAt(lastIndex - i)));

                if (i % 2 == 1) {
                    doubled *= 2;
                }

                String strDoubled = String.valueOf(doubled);

                if (strDoubled.length() > 1) {
                    luhn = luhn + Integer.parseInt(String.valueOf(strDoubled.charAt(0)))
                            + Integer.parseInt(String.valueOf(strDoubled.charAt(1)));
                } else {
                    luhn += doubled;
                }
            }

            isValid = luhn % 10 == 0;
        } catch (Exception e) {
            Log.e("Util.java", "" + e.getMessage());
        }

        return isValid;
    }

    public static String getStringNullable(String string) {
        return string == null ? "" : string;
    }
}

