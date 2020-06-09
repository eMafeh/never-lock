package view.friend;

import nio.core.User;
import unlock.MaShiBingKeChengBiao;
import unlock.MaShiBingKeChengBiao.Ke;
import unlock.MaShiBingKeChengBiao.Node;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static view.common.ViewConstance.LEAVE;

public class MsbKeChengBiaoMenu extends JMenu {
    JMenu 待学 = new JMenu("待学");
    JMenu 已学 = new JMenu("已学");
    JMenu 忽略 = new JMenu("忽略");
    JMenu 近期 = new JMenu("近期");
    JMenuItem 刷新 = new JMenuItem();

    private static void handle(JMenu menu, Stream<Ke> kes) {
        kes.sorted(Comparator.comparingInt(Ke::time))
                .forEach(ke -> {
                    JMenuItem sub = new JMenuItem();
                    menu.add(sub);
                    AbstractAction action = new AbstractAction(ke.name) {
                        {
                            putValue(Action.SHORT_DESCRIPTION, ke.history() + "/" + ke.time() + "分钟" + "    " + ke.nodes.size() + "节课");
                        }

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            User.SELF.newMsg(ke.nodes.stream()
                                    .map(node -> node.time + "      " + node.name)
                                    .collect(Collectors.joining("\n")));
                        }
                    };
                    sub.setAction(action);
                });
    }

    public MsbKeChengBiaoMenu() {
        super("课程表");
        待学.setToolTipText("待学课程");
        待学.setBackground(LEAVE);
        handle(待学, MaShiBingKeChengBiao.getKes());
        add(待学);

        已学.setToolTipText("已学课程");
        已学.setBackground(LEAVE);
        handle(已学, MaShiBingKeChengBiao.getFinishKES());
        add(已学);

        忽略.setToolTipText("忽略课程");
        忽略.setBackground(LEAVE);
        handle(忽略, MaShiBingKeChengBiao.getIgnoreKES());
        add(忽略);

        近期.setToolTipText("近期课程安排");
        近期.setBackground(LEAVE);
        add(近期);

        刷新.setAction(new AbstractAction("刷新") {
            {
                putValue(Action.SHORT_DESCRIPTION, "刷新近期课程安排");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                刷新近期();
            }
        });
        刷新.setBackground(LEAVE);
        add(刷新);
    }

    private void 刷新近期() {
        近期.removeAll();
        List<Node> nodes = MaShiBingKeChengBiao.showNext();
        for (Node node : nodes) {
            JMenuItem item = new JMenuItem(node.time + "      " + node.name);
            item.setToolTipText(node.ke.name);
            近期.add(item);
        }
        repaint();
    }
}
