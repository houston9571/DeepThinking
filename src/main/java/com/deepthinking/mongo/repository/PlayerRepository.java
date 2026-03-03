//package com.optimus.mongo.repository;
//
//import com.alicp.jetcache.anno.CacheInvalidate;
//import com.alicp.jetcache.anno.CacheType;
//import com.alicp.jetcache.anno.Cached;
//import com.optimus.base.PageInfo;
//import com.optimus.base.PageResult;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.repository.*;
//
//import java.util.List;
//
//import static com.optimus.constant.Constants.THREE_MINUTES;
//
//public interface PlayerRepository extends MongoRepository<PlayerInfo, Integer> {
//
//    String CACHE_KEY = "PlayerInfo:";
//
//    @Query("{}")
//    @Update("{ '$set': { 'validBet': 0, 'win': 0, 'amountTransIn': 0, 'amountTransOut': 0 } }")
//    long resetStatistic();
//
//    @Aggregation({
//            "{ $group : { _id: null, maxId : { $max : '$_id' } } }",
//            "{ $project: { _id: 0 , id : '$maxId' }  } "
//    })
//    int findMaxId();
//
//    /**
//     * 仅仅验证session使用，不需要更新缓存
//     */
//    @Cached(name = CACHE_KEY, key = "#playerId", cacheType = CacheType.BOTH, localExpire = THREE_MINUTES, expire = THREE_MINUTES)
//    @Query("{ '_id' : ?0 }")
//    PlayerInfo findByPlayerId(Integer playerId);
//
//    /**
//     * 只需要使用到代理id 商户id 状态等信息，只有启用禁用时删除缓存
//     */
//    @Cached(name = CACHE_KEY, key = "#agentId+':'+#uid", cacheType = CacheType.BOTH, localExpire = THREE_MINUTES, expire = THREE_MINUTES)
//    PlayerInfo findByUidAndAgentId(String uid, int agentId);
//
//    @CacheInvalidate(name = CACHE_KEY, key = "#playerId")
//    @CacheInvalidate(name = CACHE_KEY, key = "#agentId+':'+#uid")
//    @Query("{ '_id' : ?0 }")
//    @Update("{ '$set' : { 'status' : ?1 } }")
//    Integer updateStatusById(int playerId, int status, int agentId, String uid);
//
//
//    @CacheInvalidate(name = CACHE_KEY, key = "#playerId")
//    @CacheInvalidate(name = CACHE_KEY, key = "#agentId+':'+#uid")
//    @Query("{ '_id' : ?0 }")
//    @Update("{ '$set' : { 'platforms' : ?1 } }")
//    void updatePlatformsById(int playerId, String platforms, int agentId, String uid);
//
//    List<PlayerInfo> queryList(PlayerInfo entity, Class<PlayerInfo> clazz, Criteria criteria);
//
//    @CountQuery(value = "{'lastEnterTime' : {'$gte' : { '$date' : ?0 }, '$lte' : { '$date' : ?1 } } , 'agentId' : ?2 }")
//    Integer countActivesByAgentId(String start, String end, Integer agentId);
//
//    @CountQuery(value = "{'lastEnterTime' : {'$gte' : { '$date' : ?0 }, '$lte' : { '$date' : ?1 } } , 'createTime' : {'$gte' : { '$date' : ?0 }, '$lte' : { '$date' : ?1 } } , 'agentId' : ?2 }")
//    Integer countActivesNewByAgentId(String start, String end, Integer agentId);
//
//
//    PageResult<PlayerInfo> queryPage(PageInfo<PlayerInfo> pageInfo, Class<PlayerInfo> clazz);
//
//
//}