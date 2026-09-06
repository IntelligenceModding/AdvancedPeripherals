package de.srendi.advancedperipherals.common.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    /*
     * private constructor to hide the public one.
     */
    private ListUtil() {
    }

    /**
     * Used to create all possible combinations of the elements in the given lists.
     * @param lists a list of lists with elements to create combinations from
     * @param <T>   the element type
     * @return a list containing all possible combinations in form of element lists with size equals lists.size()
     */
    public static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> result = new ArrayList<>();
        if (lists == null || lists.isEmpty() || lists.stream().anyMatch(list -> list == null || list.isEmpty())) {
            return result;
        }
        result.addAll(lists.getFirst().stream().map(List::of).toList());
        for (int listIndex = 1; listIndex < lists.size(); listIndex++) {
            List<List<T>> newResult = new ArrayList<>();
            for (T element : lists.get(listIndex)) {
                for (List<T> resultList : result) {
                    List<T> newList = new ArrayList<>(resultList);
                    newList.add(element);
                    newResult.add(newList);
                }
            }
            result = newResult;
        }
        return result;
    }
}
