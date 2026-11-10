package bg.tuvarna.bank;

/** Teaching cipher only: independent rotations of Latin letters and decimal digits. */
public final class ShiftCipher {
    private ShiftCipher() {}
    static final int KEY = 3;
    public static String rotate(String value, int shift) {
        StringBuilder out = new StringBuilder(value.length());
        for (char c : value.toCharArray()) {
            if (c >= 'A' && c <= 'Z') out.append((char) ('A' + Math.floorMod(c - 'A' + shift, 26)));
            else if (c >= '0' && c <= '9') out.append((char) ('0' + Math.floorMod(c - '0' + shift, 10)));
            else out.append(c);
        }
        return out.toString();
    }
    public static String encrypt(String value) { return rotate(value, KEY); }
    public static String guess(String value, int shift) { return rotate(value, -shift); }
}
