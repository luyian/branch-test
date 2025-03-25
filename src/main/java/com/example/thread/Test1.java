package com.example.thread;

import cn.hutool.core.date.DateUtil;
import com.example.demo.BaseUser;
import com.example.demo.User;
import com.example.enmu.AccountState;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class Test1 {

    public static int duckNum = 100;

    public static void main(String[] args) {
        // 生产者
        Thread product = new Thread(() -> {
            while (duckNum < 120) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("生产者生产第" + (++duckNum) + "只烤鸭");
            }
        });

        // 消费者
        Thread consumer = new Thread(() -> {
            while (duckNum > 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("消费者消费第" + (--duckNum) + "只烤鸭");
                System.out.println("消费者消费第" + (--duckNum) + "只烤鸭");
            }
        });

        consumer.start();
        product.start();

    }

    @Test
    public void test01() {
        String str = " ";

        System.out.println(str.substring(str.length() - 1));
    }

    @Test
    public void test03() {
        System.out.println("test branch 3-1");
    }

    @Test
    public void test04() {
        String productno = "1212";
        String initSql = "" +
                // 初始化prdt_trafficlight，设置触发进件的电站节点
                "insert into prdt_trafficlight " +
                "select null,productid,40,0,1,null,now(),1,null,null from prdt_product where productno = '"+productno+"'; " +
                "insert into prdt_trafficlight " +
                "select null,productid,100,0,1,null,now(),1,null,null from prdt_product where productno = '"+productno+"'; " +
                "insert into prdt_trafficlight " +
                "select null,productid,500,0,1,null,now(),1,null,null from prdt_product where productno = '"+productno+"'; " +
                "insert into prdt_trafficlight " +
                "select null,productid,600,0,1,null,now(),1,null,null from prdt_product where productno = '"+productno+"';" +
                // 初始化prdt_trafficlightlinkserv
                "insert into prdt_trafficlightlinkserv " +
                "select null,t.trafficlightid,t1.tlserviceid,t1.switchtype,now(),1 " +
                "from prdt_trafficlight t " +
                "inner join prdt_product pr on t.productid = pr.productid " +
                "inner join base_orginfoextend oe on pr.operateleaseorgid = oe.orgid " +
                "inner join prdt_trafficlightservice t1 on t.stationnode = t1.stationnode " +
                "where pr.productno = '"+productno+"';";
        System.out.println(initSql);
    }

    @Test
    public void test05() {
        Integer.parseInt("1");
    }

    @Test
    public void test06() throws JSONException {
//        org.json.JSONObject res = new JSONObject();
////        res.put("result", new JSONObject().put("aaa", "bbb"));
//
//        org.json.JSONObject result = res.optJSONObject("result");
//        System.out.println(result);

        BaseUser baseUser = new BaseUser();
        baseUser.setLinkman("sun");
        System.out.println(baseUser.equals(new BaseUser()));

    }


    @Test
    public void test07() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("zhangsan" +i);
        }

        String s = list.stream().filter(x -> x.equals("zhangsan19")).distinct().findFirst().orElse("xxxx");
        System.out.println(s);

        list.removeIf(x->x.equals("zhangsan1"));
        list.forEach(System.out::println);
        System.out.println(Boolean.TRUE.equals(null));
        Boolean.TRUE.equals(null);
    }

    @Test
    public void test08() {
        List<String> list = new ArrayList<>();
        Map<String, String> collect = list.stream().collect(Collectors.toMap(item -> item, Function.identity(), (v1, v2) -> v1));
        System.out.println(collect.get(""));
    }


    @Test
    public void test09() {
        int v1c = 222;
        int v2c = 502;
        int pz = 200;

        int toc = v1c + v2c;
        int tp = toc/pz + 1;
        for (int i = 1; i <= tp; i++) {
            int ps1 = 0;
            int tmp = v1c - i*pz;
            int psz = 0;
            int off = 0;

            if (tmp >= 0) {
                psz = 0;
            } else if (tmp < 0 && tmp >= -pz) {
                psz = -tmp;
            } else if (tmp < -pz) {
                off = -tmp - pz;
                psz = pz;
            }
            System.out.println("limit " + psz + " offset " + off);

        }


    }

    @Test
    public void test10() {
        List<String> list = new ArrayList<>();
        list.add(null);
        list.add("账单");
        String collect = list.stream().filter(Objects::nonNull).collect(Collectors.joining(","));
        System.out.println(collect);

    }

    @Test
    public void test11() {
        Integer i = null;
        System.out.println(Boolean.TRUE.equals(null));
    }



    @Test
    public void test12() {
        // 创建单据列表
        List<String> documents = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            documents.add("Alice " + i);
        }
        for (int i = 1; i <= 600; i++) {
            documents.add("Bob " + i);
        }
        for (int i = 1; i <= 400; i++) {
            documents.add("Charlie " + i);
        }

        // 创建审核人员列表
        List<String> reviewers = new ArrayList<>();
        reviewers.add("Alice");
        reviewers.add("Bob");
        reviewers.add("Charlie");
        reviewers.add("David");
        reviewers.add("Eve");


        Map<String, List<String>> assignedDocuments = new HashMap<>();
        // 初始化每个审核人员的单据列表
        for (String reviewer : reviewers) {
            assignedDocuments.put(reviewer, new ArrayList<>());
        }

        // 打乱单据列表
        Collections.shuffle(documents);

        // 最大值数量
        int maxCount = documents.size() / reviewers.size() + 1;

        // 分配单据
        int reviewerIndex = 0;
        for (String document : documents) {
            List<String> precedenceReviewers = getPrecedenceReviewers(assignedDocuments);
            String currentReviewer = precedenceReviewers.get(0);
            String nextReviewer = precedenceReviewers.get(1);

            String addReviewer = currentReviewer;
            // 确保当前单据不分配给当前审核人员
            if (document.startsWith(currentReviewer)) {
                addReviewer = nextReviewer;
            }
            assignedDocuments.get(addReviewer).add(document);
            reviewerIndex++;
        }

        // 打印结果
        for (Map.Entry<String, List<String>> entry : assignedDocuments.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().size());
        }

        System.out.println(getPrecedenceReviewers(assignedDocuments));
    }

    /**
     * 按已分配数量确定优先级
     * @param assignedDocuments
     * @return
     */
    public List<String> getPrecedenceReviewers(Map<String, List<String>> assignedDocuments) {
        Map<String, Integer> reviewerCounts = new HashMap<>();
        List<SortHelper> list = new ArrayList<>();
        assignedDocuments.forEach((reviewer, documents) -> {
            list.add(new SortHelper(reviewer, documents));
        });
        Collections.sort(list);
        return list.stream().limit(2).map(SortHelper::getName).collect(Collectors.toList());
    }

    class SortHelper implements Comparable<SortHelper>{
        private String name;
        private List<String> assignedDocuments;

        public SortHelper(String name, List<String> assignedDocuments) {
            this.name = name;
            this.assignedDocuments = assignedDocuments;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getAssignedDocuments() {
            return assignedDocuments;
        }

        public void setAssignedDocuments(List<String> assignedDocuments) {
            this.assignedDocuments = assignedDocuments;
        }

        public int getSize() {
            if (assignedDocuments != null) {
                return assignedDocuments.size();
            }
            return 0;
        }

        @Override
        public int compareTo(SortHelper o) {
            return this.getSize() - o.getSize();
        }
    }

    @Test
    public void test13() {
        List<String> strings = new ArrayList<>();
        Map<String, String> collect = strings.stream().collect(Collectors.toMap(item -> item, item -> item));
        System.out.println(collect.get("1"));
        Map<String, Long> collect1 = strings.stream().collect(Collectors.groupingBy(item -> item, Collectors.counting()));
        System.out.println(collect1);

    }

    /**
     * 测试流去重
     */
    @Test
    public void test14() {
        List<User> list = new ArrayList<>();
        list.add(new User("张三", 10));
        list.add(new User("李四", 10));
        list.add(new User("张三", 11));
        list.add(new User("张三1", 11));

        list = list.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(p -> p.getName(), p -> p, (p1, p2) -> p1),
                        map -> new ArrayList<>(map.values())
                ));
        list.forEach(item -> System.out.println(item.getName() + "---" + item.getAge()));
    }

    @Test
    public void test15() {
        AccountState[] values = AccountState.values();
        System.out.println(Arrays.toString(values));

        List<HashMap<String, Object>> list = Arrays.asList(values).stream().map(item -> {
            HashMap<String, Object> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("value", item.getValue());
            objectObjectHashMap.put("desc", item.getDesc());
            return objectObjectHashMap;
        }).collect(Collectors.toList());
        list.forEach(System.out::println);
    }


    @Test
    public void test16() throws Exception {
        int compare = DateUtil.compare(DateUtil.parse("2024-11-29", "yyyy-MM-dd"), new Date(), "yyyy-MM-dd");
        System.out.println(compare);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        Date date2 = simpleDateFormat.parse("2023-12-24");
        Date date3 = simpleDateFormat.parse("2024-12-24");

        System.out.println(date1.compareTo(date2));
        System.out.println(date1.compareTo(date3));

    }


    @Test
    public void test17() {
        List<Integer> list = Arrays.asList(11, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        list = list.stream().sorted(Comparator.comparingInt(Integer::intValue).reversed()).collect(Collectors.toList());
        for (Integer integer : list) {
            System.out.println(integer);
            if (integer == 8) {
                break;
            }
        }
        System.out.println(list);

        System.out.println(list.stream().filter(item -> item == 12).collect(Collectors.toList()));
    }

    @Test
    public void test18() {
        List<User> list = new ArrayList<>();
//        list.add(new User("张三", 7));
//        list.add(new User("李四", 10));
//        list.add(new User("张三", 11));
        list.add(new User("张三1", null));

        int i = list.stream().map(User::getAge).filter(Objects::nonNull).reduce(0, Integer::sum).intValue();
//        int i = list.stream().map(User::getAge).reduce(0, Integer::sum).intValue();

        String collect = list.stream().filter(item -> Objects.nonNull(item.getAge())).map(x -> "" + x.getAge()).collect(Collectors.joining(","));
        System.out.println(collect);
    }

    @Test
    public void test19() {
        System.out.println(Integer.parseInt("-1"));
    }

}
