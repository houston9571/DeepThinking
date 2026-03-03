//package com.optimus.mongo.repository;
//
//import com.optimus.base.PageInfo;
//import com.optimus.base.PageResult;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.repository.Aggregation;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.Query;
//import org.springframework.data.mongodb.repository.Update;
//
//import java.util.List;
//
//public interface GameTransferOrderRepository extends MongoRepository<GameTransferOrder, Long> {
//
//    String CACHE_KEY = "GameTransferOrder:";
//
//    @Query("{}")
//    @Update("{ '$set': { 'aggregateState': 0 } }")
//    long resetAggregateState();
//
//    @Query("{ '_id' : ?0 }")
//    @Update("{ '$set' : { 'aggregateState' : ?1 } }")
//    void updateAggregateStateById(long id, int aggregateState);
//
//
//    Long countByAggregateStateAndStatus(int aggregateState, int status);
//
//    PageResult<GameTransferOrder> queryPage(PageInfo<GameTransferOrder> pageInfo, Class<GameTransferOrder> clazz);
//
//    PageResult<GameTransferOrder> queryPage(PageInfo<GameTransferOrder> pageInfo, Class<GameTransferOrder> clazz, Criteria criteria);
//
//
//    @Aggregation({
//            "{ $match:{'updateTime' : {'$gte' : { '$date' : ?0 }, '$lte' : { '$date' : ?1 } }, 'status' : 1, 'agentId' : ?2  } }",
//            "{ $group: { _id : { type : $type, playerId : $playerId }, amounts : {$sum : $amount} } } ",
//            "{ $project: { _id: 0, type : '$_id.type', counts : 1, amounts: $amounts } }",
//            "{ $group: { _id : { type : $type },  counts : {$sum : 1},  amounts : {$sum : $amounts} } } ",
//            "{ $project: { _id: 0, type : '$_id.type', counts : $counts, amounts: $amounts } }"
//    })
//    List<GameTransferOrder> groupTransferAmountAndCounts(String start, String end, int agentId);
//
//    @Aggregation({
//            "{ $match: {'updateTime' : {'$gte': { '$date': ?0 }, '$lte': { '$date': ?1 } }, 'status' : 1 } }",
//            "{ $group: { _id: { playerId: '$playerId', uid : '$uid', merchantId: '$merchantId', agentId: '$agentId', platform: '$platform', currency: '$currency' , type: '$type' }, amount : { $sum : '$amount' } } }",
//            "{ $project: { _id: 0, playerId: '$_id.playerId', uid: '$_id.uid', merchantId: '$_id.merchantId', agentId: '$_id.agentId', platform: '$_id.platform', currency: '$_id.currency', type: '$_id.type', amount : '$amount' } }",
//            "{ $sort: { 'playerId' : 1 }}"
//    })
//    List<GameTransferOrder> statisticUserTransfer(String start, String end);
//}
