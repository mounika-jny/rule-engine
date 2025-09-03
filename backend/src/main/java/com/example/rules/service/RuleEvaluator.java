package com.example.rules.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.*;

public class RuleEvaluator {
  public boolean evaluate(JsonNode compiled, Map<String,Object> variables, Map<String,Object> record){
    JsonNode expr = compiled.get("expr");
    if (expr.has("all")) return evalAll((ArrayNode) expr.get("all"), variables, record);
    if (expr.has("any")) return evalAny((ArrayNode) expr.get("any"), variables, record);
    return false;
  }

  private boolean evalAll(ArrayNode arr, Map<String,Object> vars, Map<String,Object> rec){
    for (JsonNode c : arr) if (!evalCond(c, vars, rec)) return false; return true;
  }
  private boolean evalAny(ArrayNode arr, Map<String,Object> vars, Map<String,Object> rec){
    for (JsonNode c : arr) if (evalCond(c, vars, rec)) return true; return false;
  }

  private boolean evalCond(JsonNode c, Map<String,Object> vars, Map<String,Object> rec){
    String field = c.get("field").asText();
    String op = c.get("op").asText();
    String var = c.get("valueVar").asText();

    Object recVal = rec.get(field);
    Object varVal = vars.get(var);
    if (recVal == null || varVal == null) return false;

    if ("IN".equalsIgnoreCase(op)){
      if (varVal instanceof Collection<?> col){
        return col.stream().anyMatch(v -> Objects.equals(stringify(v), stringify(recVal)));
      }
      if (varVal.getClass().isArray()){
        int len = java.lang.reflect.Array.getLength(varVal);
        for (int i=0;i<len;i++) if (Objects.equals(stringify(java.lang.reflect.Array.get(varVal,i)), stringify(recVal))) return true;
        return false;
      }
    }
    return false;
  }

  private String stringify(Object o){ return String.valueOf(o); }
}
