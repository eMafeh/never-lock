import common.util.DateUtil;
import common.util.ExceptionUtil;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MaShiBingKeChengBiao {

    @Test
    //排序展示总时长和总课次
    public static void showTime() {
        list.stream()
                .sorted(Comparator.comparingInt(Ke::time))
                .map(Ke::showTime)
                .forEach(System.out::println);
    }

    @Test
    //排序展示总时长和总课次
    public static void showKe() {
        String name = "Maven【马士兵教育】";
        int n = 15;
        Collection<Node> collect = list.stream()
                .filter(ke -> ke.name.equals(name))
                .flatMap(ke -> ke.nodes.stream())
                .filter(node -> node.num > n)
                .filter(node -> node.history() > 0)
                .collect(Collectors.toList());
        collect.forEach(System.out::println);
        System.out.println(collect.stream()
                .mapToInt(node -> node.l)
                .sum());
    }

    @Test
    //排序展示总时长和总课次
    public static void showList() {
        list.stream()
                .flatMap(ke -> ke.nodes.stream())
                .sorted()
                .map(node -> node.ke.name + "\t\t\t" + node.toString())
                .forEach(System.out::println);
    }

    @Test
    //排序展示下几节课
    public static void showNext() {
        list.stream()
                .flatMap(ke -> ke.nodes.stream())
                .sorted()
                .filter(node -> node.sort > System.currentTimeMillis() && node.sort < System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 10)
                .map(node -> node.ke.name + "\t\t\t" + node.toString())
                .forEach(System.out::println);
    }

    final static ArrayList<Ke> list = new ArrayList<>();

    static {
        List<String> strings = null;
        try {
            strings = Files.readAllLines(Paths.get("src/test/java/keChengBiao"));
        } catch (IOException e) {
            ExceptionUtil.throwT(e);
        }
        List<String[]> collect = strings
                .stream()
                .skip(1)
                .map(s -> s.split("\t"))
                .collect(Collectors.toList());
        int all = collect.stream()
                .mapToInt(s -> s.length)
                .max()
                .orElse(0);


        String[] head = collect.get(0);
        String name = "";
        for (int i = 0; i < all; i += 2) {
            name = empty(head, i) ? name + "*" : head[i];
            list.add(new Ke(i, name));
        }
        for (int line = 1, collectSize = collect.size(); line < collectSize; line++) {
            String[] a = collect.get(line);
            for (Ke ke : list) {
                ke.add(a);
            }
        }
    }

    private static boolean empty(String[] strings, int index) {
        return index < 0 || index >= strings.length || strings[index] == null || strings[index].equals("");
    }

    private static class Ke {
        final int index;
        final String name;
        final List<Node> nodes = new ArrayList<>();
        int time;

        public Ke(int index, String name) {
            this.index = index;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + ':' + nodes;
        }

        public void add(String[] a) {
            if (!empty(a, index)) {
                nodes.add(new Node(this, nodes.size() + 1, a[index], a[index + 1]));
            }
        }

        public int time() {
            if (time == 0) time = nodes.stream()
                    .mapToInt(node -> node.l)
                    .sum();
            return time;
        }

        public int history() {
            return nodes.stream()
                    .mapToInt(Node::history)
                    .sum();
        }

        public String showTime() {
            return name + "\t" + history() + "/" + time() + "分钟" + "\t" + nodes.size() + "结课";
        }
    }

    private static class Node implements Comparable<Node> {
        final Ke ke;
        final int num;
        final String name;
        final String time;
        private static final int length = "2020-05-23 周六 09:00-11:00".length();
        final Date begin;
        final Date end;
        final long sort;
        final int l;


        public Node(Ke ke, int num, String name, String time) {
            this.ke = ke;
            this.num = num;
            this.name = name;
            this.time = time;

            int tmp = 0;
            Date b = null, e = null;
            long sort = 0;
            if (time != null)
                if (time.length() == length) {
                    String head = time.substring(0, 10);
                    b = DateUtil.parse("yyyy-MM-ddhh:mm", head + time.substring(14, 19));
                    e = DateUtil.parse("yyyy-MM-ddhh:mm", head + time.substring(20, 25));
                    sort = b.getTime();
                    tmp = (int) ((e.getTime() - b.getTime()) / 1000 / 60);
                } else {
                    tmp = Integer.parseInt(time);
                    sort = tmp;
                }
            begin = b;
            end = e;
            l = tmp;
            this.sort = sort;
        }

        @Override
        public String toString() {
            return "{" + name + '\t' + time +
                    '}';
        }

        @Override
        public int compareTo(Node o) {
            return Long.compare(sort, o.sort);
        }

        public int history() {
            return begin != null && begin.getTime() > System.currentTimeMillis() ? 0 : l;
        }
    }
}
