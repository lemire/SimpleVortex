import java.util.*;
import java.util.Map.Entry;

/**
 * This is a simple Java class to sort a list of csv in vortex order. Put
 * together in a few minutes. Not nice. Not efficient. Just as demo.
 */
public class SimpleVortex {

        public static void main(String[] args) {
                String[] demo = { "a,B", "b,C", "a,B", "c,C", "a,A", "a,A",
                        "b,B", "b,A", "a,A", "b,A" };
                String[] sorted = sort(demo);
                for (String line : sorted)
                        System.out.println(line);
        }

        /**
         * Assumes that the strings are comma-separated-values.
         */
        public static String[] sort(String[] table) {
                ArrayList<HashMap<String, Integer>> mapfromvaluestokey = new ArrayList<HashMap<String, Integer>>();
                ArrayList<HashMap<Integer, String>> reversemapfromvaluestokey = new ArrayList<HashMap<Integer, String>>();
                getMaps(table, mapfromvaluestokey, reversemapfromvaluestokey);

                for (HashMap<String, Integer> x : mapfromvaluestokey)
                        System.out.println(x);
                AttributeValue[][] coded = toAttributeValues(toInts(table, mapfromvaluestokey));
                Arrays.sort(coded, AVComparator);
                return toStrings(toInts(coded), reversemapfromvaluestokey);
        }

        public static String[] toValues(String s) {
                return s.split(",");// this is crude, for basic CSV only
        }

        private static List<HashMap<String, Integer>> getHistogram(
                String[] table) {
                ArrayList<HashMap<String, Integer>> answer = new ArrayList<HashMap<String, Integer>>();
                for (String line : table) {
                        String[] vals = toValues(line);
                        while (answer.size() < vals.length)
                                answer.add(new HashMap<String, Integer>());
                        for (int k = 0; k < vals.length; ++k)
                                if (!answer.get(k).containsKey(vals[k]))
                                        answer.get(k).put(vals[k], 1);
                                else
                                        answer.get(k).put(vals[k],
                                                1 + answer.get(k).get(vals[k]));
                }
                return answer;

        }

        private static void getMaps(String[] table,
                ArrayList<HashMap<String, Integer>> mapfromvaluestokey,
                ArrayList<HashMap<Integer, String>> reversemapfromvaluestokey

        ) {
                final List<HashMap<String, Integer>> histograms = getHistogram(table);

                for (HashMap<String, Integer> h : histograms) {

                        final ArrayList<Pair<Integer, String>> freqvaluevec = new ArrayList<Pair<Integer, String>>();
                        for (Entry<String, Integer> e : h.entrySet())
                                freqvaluevec.add(new Pair<Integer, String>(e
                                        .getValue(), e.getKey()));
                        Collections.sort(freqvaluevec,
                                Collections.reverseOrder());// Collections.sort(freqvaluevec);//,Collections.reverseOrder());
                        int k = 0;
                        HashMap<String, Integer> tm = new HashMap<String, Integer>();
                        HashMap<Integer, String> rtm = new HashMap<Integer, String>();

                        for (Pair<Integer, String> p : freqvaluevec) {
                                tm.put(p.snd, k);
                                rtm.put(k, p.snd);
                                k++;
                        }
                        mapfromvaluestokey.add(tm);
                        reversemapfromvaluestokey.add(rtm);
                }
        }

        private static int[][] toInts(String[] table,
                ArrayList<HashMap<String, Integer>> map) {
                int[][] answer = new int[table.length][];
                for (int k = 0; k < table.length; ++k) {
                        String[] line = toValues(table[k]);
                        answer[k] = new int[line.length];
                        for (int z = 0; z < answer[k].length; ++z) {
                                answer[k][z] = map.get(z).get(line[z]);
                        }
                }
                return answer;
        }

        private static String[] toStrings(int[][] table,
                ArrayList<HashMap<Integer, String>> map) {
                String[] answer = new String[table.length];
                for (int k = 0; k < table.length; ++k) {
                        int[] line = table[k];
                        answer[k] = new String();
                        for (int z = 0; z < line.length; ++z)
                                answer[k] += map.get(z).get(line[z]) + ",";
                }
                return answer;
        }

        private static AttributeValue[] toAttributeValues(int[] x) {
                AttributeValue[] answer = new AttributeValue[x.length];
                for (int k = 0; k < x.length; ++k)
                        answer[k] = new AttributeValue(k, x[k]);
                Arrays.sort(answer,AttributeValue.valuethendimcomp);
                return answer;

        }

