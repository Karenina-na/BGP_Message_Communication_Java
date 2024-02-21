# BGP_Message_Communication_Java
### 简介
本项目实现了BGP协议中的几种常见消息的组装和解析,包括Open、Keepalive、Notification、Route Refresh和Update消息。

### 主要功能:

- 支持BGP消息的组装和解析
- 提供插件接口,可扩展其他BGP消息
- 支持XML格式的BGP消息读取和输出

### 使用方法:

1. BGPClient实现了一次简单的五种消息与BGP服务器的交互过程,可作为参考
2. message包中提供了BGP消息的组装方法,可直接调用
3. parsers包中提供了BGP消息的解析方法,可直接调用
4. 解析和组装的方法均提供了XML格式的读取和输出方法

### 项目结构:
```text
├─src
│  ├─main
│  │  ├─java
│  │  │  └─org
│  │  │      └─example
│  │  │          ├─message
│  │  │          │  ├─keeplive      # Keepalive消息的组装
│  │  │          │  ├─notification  # Notification消息的组装
│  │  │          │  ├─open          # Open消息的组装
│  │  │          │  │  └─open_opt   # Open消息的Optional Parameter
│  │  │          │  ├─refresh       # Route Refresh消息的组装
│  │  │          │  └─update        # Update消息的组装
│  │  │          │     └─path_attr  # Update消息的Path Attribute
│  │  │          │  
│  │  │          └─parsers
│  │  │              ├─keeplive # 解析Keepalive消息
│  │  │              ├─notification # 解析Notification消息
│  │  │              ├─open     # 解析Open消息
│  │  │              ├─refresh  # 解析Route Refresh消息
│  │  │              └─update   # 解析Update消息
│  │  └─resources
│  │      └─xml_demo    # 存放XML格式的BGP消息
│  └─test
│      └─java
├─target
├─xml       # 存放XML格式的BGP消息
```

### 项目依赖:
- JDK 11
- Maven 3.6.3
- hutool 5.8.22
- fastjson 2.0.1
- dom4j 2.1.3
- junit 4.13.2
- logback 1.2.3

### 项目运行:

1. 克隆项目到本地
2. 使用IDEA打开项目
3. 运行BGPClient类（需要类中定义的路由器对端信息）

### 项目测试:

test包中提供了对BGP消息的组装和解析的测试用例,可直接运行
