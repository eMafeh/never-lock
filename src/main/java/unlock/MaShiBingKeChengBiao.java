package unlock;

import common.util.DateUtil;
import common.util.ExceptionUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaShiBingKeChengBiao {
    private final static ArrayList<Ke> KES = new ArrayList<>();
    private final static ArrayList<Ke> finishKES = new ArrayList<>();
    private final static ArrayList<Ke> ignoreKES = new ArrayList<>();
    private final static Set<String> finishKe = Stream.of("Maven【马士兵教育】")
            .collect(Collectors.toSet());
    private final static Set<String> ignoreKe = Stream.of("Python大数据全栈工程师【马士兵教育】")
            .collect(Collectors.toSet());

    //排序展示总时长和总课次
    public static void showTime() {
        KES.stream()
                .sorted(Comparator.comparingInt(Ke::time))
                .map(Ke::showTime)
                .forEach(System.out::println);
    }

    //排序展示总时长和总课次
    public static void showKe() {
        String name = "Maven【马士兵教育】";
        int n = 18;
        Collection<Node> collect = KES.stream()
                .filter(ke -> ke.name.startsWith(name))
                .flatMap(ke -> ke.nodes.stream())
                .filter(node -> node.num > n)
                .filter(node -> node.history() > 0)
                .collect(Collectors.toList());
        collect.forEach(System.out::println);
        System.out.println(collect.stream()
                .mapToInt(node -> node.l)
                .sum());
    }

    //排序展示所有的课
    public static void showList() {
        KES.stream()
                .flatMap(ke -> ke.nodes.stream())
                .sorted()
                .map(node -> node.ke.name + "\t" + node.toString())
                .forEach(System.out::println);
    }

    //排序展示下几节课
    public static List<Node> showNext() {
        return KES.stream()
                .flatMap(ke -> ke.nodes.stream())
                .sorted()
                .filter(node -> node.sort > System.currentTimeMillis() && node.sort < System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 10)
                .collect(Collectors.toList());
    }


    static {
        List<String> strings = null;
        try {
            strings = Files.readAllLines(Paths.get("E:\\data\\never-lock\\src\\test\\java\\keChengBiao"));
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
            if (finishKe.contains(name))
                finishKES.add(new Ke(i, name));
            else if (ignoreKe.contains(name)) {
                ignoreKES.add(new Ke(i, name));
            } else KES.add(new Ke(i, name));
        }
        for (int line = 1, collectSize = collect.size(); line < collectSize; line++) {
            String[] a = collect.get(line);
            Stream.of(finishKES, ignoreKES, KES)
                    .flatMap(Collection::stream)
                    .distinct()
                    .forEach(ke -> ke.add(a));
        }
    }

    public static Stream<Ke> getKes() {
        return KES.stream();
    }
    public static Stream<Ke> getFinishKES() {
        return finishKES.stream();
    }
    public static Stream<Ke> getIgnoreKES() {
        return ignoreKES.stream();
    }
    private static boolean empty(String[] strings, int index) {
        return index < 0 || index >= strings.length || strings[index] == null || strings[index].equals("");
    }

    public static class Ke {
        public final int index;
        public final String name;
        public final List<Node> nodes = new ArrayList<>();
        public int time;

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

    public static class Node implements Comparable<Node> {
        public final Ke ke;
        public final int num;
        public final String name;
        public final String time;
        private static final int length = "2020-05-23 周六 09:00-11:00".length();
        public final Date begin;
        public final Date end;
        public final long sort;
        public final int l;


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
            return time + '\t' + name;
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
/*
function copyToClipboard (text) {
    var textArea = document.createElement("textarea");
      textArea.style.position = 'fixed';
      textArea.style.top = '0';
      textArea.style.left = '0';
      textArea.style.width = '2em';
      textArea.style.height = '2em';
      textArea.style.padding = '0';
      textArea.style.border = 'none';
      textArea.style.outline = 'none';
      textArea.style.boxShadow = 'none';
      textArea.style.background = 'transparent';
      textArea.value = text;
      document.body.appendChild(textArea);
      textArea.select();

      try {
        var successful = document.execCommand('copy');

      } catch (err) {
        alert('该浏览器不支持点击复制到剪贴板');
      }

      document.body.removeChild(textArea);
}
for(let sub of document.getElementsByClassName("detail-item-ctn expend always-show"))sub.remove();let excelresult=""; for(let sub of document.getElementsByClassName("task-item-ctn"))excelresult+=sub.childNodes[1].innerText+"\t"+sub.childNodes[2].innerText.replace("分钟","")+"\n";console.clear(); copyToClipboard ( excelresult);console.log(excelresult);
*/