//package com.deepthinking.ext.config;
//import com.deepthinking.ext.base.PagingMongoRepository;
//import org.bson.types.Decimal128;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.data.convert.ReadingConverter;
//import org.springframework.data.convert.WritingConverter;
//import org.springframework.data.mongodb.MongoDatabaseFactory;
//import org.springframework.data.mongodb.config.EnableMongoAuditing;
//import org.springframework.data.mongodb.core.convert.*;
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//import org.springframework.lang.NonNull;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//
//@Configuration
//@EnableMongoAuditing
//@EnableMongoRepositories(basePackages = "com.optimus.mongo.repository", repositoryBaseClass = PagingMongoRepository.class)
//public class MongoConfig {
//
//
//
//
//    @Bean
//    @ConditionalOnMissingBean(MongoConverter.class)
//    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context, MongoCustomConversions conversions) {
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
//        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);
//        converter.setTypeMapper(new DefaultMongoTypeMapper(null));//remove spring data generate column _class
//        converter.setCustomConversions(conversions);
//        return converter;
//    }
//
//    @Bean
//    public MongoCustomConversions mongoCustomConversions() {
//        return new MongoCustomConversions(Arrays.asList(
//                new BigDecimalDecimal128Converter(),
//                new Decimal128BigDecimalConverter()
//        ));
//
//    }
//
//    /**
//     * BigDecimal转换成Decimal128存储
//     */
//    @WritingConverter
//    private static class BigDecimalDecimal128Converter implements Converter<BigDecimal, Decimal128> {
//        @Override
//        public Decimal128 convert(@NonNull BigDecimal source) {
//            return new Decimal128(source);
//        }
//    }
//
//    @ReadingConverter
//    private static class Decimal128BigDecimalConverter implements Converter<Decimal128, BigDecimal> {
//        @Override
//        public BigDecimal convert(@NonNull Decimal128 source) {
//            return source.bigDecimalValue();
//        }
//
//    }
//
//}
