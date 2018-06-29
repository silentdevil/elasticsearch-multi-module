package com.gofluent.elasticsearch.service;

import com.gofluent.elasticsearch.model.SearchObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SearchObjectService {
    Page<SearchObject> findByKeyword(String keyword, Pageable pageable);
    Page<SearchObject> findByKeywordAndType(String keyword, String type, Pageable pageable);
    List<SearchObject> getAll();
    void syncAllFromMongoDB();
    void migrateToDB();
    void save(SearchObject searchObject);
}
