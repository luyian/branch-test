package com.example.batch.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.batch.item.ItemReader;

import java.util.Iterator;
import java.util.List;

/**
 * 订单数据读取器（基于 ID 范围的游标分页）
 *
 * <p>每个分区有独立的 Reader 实例，只读取自己 ID 范围内的数据，
 * 分区之间互不干扰，无需加锁，实现真正的并行读取。</p>
 *
 * @author claude
 */
public class OrderItemReader implements ItemReader<OrderExcelModel> {

    private final OrderInfoMapper orderInfoMapper;
    private final int pageSize;
    private final long minId;
    private final long maxId;
    private long lastId;
    private Iterator<OrderExcelModel> currentIterator;

    public OrderItemReader(OrderInfoMapper orderInfoMapper, int pageSize, long minId, long maxId) {
        this.orderInfoMapper = orderInfoMapper;
        this.pageSize = pageSize;
        this.minId = minId;
        this.maxId = maxId;
        this.lastId = minId;
    }

    @Override
    public OrderExcelModel read() {
        if (currentIterator != null && currentIterator.hasNext()) {
            return currentIterator.next();
        }

        List<OrderInfoEntity> records = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfoEntity>()
                        .gt(OrderInfoEntity::getId, lastId)
                        .le(OrderInfoEntity::getId, maxId)
                        .orderByAsc(OrderInfoEntity::getId)
                        .last("LIMIT " + pageSize)
        );

        if (records == null || records.isEmpty()) {
            return null;
        }

        lastId = records.get(records.size() - 1).getId();
        currentIterator = records.stream().map(this::toExcelModel).iterator();
        return currentIterator.hasNext() ? currentIterator.next() : null;
    }

    private OrderExcelModel toExcelModel(OrderInfoEntity entity) {
        OrderExcelModel model = new OrderExcelModel();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setCusName(entity.getCusName());
        model.setAddress(entity.getAddress());
        model.setDetail(entity.getDetail());
        model.setPhone(entity.getPhone());
        model.setCreateTime(entity.getCreateTime());
        model.setUpdateTime(entity.getUpdateTime());
        return model;
    }
}
