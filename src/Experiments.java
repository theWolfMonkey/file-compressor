import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Experiments {
    private static <K, V extends Comparable<V>> Map<K, V> sortMap(HashMap<K, V> mapToSort) {
        Comparator<Map.Entry<K, V>> comparator = new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = o1.getValue().compareTo(o2.getValue());
                if (compare == 0) {
                    return -1;
                }
                return -compare;
            }
        };

        Map<K, V> sortedData = new TreeMap<>();
//        Collections.sort(mapToSort, comparator);
//        sortedData.putAll(mapToSort);

//        MapUtil

        return sortedData;
    }
}