        private static int[][] toInts(AttributeValue[][] table) {
                int[][] answer = new int[table.length][];
                for (int k = 0; k < table.length; ++k) {
                        answer[k] = new int[table[k].length];
                        for (int z = 0; z < answer[k].length; ++z) {
                                answer[k][table[k][z].dim] = table[k][z].value;
                        }
                }
                return answer;
        }
        private static AttributeValue[][] toAttributeValues(int[][] x) {
                AttributeValue[][] answer = new AttributeValue[x.length][];
                for (int k = 0; k < x.length; ++k) {
                        answer[k] = toAttributeValues(x[k]);
                }
                return answer;

        }

       static Comparator<AttributeValue[]> AVComparator = new Comparator<AttributeValue[]>() {
               int order = 1;
               public int compare(AttributeValue[] x1, AttributeValue[] x2) {
                       order = 1;
                       for(int k = 0; k<x1.length;++k) {
                               int resultofcomp = x1[k].compare( x2[k]);
                               if(resultofcomp != 0)
                                       return resultofcomp *order;
                               order *= -1;
                       }
                       return 0;
               }};

        static Comparator<int[]> VortexComparator = new Comparator<int[]>() {
                public int compare(int[] r1, int[] r2) {
                        AttributeValue[] p1 = toAttributeValues(r1);
                        AttributeValue[] p2 = toAttributeValues(r2);
                        Arrays.sort(p1, AttributeValue.valuethendimcomp);
                        Arrays.sort(p1, AttributeValue.valuethendimcomp);
                        int order = 1;
                        for (int k = 0; k < p1.length; ++k) {
                                int resultofcomp = AttributeValue.valuethendimcomp
                                        .compare(p1[k], p2[k]);
                                if (resultofcomp != 0)
                                        return resultofcomp * order;
                                order *= -1;
                        }
                        return 0;
                }
        };

}

class Pair<T extends Comparable<? super T>, U extends Comparable<? super U>>
        implements Comparable<Pair<T, U>> {
        public T fst;
        public U snd;

        public Pair(T a, U b) {
                fst = a;
                snd = b;
        }

        public String toString() {
                return "('" + fst + "','" + snd + "')";
        }

        public T first() {
                return fst;
        }

        public U second() {
                return snd;
        }

        public int hashCode() {
                return fst.hashCode() ^ snd.hashCode();
        }

        public boolean equals(Object oo) {
                if (oo instanceof Pair) { // un-pretty
                        Pair o = (Pair) oo;
                        return fst.equals(o.fst) && snd.equals(o.snd);
                }
                return false;
        }

        public int compareTo(Pair<T, U> o) {
                Pair<T, U> x = o;
                int ans;
                if ((ans = fst.compareTo(x.fst)) != 0)
                        return ans;
                return snd.compareTo(x.snd);
        }
}

class AttributeValue {
        public int dim;
        public int value;

        int compare(AttributeValue o2) {
                if (this.value - o2.value != 0)
                        return this.value - o2.value;
                return this.dim - o2.dim;
        }

        public AttributeValue(int d, int v) {
                dim = d;
                value = v;
        }

        public static Comparator<AttributeValue> valuethendimcomp = new Comparator<AttributeValue>() {
                public int compare(AttributeValue o1, AttributeValue o2) {
                        if (o1.value - o2.value != 0)
                                return o1.value - o2.value;
                        return o1.dim - o2.dim;
                }
        };
        /*public static Comparator<AttributeValue> dimthenvaluecomp = new Comparator<AttributeValue>() {
                public int compare(AttributeValue o1, AttributeValue o2) {
                        if (o1.dim - o2.dim != 0)
                                return o1.dim - o2.dim;
                        return o1.value - o2.value;
                }
        };*/

        @Override
        public boolean equals(Object av) {
                // if(!(av instanceof AttributeValue)) return false;
                AttributeValue avv = (AttributeValue) av;
                return (avv.value == value) && (avv.dim == dim);
        }

        @Override
        public int hashCode() {
                // assume that dim<=32, then dim << 26 should be smaller than
                // 1<<31.
                return value + (dim << 26);
        }

        public String toString() {
                return "dim : " + dim + " value : " + value;
        }
}
