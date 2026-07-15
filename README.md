# 电子账本 (Bookkeeping)

## 前言

这只是本人的安卓课程设计，90%代码手搓所以挺简陋的（屎山代码警告），而且不打算维护，如果有缘人看到而且想用的话我倍感荣幸👍。（该文档除前言部分均为AI生成）

## 简介

该项目是一款轻量级的 Android 个人记账应用，支持多用户登录、收支记录、分类统计、预算管理和数据导入导出。采用 SQLite 本地存储，所有数据离线可用，注重隐私安全。

## 功能特性

### 用户管理
- **注册 / 登录**：基于账号 + 密码的本地认证体系，支持多用户隔离
- **持久登录**：通过 SharedPreferences 记忆登录状态，免重复登录
- **个人资料**：修改昵称、密码，自定义头像（从相册选取）
- **注销账号**：带 5 秒倒计时确认的不可撤销操作，同步清除用户数据
- **退出登录**：清除登录态，返回登录页面

### 记账功能
- **收支记录**：自定义数字键盘输入，支持加减法运算
- **收支分类**：
  - 支出：三餐、零食、衣物、交通、日用品、水电煤、网费话费、娱乐、医疗、其他
  - 收入：工资、生活费、红包、外快、其他
- **日期选择**：支持今天/昨天快捷选择及日期选择器
- **备注**：每笔记录可添加文字备注
- **编辑 / 删除**：长按记录可修改或删除

### 首页概览
- **月度账单列表**：按日期分组展示当月所有收支记录
- **月度汇总**：实时显示月收入、月支出、本月结余
- **预算管理**：设置月度预算，双色进度条直观展示预算使用情况
- **备注搜索**：按关键词快速检索相关账单记录
- **月份切换**：自由切换查看历史月份的记录

### 统计分析
- **饼图可视化**：按分类展示支出 / 收入构成比例（基于 MPAndroidChart）
- **关键指标**：月总收入、月总支出、月结余、日均支出
- **收支切换**：独立查看支出或收入的分类占比

### 数据导入导出
- **CSV 导出**：将所有账单记录导出为 CSV 文件（含 BOM 头，Excel 兼容）
- **CSV 导入**：从 CSV 文件批量导入账单记录
- **格式规范**：`金额,分类,日期(YYYY-MM-DD),备注`（没有进行严格的格式校验，如果备注里有什么特殊符号之类的可能会有问题）
- 基于 Android Storage Access Framework (SAF)，兼容各版本 Android

## 技术栈

| 项目         | 说明                                      |
| ------------ | ----------------------------------------- |
| 语言         | Java 11                                   |
| 构建         | Gradle 8.13 (Kotlin DSL)                  |
| 最低 SDK     | 26 (Android 8.0)                         |
| 目标 SDK     | 36                                        |
| 数据库       | SQLite (Android 原生 SQLiteOpenHelper)    |
| 图表库       | [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) v3.1.0 |
| UI 框架      | AndroidX AppCompat + Material Design      |
| 架构组件     | AndroidX Lifecycle (LiveData / ViewModel) |
| ViewBinding  | 已启用                                    |

## 项目结构

```
app/src/main/java/org/mf/bookkeeping/
├── MainActivity.java          # 主界面，承载首页/分析/个人三个 Fragment
├── LoginActivity.java         # 登录/注册页面
├── AddItemActivity.java       # 添加/编辑账单页面（含自定义数字键盘）
├── adapters/
│   └── MainListAdapter.java   # 账单列表适配器
├── database/
│   ├── DatabaseHelper.java    # SQLite 数据库管理（单例）
│   ├── FixedFields.java        # 数据库表/列名常量
│   ├── RecordTable.java        # 账单记录表操作
│   ├── UserTable.java          # 用户表操作
│   └── TableHelper.java        # 数据表操作基类
├── fragments/
│   ├── SuperFragment.java      # Fragment 基类
│   ├── HomeFragment.java       # 首页 - 账单列表与月度概览
│   ├── AnalysisFragment.java   # 分析 - 饼图统计
│   ├── ProfileFragment.java    # 个人中心
│   ├── AddItemFragment.java     # 添加账单基类
│   ├── ExpenditureFragment.java # 支出分类选择
│   └── IncomeFragment.java      # 收入分类选择
├── models/
│   ├── BillRecord.java          # 账单记录模型
│   ├── User.java                # 用户模型
│   └── DayEntry.java            # 单日账单分组模型
├── util/
│   ├── Number.java              # 精确金额类（整数+两位小数，避免浮点误差）
│   ├── AmountBuilder.java       # 金额输入构建器（支持加减运算）
│   ├── AssembleNumber.java      # 数字组装工具类
│   ├── Category.java            # 收支分类定义
│   ├── AppDate.java             # 日期工具类
│   └── CSVUtil.java             # CSV 导入导出工具
└── widgets/
    └── DualColorProgressBar.java # 双色进度条（预算展示）
```

## 数据库设计

### users 表

| 字段           | 类型    | 说明         |
| -------------- | ------- | ------------ |
| _id            | INTEGER | 主键，自增   |
| name           | TEXT    | 用户名       |
| account        | INTEGER | 账号         |
| password       | TEXT    | 密码         |
| portrait_path  | TEXT    | 头像本地路径 |

### records 表

| 字段           | 类型    | 说明               |
| -------------- | ------- | ------------------ |
| _id            | INTEGER | 主键，自增         |
| user_id        | INTEGER | 关联用户 ID        |
| year           | INTEGER | 年                 |
| month          | INTEGER | 月                 |
| day_of_month   | INTEGER | 日                 |
| category       | INTEGER | 分类 ID（见分类表）|
| note           | TEXT    | 备注               |
| amount         | TEXT    | 金额（精确两位小数）|

## 核心设计说明

### 精确金额处理

应用未使用 `double` / `float` 存储金额，而是自定义了 `Number` 类，将金额拆分为整数部分和两位小数部分分别存储为 `int`，所有运算通过转换为"分"（cent）后的 `long` 运算完成，从根源上避免了浮点数精度问题。

### 自定义计算器键盘

`AmountBuilder` 实现了一个类似计算器的输入体验，用户可以在记账时直接输入表达式（如 `100 - 20.5 + 10`），系统自动解析并计算出最终金额，提升了记账效率。

### 多用户数据隔离

所有数据库查询均以 `user_id` 作为过滤条件，确保不同用户的账单数据完全隔离。写入操作前会校验当前登录状态，未登录时拒绝写入。

## 构建与运行

### 环境要求

- Android Studio (Hedgehog 或更高版本)
- JDK 11+
- Android SDK 36

### 构建步骤

```bash
# 克隆项目
git clone <repository-url>
cd Bookkeeping

# 使用 Gradle Wrapper 构建
./gradlew assembleDebug
```

构建产物位于 `app/build/outputs/apk/debug/app-debug.apk`。

### 在 Android Studio 中运行

1. 用 Android Studio 打开项目根目录
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 点击 Run 或使用 `Shift + F10` 运行

## 权限说明

| 权限                        | 用途                              | 限制              |
| --------------------------- | --------------------------------- | ----------------- |
| READ_EXTERNAL_STORAGE       | 读取导入文件（Android 9 及以下）  | maxSdkVersion=28  |
| WRITE_EXTERNAL_STORAGE      | 写入导出文件（Android 9 及以下）  | maxSdkVersion=28  |

> Android 10+ 通过 Storage Access Framework (SAF) 进行文件操作，无需上述存储权限。
