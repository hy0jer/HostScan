package example.customscanchecks;

public class Utils {
    public static int count_string(String string, char find_string) {
        int[] count = new int[2];
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == find_string) {
                count[0]++;
                if (count[0] >= 2) {
                    count[1] = i;
                }
            }
        }
        return count[1];
    }
}
