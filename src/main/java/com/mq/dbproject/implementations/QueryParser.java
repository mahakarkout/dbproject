package com.mq.dbproject.implementations;

import  com.mq.dbproject.interfaces.IQueryParser;

// import java.util.HashMap;
// import java.util.Map;

public class QueryParser implements IQueryParser {
    @Override
    public String[] parse(String query) {
        return query.split(" ");
    }
}
