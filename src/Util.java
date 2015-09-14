import java.util.*;

/**
 * Created by Nick on 9/13/2015.
 */
public class Util {

    public static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return -((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue()); // minus to sort largest to smallest
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    public static <T> void addDataToMap(HashMap<T, Integer> map, T data) {
        if (map.get(data) == null) {
            map.put(data, 1);
        } else {
            map.put(data, map.get(data) + 1);
        }
    }

    public static String padString(String stringToPad, char padding, int desiredLength) {
        String ret = "";

        int charsToAdd = desiredLength - stringToPad.length();
        for (int i = 0; i < charsToAdd; i++) {
            ret += padding;
        }

        ret += stringToPad;

        return ret;
    }

    public static String padLongBinaryString(long data) {
        return padString(Long.toBinaryString(data), '0', 64);
    }

}
