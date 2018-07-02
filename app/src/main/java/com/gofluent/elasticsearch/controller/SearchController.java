package com.gofluent.elasticsearch.controller;

import com.gofluent.elasticsearch.model.SearchObject;
import com.gofluent.elasticsearch.service.SearchObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/")
public class SearchController {

    @Autowired
    private SearchObjectService searchObjectService;

    @GetMapping(value = "/search/{keyword}/{start}/{end}")
    public List<SearchObject> getByKeyword(@PathVariable final String keyword, @PathVariable int start,
                                           @PathVariable int end) throws UnsupportedEncodingException {
        return searchObjectService.findByKeyword( URLDecoder.decode(keyword,"UTF-8"), new PageRequest(start, end)).getContent();
    }

    @GetMapping(value = "/search/{keyword}")
    public List<SearchObject> findAllByKeyword(@PathVariable final String keyword,
                                               Pageable pageable) throws UnsupportedEncodingException {
        return searchObjectService.findByKeyword( URLDecoder.decode(keyword,"UTF-8"), pageable).getContent();
    }

    @GetMapping(value = "/searchtype/{type}/{keyword}")
    public List<SearchObject> findByKeywordAndType(@PathVariable final String type, @PathVariable final String keyword,
                                                   Pageable pageable) throws UnsupportedEncodingException {
        return searchObjectService.findByKeywordAndType( URLDecoder.decode(keyword,"UTF-8"), type, pageable).getContent();
    }

    @GetMapping(value = "/searchtype/{type}/{keyword}/{start}/{end}")
    public List<SearchObject> findByKeywordAndType(@PathVariable final String type, @PathVariable final String keyword,
                                           @PathVariable int start, @PathVariable int end) throws UnsupportedEncodingException {
        return searchObjectService.findByKeywordAndType( URLDecoder.decode(keyword,"UTF-8"), type, new PageRequest(start, end)).getContent();
    }

    @RequestMapping(value = "/search/all")
    public List<SearchObject> findAll() throws UnsupportedEncodingException {
        return searchObjectService.getAll();
    }

    @RequestMapping(value = "/migratetodb")
    public ResponseEntity<String> migrateAllToDB() throws UnsupportedEncodingException {
        searchObjectService.migrateToDB();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/syncallfromdb")
    public ResponseEntity<String> syncAllFromDB() {
        searchObjectService.syncAllFromMongoDB();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
