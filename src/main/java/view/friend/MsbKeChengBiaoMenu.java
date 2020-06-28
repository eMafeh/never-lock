package view.friend;

import nio.core.User;
import unlock.MaShiBingKeChengBiaoDb;
import unlock.MaShiBingKeChengBiaoDb.Ke;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static view.common.ViewConstance.LEAVE;

public class MsbKeChengBiaoMenu extends JMenu {
    private final JMenu 待学 = new JMenu("待学");
    private final JMenu 已学 = new JMenu("已学");
    private final JMenu 忽略 = new JMenu("忽略");
    private final JMenu 近期 = new JMenu("近期");
    private final JMenuItem 刷新 = new JMenuItem();

    private static void handle(JMenu menu, Stream<Ke> kes) {
        menu.removeAll();
        kes.sorted(Comparator.comparingInt(a -> a.times))
                .forEach(ke -> {
                    JMenuItem item = new JMenuItem();
                    if (ke.latest_study_ts > 0) item.setBackground(Color.CYAN);
                    menu.add(item);
                    AbstractAction action = new AbstractAction(ke.cname + ke.tname) {
                        {
                            putValue(Action.SHORT_DESCRIPTION, ke.history() + "/" + ke.times + "分钟" + "    " + ke.size + "节课");
                        }

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            User.SELF.newMsg(ke.k1s.stream()
                                    .flatMap(k1 -> k1.k2s.stream())
                                    .map(node -> node.humanTime() + " " + node.times + "(" + node.complement_degree / 100d + "%)" + "      " + node.name)
                                    .collect(Collectors.joining("\n")) + "\n" + ke.k1s.stream()
                                    .flatMap(k1 -> k1.k2s.stream())
                                    .mapToInt(node -> node.times * (10000 - node.complement_degree) / 10000)
                                    .sum());
                        }
                    };
                    item.setAction(action);
                });
    }

    public MsbKeChengBiaoMenu() {
        super("课程表");
        待学.setToolTipText("待学课程");
        待学.setBackground(LEAVE);
        add(待学);

        已学.setToolTipText("已学课程");
        已学.setBackground(LEAVE);
        add(已学);

        忽略.setToolTipText("忽略课程");
        忽略.setBackground(LEAVE);
        add(忽略);

        近期.setToolTipText("近期课程安排");
        近期.setBackground(LEAVE);
        add(近期);

        刷新.setAction(new AbstractAction("刷新") {
            {
                putValue(Action.SHORT_DESCRIPTION, "刷新课程安排");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                刷新();
            }
        });
        刷新.setBackground(LEAVE);
        add(刷新);
    }

    private void 刷新() {
        MaShiBingKeChengBiaoDb.init();
        handle(待学, MaShiBingKeChengBiaoDb.getKes());
        handle(已学, MaShiBingKeChengBiaoDb.getFinishKES());
        handle(忽略, MaShiBingKeChengBiaoDb.getIgnoreKES());
        近期.removeAll();
        long now = System.currentTimeMillis() / 1000;
        MaShiBingKeChengBiaoDb.getKes()
                .flatMap(ke -> ke.k1s.stream()
                        .flatMap(k1 -> k1.k2s.stream())
                        .filter(k2 -> k2.endtime > now && k2.bgtime < now + 60 * 60 * 24 * 10)
                        .map(node -> {
                            JMenuItem item = new JMenuItem(node.humanTime() + "      " + node.name);
                            item.setToolTipText(ke.cname + ke.tname);
                            if (ke.latest_study_ts > 0) item.setBackground(Color.CYAN);
                            return item;
                        }))
                .sorted(Comparator.comparing(AbstractButton::getText))
                .forEach(近期::add);
        repaint();
        User.SELF.newMsg(MaShiBingKeChengBiaoDb.getKes()
                .map(ke -> ke.cname + ke.tname + "       " + ke.url)
                .collect(Collectors.joining("\n")));
        User.SELF.newMsg(MaShiBingKeChengBiaoDb.getKes()
                .mapToInt(ke -> ke.times)
                .sum() + "");
    }
}
