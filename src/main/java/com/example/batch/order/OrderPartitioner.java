package com.example.batch.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单数据分区器
 *
 * <p>查询总数和 MIN/MAX ID，按行数均分后定位每个分区的 ID 边界。
 * 每个分区拿到 minId/maxId，独立读写，实现真正的并行。</p>
 *
 * @author claude
 */
public class OrderPartitioner implements Partitioner {

    private final OrderInfoMapper orderInfoMapper;

    public OrderPartitioner(OrderInfoMapper orderInfoMapper) {
        this.orderInfoMapper = orderInfoMapper;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        long totalCount = orderInfoMapper.selectCount(null);
        int partitionSize = (int) Math.ceil((double) totalCount / gridSize);

        Map<String, ExecutionContext> partitions = new HashMap<>(gridSize);
        long lastMaxId = 0;

        for (int i = 0; i < gridSize; i++) {
            // 从上一分区的 maxId 开始，取 partitionSize 条，最后一条的 ID 就是本分区的 maxId
            LambdaQueryWrapper<OrderInfoEntity> wrapper = new LambdaQueryWrapper<OrderInfoEntity>()
                    .select(OrderInfoEntity::getId)
                    .gt(OrderInfoEntity::getId, lastMaxId)
                    .orderByAsc(OrderInfoEntity::getId)
                    .last("LIMIT " + partitionSize);

            java.util.List<OrderInfoEntity> list = orderInfoMapper.selectList(wrapper);
            if (list == null || list.isEmpty()) {
                break;
            }

            long minId = lastMaxId;
            long maxId = list.get(list.size() - 1).getId();

            ExecutionContext context = new ExecutionContext();
            context.putLong("minId", minId);
            context.putLong("maxId", maxId);
            context.putInt("partitionIndex", i);
            partitions.put("partition" + i, context);

            System.out.println("分区" + i + "：ID 范围 (" + minId + ", " + maxId + "]，约 " + list.size() + " 条");
            lastMaxId = maxId;
        }
        return partitions;
    }
}
