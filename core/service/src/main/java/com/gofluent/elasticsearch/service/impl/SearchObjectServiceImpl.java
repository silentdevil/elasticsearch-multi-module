package com.gofluent.elasticsearch.service.impl;

import com.gofluent.elasticsearch.model.SearchObject;
import com.gofluent.elasticsearch.repository.SearchObjectMongoRepository;
import com.gofluent.elasticsearch.repository.SearchObjectRepository;
import com.gofluent.elasticsearch.service.SearchObjectService;
import com.google.gson.Gson;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchObjectServiceImpl implements SearchObjectService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SearchObjectRepository searchObjectRepository;

    @Autowired
    private SearchObjectMongoRepository searchObjectMongoRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    Gson gson;

    @Override
    public Page<SearchObject> findByKeyword(String keyword, Pageable pageable) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(buildQueryWithKeyword(keyword.replace("@"," ")))
                .build();
        return elasticsearchTemplate.queryForPage(searchQuery, SearchObject.class);
    }

    @Override
    public Page<SearchObject> findByKeywordAndType(String keyword, String type, Pageable pageable) {
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(buildQueryWithKeywordAndType(keyword.replace("@"," "), type))
                .build();
        return elasticsearchTemplate.queryForPage(build, SearchObject.class);
    }

    @Override
    public List<SearchObject> getAll() {
        List<SearchObject> searchObjects = new ArrayList<>();
        searchObjectRepository.findAll().forEach(searchObjects::add);
        return searchObjects;
    }

    @Override
    @KafkaListener(topics="${elasticsearch.topic}")
    public void syncDataFromMongoDB(String data) {
        System.out.println(data);
        Map<String, String> map = gson.fromJson(data, Map.class);
        SearchObject searchObject = new SearchObject();
        searchObject.setId(map.get("id"));
        searchObject.setDisplay(map.get("display"));
        searchObject.setKeyword(map.get("keyword"));
        searchObject.setReturnId(map.get("returnid"));
        searchObject.setType(map.get("type"));
        searchObjectRepository.save(searchObject);
    }

    @Override
    public void syncAllFromMongoDB() {
        List<SearchObject> searchObjects = mongoTemplate.findAll(SearchObject.class);
        searchObjectRepository.save(searchObjects);
    }

    @Override
    public void migrateToDB() {
        searchObjectMongoRepository.save(searchObjectRepository.findAll());
    }

    private QueryBuilder buildQueryWithKeyword(String keyword) {
         BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.queryStringQuery(keyword)
                                                .fuzziness(Fuzziness.TWO)
                                                .lenient(true)
                                                .field("keyword"));
        for(String key: keyword.split(" ")) {
            boolQueryBuilder.must(QueryBuilders.queryStringQuery("*" + key + "*")
                    .lenient(true)
                    .fuzziness(Fuzziness.TWO)
                    .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                    .field("keyword"));
        }
        return boolQueryBuilder;
    }

    private QueryBuilder buildQueryWithKeywordAndType(String keyword, String type) {
        return QueryBuilders.boolQuery().should(QueryBuilders.queryStringQuery(keyword)
                                                .fuzziness(Fuzziness.ONE)
                                                .lenient(true)
                                                .field("keyword"))
                                        .must(QueryBuilders.queryStringQuery(type)
                                                                        .fuzziness(Fuzziness.ONE)
                                                                        .lenient(true)
                                                                        .field("type"))
                .must(QueryBuilders.queryStringQuery("*" + keyword + "*")
                        .lenient(true)
                        .fuzziness(Fuzziness.ONE)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .field("keyword"));
    }
}
