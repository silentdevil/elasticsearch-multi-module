package com.gofluent.elasticsearch.load;

import com.gofluent.elasticsearch.model.SearchObject;
import com.gofluent.elasticsearch.service.SearchObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;

@Component
public class Loaders {

    @Autowired
    ElasticsearchOperations operations;

    @Autowired
    SearchObjectService searchObjectService;

    @PostConstruct
    @Transactional
    public void loadAll(){
        operations.putMapping(SearchObject.class);
        searchObjectService.syncAllFromMongoDB();
    }
}
