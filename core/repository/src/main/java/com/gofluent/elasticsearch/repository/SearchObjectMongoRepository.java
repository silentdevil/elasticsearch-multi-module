package com.gofluent.elasticsearch.repository;

import com.gofluent.elasticsearch.model.SearchObject;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SearchObjectMongoRepository extends MongoRepository<SearchObject, String> {
}
