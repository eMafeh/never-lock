package unlock;

import common.util.DateUtil;
import common.util.FileUtil;
import common.util.JackSonUtil;
import common.util.MapUtil;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaShiBingKeChengBiaoDb {
    private final static Set<String> finishKe = Stream.of("Maven【马士兵教育】")
            .collect(Collectors.toSet());
    private final static Set<String> ignoreKe = Stream.of("Python大数据全栈工程师【马士兵教育】")
            .collect(Collectors.toSet());

    private static ArrayList<Ke> KES = new ArrayList<>();
    private static ArrayList<Ke> finishKES = new ArrayList<>();
    private static ArrayList<Ke> ignoreKES = new ArrayList<>();

    public static Stream<Ke> getKes() {
        return KES.stream();
    }

    public static Stream<Ke> getFinishKES() {
        return finishKES.stream();
    }

    public static Stream<Ke> getIgnoreKES() {
        return ignoreKES.stream();
    }

    static {
        init();
    }

    public static synchronized void init() {
        KES = new ArrayList<>();
        finishKES = new ArrayList<>();
        ignoreKES = new ArrayList<>();
//        Map<String, Ke> keMap =
        FileUtil.readAllLines(Paths.get("E:\\data\\never-lock\\src\\test\\java\\keChengJsons.txt"))
                .stream()
                .map(s -> new Ke(JackSonUtil.parse(s)))
//                .collect(Collectors.toMap(ke -> ke.cname + ke.tname, ke -> ke, (a, b) -> b));
//        keMap.values()
                .forEach(ke -> (!finishKe.contains(ke.cname) ? ignoreKe.contains(ke.cname) ? ignoreKES : KES : finishKES).add(ke));
    }

    public static class Ke {
        public final int cid;
        public final int term_id;
        public final int isTaskDone;
        public final String tname;
        public final String cname;
        public final int latest_study_ts;
        public final int endtime;
        public final int bgtime;

        public final List<K1> k1s;
        public final int times;
        public final int size;
        public final String url;

        public Ke(Map<String, Object> parse) {
            this.cid = MapUtil.get(parse, "result", "plan", "cid");
            this.term_id = MapUtil.get(parse, "result", "plan", "term_id");
            this.isTaskDone = MapUtil.get(parse, "result", "comment_result", "isTaskDone");
            this.tname = MapUtil.get(parse, "result", "plan", "tname");
            this.cname = MapUtil.get(parse, "result", "plan", "cname");
            this.latest_study_ts = MapUtil.get(parse, "result", "plan", "latest_study_ts");
            List<Map<String, Object>> sub_course_list = MapUtil.get(parse, "result", "plan", "chapter_list", 0, "sub_course_list");
            this.k1s = sub_course_list.stream()
                    .map(K1::new)
                    .collect(Collectors.toList());
            Map<String, Integer> applied_terms = MapUtil.get(parse, "result", "applied_terms", 0);
            this.endtime = applied_terms.get("endtime");
            this.bgtime = applied_terms.get("bgtime");
            this.times = k1s.stream()
                    .flatMap(k1 -> k1.k2s.stream())
                    .mapToInt(k2 -> k2.times)
                    .sum();
            this.size = k1s.stream()
                    .mapToInt(k1 -> k1.k2s.size())
                    .sum();
            this.url = "https://ke.qq.com/user/index/index.html#/plan/cid=" + cid + "&term_id=" + term_id;

        }

        public int history() {
            int l = (int) (System.currentTimeMillis() / 1000);
            return k1s.stream()
                    .flatMap(k1 -> k1.k2s.stream())
                    .mapToInt(k2 -> k2.endtime > l ? 0 : k2.times)
                    .sum();
        }
    }


    public static class K1 {
        public final int endtime;
        public final List<K2> k2s;
        public final int bgtime;

        public K1(Map<String, Object> reduce) {
            this.endtime = MapUtil.get(reduce, "endtime");
            this.bgtime = MapUtil.get(reduce, "bgtime");
            List<Map<String, Object>> task_list = MapUtil.get(reduce, "task_list");
            this.k2s = task_list.stream()
                    .map(K2::new)
                    .collect(Collectors.toList());
        }
    }

    public static class K2 {
        public final Integer update_ts;
        public final Integer latest_pos;
        public final int endtime;
        public final int type;
        public final int bgtime;
        public final int times;
        public final String name;
        public final int complement_degree;
        public final String taid;

        public K2(Map<String, Object> map) {
            this.update_ts = MapUtil.get(map, "update_ts");
            this.latest_pos = MapUtil.get(map, "latest_pos");
            this.endtime = MapUtil.get(map, "endtime");
            this.type = MapUtil.get(map, "type");
            this.bgtime = MapUtil.get(map, "bgtime");
            this.name = MapUtil.get(map, "name");
            this.complement_degree = MapUtil.get(map, "complement_degree");
            this.taid = MapUtil.get(map, "taid");


            Integer times = MapUtil.get(map, "resid_ext", "times");
            if (times == null) {
                Object o = MapUtil.get(map, "resid_ext", "list");
                times = o != null ? MapUtil.get(o, 0, "times") : Integer.valueOf(endtime - bgtime);
            }
            this.times = times;
        }

        public String humanTime() {
            return bgtime == 0 ? "" : DateUtil.format("yyyy-MM-dd HH:mm", 1000L * bgtime);
        }
    }
}
