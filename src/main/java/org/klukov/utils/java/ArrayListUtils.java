package org.klukov.utils.java;

import java.util.ArrayList;
import java.util.List;

public class ArrayListUtils {

    public <T> ArrayList<ArrayList<T>> shallowCopyTo2DArrayList(List<List<T>> list) {
        ArrayList<ArrayList<T>> result = new ArrayList<>();
        for (List<T> sublist : list) {
            result.add(new ArrayList<>(sublist));
        }
        return result;
    }

    public <T> ArrayList<ArrayList<ArrayList<T>>> shallowCopyTo3DArrayList(
            List<List<List<T>>> list) {
        ArrayList<ArrayList<ArrayList<T>>> result = new ArrayList<>();
        for (List<List<T>> sublist : list) {
            ArrayList<ArrayList<T>> subResult = new ArrayList<>();
            for (List<T> innerList : sublist) {
                subResult.add(new ArrayList<>(innerList));
            }
            result.add(subResult);
        }
        return result;
    }
}
