package com.example.rules.service;

import com.fasterxml.jackson.databind.node.*;
import java.util.*;
import java.util.regex.*;

/**
 * Extremely simple parser for patterns like:
 *   department ids in (:deptSet) and employeeIds in (:empSet)
 * Supports AND/OR joins. Case-insensitive.
 */
public class RuleParser {
  private static final Pattern COND = Pattern.compile(
    "\\s*([a-zA-Z][a-zA-Z0-9_]*)\\s*(ids|id)?\\s+in\\s*\\(:\\s*([a-zA-Z][a-zA-Z0-9_]*)\\s*\\)\\s*",
    Pattern.CASE_INSENSITIVE);

  public ObjectNode parse(String appName, List<String> entities, String text) {
    String normalized = text.trim();
    // Split on ' and ' / ' or ' but keep the operators
    List<String> tokens = new ArrayList<>();
    Matcher m = Pattern.compile("\\s+(and|or)\\s+", Pattern.CASE_INSENSITIVE).matcher(normalized);
    int last = 0;
    while (m.find()) {
      tokens.add(normalized.substring(last, m.start()));
      tokens.add(m.group(1).toUpperCase());
      last = m.end();
    }
    tokens.add(normalized.substring(last));

    ArrayNode all = JsonNodeFactory.instance.arrayNode();
    ArrayNode any = JsonNodeFactory.instance.arrayNode();

    // Default: if only ANDs -> all; if any OR present -> group in any
    boolean hasOr = tokens.stream().anyMatch(t -> t.equals("OR"));

    for (String t : tokens) {
      if (t.equalsIgnoreCase("AND") || t.equalsIgnoreCase("OR")) continue;
      Matcher c = COND.matcher(t);
      if (!c.matches()) throw new IllegalArgumentException("Unsupported condition: " + t);
      String fieldRaw = c.group(1);
      String var = c.group(3);

      String field = normalizeField(fieldRaw);
      ObjectNode cond = JsonNodeFactory.instance.objectNode()
        .put("field", field)
        .put("op", "IN")
        .put("valueVar", var);
      if (hasOr) any.add(cond); else all.add(cond);
    }

    ObjectNode expr = JsonNodeFactory.instance.objectNode();
    if (hasOr) expr.set("any", any); else expr.set("all", all);

    ObjectNode root = JsonNodeFactory.instance.objectNode();
    root.put("app", appName);
    ArrayNode ents = JsonNodeFactory.instance.arrayNode();
    entities.forEach(ents::add);
    root.set("entities", ents);
    root.set("expr", expr);
    return root;
  }

  private String normalizeField(String raw){
    String s = raw.toLowerCase();
    if (s.contains("department")) return "departmentId";
    if (s.contains("employee")) return "employeeId";
    return s; // fallback
  }
}
