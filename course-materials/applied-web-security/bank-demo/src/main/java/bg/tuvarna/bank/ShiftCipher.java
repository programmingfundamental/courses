package bg.tuvarna.bank;

/** Deliberately weak teaching cipher. The prefix identifies the digit rotation. */
public final class ShiftCipher {
    private static final String PREFIX = "ROT5:";

    private ShiftCipher() {}

    private static String rotate(String digits) {
        StringBuilder result = new StringBuilder(digits.length());
        for (char digit : digits.toCharArray()) {
            result.append((char) ('0' + (digit - '0' + 5) % 10));
        }
        return result.toString();
    }

    public static String encrypt(String egn) {
        if (egn == null || !egn.matches("[0-9]{10}")) {
            throw new IllegalArgumentException("ЕГН трябва да съдържа точно 10 цифри.");
        }
        return PREFIX + rotate(egn);
    }

    public static String decrypt(String ciphertext) {
        if (ciphertext == null || !ciphertext.matches("ROT5:[0-9]{10}")) {
            throw new IllegalArgumentException("Очаква се ROT5: и 10 цифри.");
        }
        return rotate(ciphertext.substring(PREFIX.length()));
    }
}
