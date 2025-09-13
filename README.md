# CodeIgniter Load Analyzer Plugin

這是一個 JetBrains IDE 插件，用於自動分析 CodeIgniter 檔案中的 `$this->load` 調用，並生成對應的 PHPDoc `@property` 註解。

## 功能特色

- 🚀 **一鍵生成** - 右鍵點擊即可自動生成 PHPDoc 屬性註解
- 🔍 **智能分析** - 自動偵測 `$this->load->library()`, `$this->load->model()`, `$this->load->database()` 調用
- 🎯 **精確映射** - 智能類別名稱映射，符合 CodeIgniter 慣例
- 🔄 **自動去重** - 避免重複的屬性定義
- ✨ **無縫整合** - 完美整合到 PhpStorm/IntelliJ IDEA 工作流程中

## 支援的 IDE

- IntelliJ IDEA Community/Ultimate 2023.1+
- PhpStorm 2023.1+
- WebStorm 2023.1+
- 其他基於 IntelliJ Platform 的 IDE

## 安裝方法

### 方法一：從源碼建置

1. **準備環境**
   ```bash
   # 確保已安裝 Java 11+ 和 Gradle
   java -version
   gradle -version
   ```

2. **編譯插件**
   ```bash
   cd ci-load-analyzer-plugin
   ./gradlew buildPlugin
   ```

3. **安裝插件**
   - 在 IDE 中：`File` → `Settings` → `Plugins` → `⚙️` → `Install Plugin from Disk...`
   - 選擇 `build/distributions/ci-load-analyzer-plugin-1.0.0.zip`

### 方法二：直接安裝 (開發測試用)

```bash
cd ci-load-analyzer-plugin
./gradlew runIde
```

這會啟動一個帶有插件的 IDE 實例，供測試使用。

## 使用方法

1. **開啟 PHP 檔案** - 確保檔案包含 CodeIgniter 的 `$this->load` 調用

2. **執行生成** - 選擇以下任一方式：
   - **右鍵選單**：在編輯器中右鍵 → `Generate CodeIgniter Properties`
   - **Code 選單**：`Code` → `Generate CodeIgniter Properties`  
   - **快捷鍵**：`Ctrl+Shift+G` (Windows/Linux) 或 `Cmd+Shift+G` (Mac)

3. **查看結果** - 插件會自動：
   - 分析檔案中的所有 `$this->load` 調用
   - 生成對應的 `@property` 註解
   - 更新或添加到類別的 PHPDoc 註解中
   - 顯示處理結果摘要

## 使用範例

### 輸入 (PHP 檔案)
```php
<?php
class MyController extends CI_Controller {
    public function index() {
        $this->load->library('personal_overtime_application/Overtime_application_component');
        $this->load->model('Overtime_record_model');
        $this->load->database();
    }
}
```

### 輸出 (自動生成)
```php
<?php
/**
 * @property CI_DB_query_builder $db
 * @property Overtime_Record_Model $overtime_record_model
 * @property Overtime_application_component $overtime_application_component
 */
class MyController extends CI_Controller {
    public function index() {
        $this->load->library('personal_overtime_application/Overtime_application_component');
        $this->load->model('Overtime_record_model');
        $this->load->database();
    }
}
```

## 命名規則

### Library 載入
```php
$this->load->library('personal_overtime_application/Overtime_application_component');
// 生成: @property Overtime_application_component $overtime_application_component
```

### Model 載入
```php
$this->load->model('Overtime_record_model');
// 生成: @property Overtime_Record_Model $overtime_record_model
```

### Database 載入
```php
$this->load->database();
// 生成: @property CI_DB_query_builder $db
```

## 開發說明

### 專案結構
```
ci-load-analyzer-plugin/
├── src/main/
│   ├── java/org/tonyq/ciloadanalyzer/
│   │   ├── GeneratePropertiesAction.java     # 主要 Action
│   │   ├── CodeIgniterLoadAnalyzer.java      # 核心分析邏輯  
│   │   └── LoadInfo.java                     # 資料結構
│   └── resources/META-INF/
│       └── plugin.xml                        # 插件配置
├── build.gradle                              # 建置配置
├── gradle.properties                         # Gradle 屬性
└── settings.gradle                           # 專案設定
```

### 開發指令

```bash
# 編譯插件
./gradlew buildPlugin

# 執行測試 IDE
./gradlew runIde

# 驗證插件
./gradlew verifyPlugin

# 清理建置檔案
./gradlew clean
```

### 除錯

1. **啟用除錯模式**
   ```bash
   ./gradlew runIde --debug-jvm
   ```

2. **在 IDE 中設定除錯點**
   - 連接到 `localhost:5005`

## 常見問題

### Q: 插件無法安裝？
A: 請確認：
- IDE 版本符合需求 (2023.1+)
- ZIP 檔案完整下載

### Q: 找不到 "Generate CodeIgniter Properties" 選項？
A: 請確認：
- 當前檔案是 PHP 檔案 (`.php` 副檔名)
- 插件已正確啟用

### Q: 生成的屬性名稱不正確？
A: 插件遵循以下規則：
- Library: 路徑最後部分，駝峰轉蛇形
- Model: 直接使用檔案名，添加 `_model` 後綴並轉換格式
- 如有特殊需求，可修改 `CodeIgniterLoadAnalyzer.java` 中的命名邏輯

## 技術細節

- **語言**: Java 11
- **框架**: IntelliJ Platform SDK
- **建置工具**: Gradle
- **相依性**: 無 (純 IntelliJ Platform)

## 貢獻

歡迎提交 Issue 和 Pull Request！

## 授權

MIT License

## 更新日誌

### v1.0.0 (2025-01-13)
- 初始版本發布
- 支援 Library, Model, Database 載入分析
- 自動生成 PHPDoc 屬性註解
- 智能類別名稱映射
- 自動去重複屬性