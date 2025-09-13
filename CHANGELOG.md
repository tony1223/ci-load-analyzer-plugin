# 更新日誌

## [1.0.0] - 2025-01-13

### 新增
- 🎉 初始版本發布
- ✨ 支援 `$this->load->library()` 分析
- ✨ 支援 `$this->load->model()` 分析  
- ✨ 支援 `$this->load->database()` 分析
- 🎯 自動生成 PHPDoc `@property` 註解
- 🔄 智能類別名稱映射
- 🛡️ 自動去重複屬性
- ⚡ 一鍵操作，右鍵選單整合
- ⌨️ 支援快捷鍵 `Ctrl+Shift+G`

### 功能細節
- **Library 載入**: 支援路徑形式如 `personal_overtime_application/Overtime_application_component`
- **Model 載入**: 自動處理 `_model` 後綴和命名轉換
- **Database 載入**: 自動識別 `$this->load->database()` 並生成 `$db` 屬性
- **智能更新**: 可更新現有 PHPDoc 註解，不會覆蓋其他內容

### 技術規格
- 最低支援 IntelliJ IDEA 2023.1+
- 需要 PHP 插件
- Java 11+ 建置環境