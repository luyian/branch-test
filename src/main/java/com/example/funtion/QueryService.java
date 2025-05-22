package com.example.funtion;

import cn.hutool.json.JSONUtil;
import org.apache.poi.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryService {

    public List<OrderInfo> queryOrder(OrderInfo search) {
        // 查询订单, 生成10条测试数据
        List<OrderInfo> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            OrderInfo info = new OrderInfo();
            info.setOrderId(i);
            info.setStationNo(String.valueOf(1000 + i));
            info.setCreateUser("张三" );
            info.setCreateTime(new Date());
            list.add(info);
        }

        if (StringUtil.isNotBlank(search.getCreateUser())) {
            list.forEach(item -> item.setCreateUser(search.getCreateUser()));
        }

        return list;
    }

    public List<BomInfo> queryBom(BomInfo search) {
        // 模拟查询BOM, 10条数据
        List<BomInfo> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BomInfo info = new BomInfo();
            info.setId(i);
            info.setMaterialCode(String.valueOf(1000 + i));
            info.setMaterialName("物料" + i);
            info.setMaterialGroup("发电机");
            list.add(info);
        }
        return list;
    }

    public <T extends BaseSearch> void printPageData(QueryFactory<T> queryFactory, T search) {
        List<? extends BaseSearch> baseSearches = queryFactory.queryPage(search);
        String jsonStr = JSONUtil.toJsonStr(baseSearches);
        System.out.println(jsonStr);
    }


    public static void main(String[] args) {
        QueryService queryService = new QueryService();
        OrderInfo search = new OrderInfo();
        search.setPageNum(1);
        search.setPageSize(10);
        search.setCreateUser("张四");
        queryService.printPageData(queryService::queryOrder, search);


        BomInfo search2 = new BomInfo();
        search2.setPageNum(1);
        search2.setPageSize(10);
        queryService.printPageData(queryService::queryBom, search2);
    }

}
