package com.gofluent.elasticsearch.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "searchobject", type = "searchobject")
public class SearchObject {

    @Id
    private String id;
    private String type;
    private String keyword;
    private String display;
    private String returnId;

    public SearchObject() {}

    public SearchObject(String id, String type, String keyword, String display, String returnId) {
        this.id = id;
        this.type = type;
        this.keyword = keyword;
        this.display = display;
        this.returnId = returnId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }
}
