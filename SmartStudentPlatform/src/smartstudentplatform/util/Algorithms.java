package smartstudentplatform.util;

import java.util.List;
import smartstudentplatform.model.Student;

public final class Algorithms {
    private Algorithms() {}

    /* ---------- LINEAR SEARCH by ID ---------- */
    public static Student linearSearchById(List<Student> arr, String id) {
        for (Student s : arr) {
            if (s.getId().equalsIgnoreCase(id)) return s;
        }
        return null;
    }

    /* ---------- BINARY SEARCH by ID (arr must be sorted by ID) ---------- */
    public static Student binarySearchById(List<Student> arr, String id) {
        int lo = 0, hi = arr.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int cmp = arr.get(mid).getId().compareToIgnoreCase(id);
            if (cmp == 0) return arr.get(mid);
            if (cmp < 0) lo = mid + 1; else hi = mid - 1;
        }
        return null;
    }

    /* ---------- BUBBLE SORT by CGPA (DESC) ---------- */
    public static void bubbleSortByCgpa(List<Student> arr) {
        int n = arr.size();
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr.get(j).getCgpa() < arr.get(j + 1).getCgpa()) {
                    Student tmp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, tmp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    /* ---------- QUICKSORT by NAME (ASC, case-insensitive) ---------- */
    public static void quickSortByName(List<Student> arr) {
        quickSortByName(arr, 0, arr.size() - 1);
    }

    private static void quickSortByName(List<Student> arr, int lo, int hi) {
        if (lo >= hi) return;
        int p = partitionByName(arr, lo, hi);
        quickSortByName(arr, lo, p - 1);
        quickSortByName(arr, p + 1, hi);
    }

    private static int partitionByName(List<Student> arr, int lo, int hi) {
        String pivot = arr.get(hi).getName().toLowerCase();
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (arr.get(j).getName().toLowerCase().compareTo(pivot) <= 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, hi);
        return i + 1;
    }

    private static void swap(List<Student> arr, int i, int j) {
        Student tmp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, tmp);
    }

    /* ---------- SIMPLE INSERTION SORT by ID (ASC) (handy before binary search) ---------- */
    public static void insertionSortById(List<Student> arr) {
        for (int i = 1; i < arr.size(); i++) {
            Student key = arr.get(i);
            int j = i - 1;
            while (j >= 0 && arr.get(j).getId().compareToIgnoreCase(key.getId()) > 0) {
                arr.set(j + 1, arr.get(j));
                j--;
            }
            arr.set(j + 1, key);
        }
    }
}
