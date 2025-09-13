package org.tonyq.ciloadanalyzer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * CodeIgniter Load 分析器
 * 
 * 將之前的 Node.js 邏輯移植到 Java
 */
public class CodeIgniterLoadAnalyzer {
    
    private static final String[] LOAD_TYPES = {"library", "model", "helper", "config", "database"};
    
    /**
     * 分析內容中的 $this->load 調用
     */
    public List<LoadInfo> analyzeContent(String content) {
        Map<String, LoadInfo> loads = new LinkedHashMap<>();
        
        // 解析各種 $this->load 調用
        for (String type : LOAD_TYPES) {
            Pattern pattern = Pattern.compile(
                "\\$this->load->" + type + "\\(['\"]([^'\"]+)['\"]\\);?",
                Pattern.MULTILINE
            );
            
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                String loadPath = matcher.group(1);
                String propertyName = getPropertyName(type, loadPath);
                
                if (propertyName != null) {
                    String key = propertyName + "_" + type;
                    loads.put(key, new LoadInfo(
                        type,
                        loadPath,
                        propertyName,
                        getClassName(type, loadPath)
                    ));
                }
            }
        }
        
        // 特殊處理 database 加載
        if (content.contains("$this->load->database()")) {
            loads.put("db_database", new LoadInfo(
                "database",
                "",
                "db",
                "CI_DB_query_builder"
            ));
        }
        
        return new ArrayList<>(loads.values());
    }
    
    /**
     * 獲取屬性名稱
     */
    private String getPropertyName(String type, String loadPath) {
        String[] libParts;
        String libClassName;
        switch (type) {
            case "library":
                // personal_overtime_application/Overtime_application_component -> overtime_application_component
                libParts = loadPath.split("/");
                libClassName = libParts[libParts.length - 1];
                return camelCaseToSnakeCase(libClassName).toLowerCase();
            
            case "model":
                // ModelName_model -> modelname_model 或 ModelName -> modelname
                libParts = loadPath.split("/");
                libClassName = libParts[libParts.length - 1];
                return camelCaseToSnakeCase(libClassName).toLowerCase();
                
            case "database":
                return "db";
            
            default:
                return null; // helper 和 config 不生成屬性
        }
    }
    
    /**
     * 獲取類別名稱
     */
    private String getClassName(String type, String loadPath) {
        switch (type) {
            case "library":
                String[] libParts = loadPath.split("/");
                return libParts[libParts.length - 1];
            
            case "model":
                // 如果沒有 _model 後綴，自動添加
                String modelName = loadPath.endsWith("_model") ? 
                    loadPath : 
                    loadPath + "_model";
                return snakeCaseToPascalCase(modelName);
            
            case "database":
                return "CI_DB_query_builder";
            
            default:
                return loadPath;
        }
    }
    
    /**
     * 駝峰轉蛇形
     */
    private String camelCaseToSnakeCase(String str) {
        return str.replaceAll("([A-Z])", "_$1")
                  .replaceFirst("^_", "")
                  .toLowerCase();
    }
    
    /**
     * 蛇形轉帕斯卡
     */
    private String snakeCaseToPascalCase(String str) {
        String[] words = str.split("_");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                result.append("_");
            }
        }
        
        // 移除最後的底線
        if (result.length() > 0 && result.charAt(result.length() - 1) == '_') {
            result.setLength(result.length() - 1);
        }
        
        return result.toString();
    }
    
    /**
     * 生成 PHPDoc 屬性
     */
    public String generatePhpDocProperties(List<LoadInfo> loads) {
        return loads.stream()
            .filter(load -> load.getPropertyName() != null)
            .map(load -> String.format(" * @property %s $%s", 
                load.getClassName(), 
                load.getPropertyName()))
            .sorted()
            .collect(Collectors.joining("\n"));
    }
    
    /**
     * 將 PHPDoc 加入到內容中
     */
    public String addPhpDocToContent(String content, String properties) {
        // 查找現有的 PHPDoc 註解
        Pattern classPattern = Pattern.compile(
            "((\\/\\*\\*[\\s\\S]*?\\*\\/\\s*)?class\\s+(\\w+))",
            Pattern.MULTILINE
        );
        
        Matcher matcher = classPattern.matcher(content);
        if (!matcher.find()) {
            throw new RuntimeException("無法找到類別定義");
        }
        
        String existingDoc = matcher.group(2);
        String className = matcher.group(3);
        
        String newDoc;
        if (existingDoc != null && !existingDoc.trim().isEmpty()) {
            // 更新現有的 PHPDoc
            String docContent = existingDoc.trim();
            String[] lines = docContent.split("\n");
            
            // 移除舊的 @property 行
            List<String> filteredLines = Arrays.stream(lines)
                .filter(line -> !line.contains("@property"))
                .collect(Collectors.toList());
            
            // 在最後一行 */ 之前插入新的 @property
            int lastIndex = filteredLines.size() - 1;
            if (lastIndex >= 0 && filteredLines.get(lastIndex).contains("*/")) {
                filteredLines.add(lastIndex, properties);
            }
            
            newDoc = String.join("\n", filteredLines);
        } else {
            // 創建新的 PHPDoc
            newDoc = String.format("/**\n%s\n */", properties);
        }
        
        // 替換內容
        String replacement = String.format("%s\nclass %s", newDoc, className);
        return content.replaceFirst(
            "((\\/\\*\\*[\\s\\S]*?\\*\\/\\s*)?class\\s+(\\w+))",
            Matcher.quoteReplacement(replacement)
        );
    }
}