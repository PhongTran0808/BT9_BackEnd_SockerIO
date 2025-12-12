# Customer Support Backend - Socket.IO

Ứng dụng backend hỗ trợ khách hàng với tính năng **real-time chat** sử dụng Socket.IO giữa khách hàng và manager.

## 📋 Mô tả

Project này cung cấp **Socket.IO Server** với real-time communication để:
- ✅ **Real-time Authentication**: Socket.IO based login cho customer và manager
- ✅ **Instant Messaging**: Tin nhắn được gửi và nhận ngay lập tức
- ✅ **Bidirectional Communication**: Full-duplex real-time communication
- ✅ **Message History**: Lấy lịch sử chat theo user ID
- ✅ **Online Status**: Theo dõi trạng thái online/offline của users
- ✅ **Event-driven Architecture**: Sử dụng events thay vì HTTP requests
- ✅ **Real-time Notifications**: Push notifications cho tin nhắn mới

## 🛠️ Yêu cầu hệ thống

### Phần mềm cần thiết:
- **Java**: JDK 11 hoặc cao hơn
- **Maven**: 3.6.0 hoặc cao hơn
- **IDE**: IntelliJ IDEA, Eclipse, hoặc VS Code (tùy chọn)

### Kiểm tra phiên bản:
```bash
java -version
mvn -version
```

## 📦 Thư viện sử dụng

Project này sử dụng **Socket.IO** cho real-time communication:
- **Socket.IO Server**: `netty-socketio` cho Java
- **Jackson**: JSON processing
- **Netty**: High-performance network framework
- **SLF4J**: Logging framework
- **Java Collections**: ConcurrentHashMap cho in-memory database

### Dependencies trong pom.xml:
```xml
<dependencies>
    <!-- Socket.IO Server for Java -->
    <dependency>
        <groupId>com.corundumstudio.socketio</groupId>
        <artifactId>netty-socketio</artifactId>
        <version>1.7.19</version>
    </dependency>
    
    <!-- JSON processing -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
    
    <!-- Logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>1.7.36</version>
    </dependency>
</dependencies>
```

## 🚀 Cách chạy chương trình

### Cách 1: Chạy Socket.IO Server (Khuyến nghị)

1. **Download dependencies:**
```bash
mvn dependency:resolve
```

2. **Chạy Socket.IO Server:**
```bash
mvn exec:java
```

**Server sẽ khởi động trên:** `ws://localhost:9092`

**Output mong đợi:**
```
🚀 Socket.IO Customer Support Server started!
📱 Server running on: ws://localhost:9092
📡 Android app should connect to: ws://10.0.2.2:9092
⚡ Real-time events:
   - login: Authenticate user
   - send_message: Send message to recipient
   - get_messages: Get message history
   - get_customers: Get customer list (manager only)
   - new_message: Receive real-time messages
⏹️  Press Ctrl+C to stop server
```

### Cách 2: Sử dụng batch file (Windows)

1. **Chạy Socket.IO server batch:**
```bash
run-socketio.bat
```

## 📁 Cấu trúc project

```
src/main/java/com/example/support/
├── SocketIOServer.java           # 🚀 Socket.IO Server chính
├── SimpleServer.java             # 📡 HTTP Server (backup)
├── User.java                     # Entity người dùng
├── ChatMessage.java              # Entity tin nhắn
├── UserRepository.java           # Repository quản lý user
├── ChatMessageRepository.java    # Repository quản lý message
├── AuthController.java           # Controller đăng nhập
├── ChatController.java           # Controller chat messaging
├── ManagerController.java        # Controller quản lý
├── JwtTokenProvider.java         # JWT token generator
├── model/                        # 📂 Models (backup structure)
├── repository/                   # 📂 Repositories (backup structure)
├── controller/                   # 📂 Controllers (backup structure)
└── security/                     # 📂 Security (backup structure)
```

## 📖 Socket.IO Events Documentation

### 🔐 1. Authentication Events

#### Event: `login`
Đăng nhập và tạo JWT token

**Emit:**
```javascript
socket.emit('login', {
    username: 'customer1',
    role: 'CUSTOMER'
});
```

**Listen:**
```javascript
socket.on('login_response', (response) => {
    // response: {success, token, role, userId, username, error}
});
```

**Users có sẵn:**
- `customer1`, `customer2`, `customer3` (role: CUSTOMER)
- `manager` (role: MANAGER)

### 💬 2. Chat Events

#### Event: `send_message`
Gửi tin nhắn real-time

**Emit:**
```javascript
socket.emit('send_message', {
    senderId: 'customer1',
    senderName: 'Customer 1',
    content: 'Hello Manager',
    recipientId: 'manager',
    role: 'CUSTOMER'
});
```

**Listen:**
```javascript
socket.on('message_response', (response) => {
    // response: {success, message, error}
});
```

#### Event: `new_message`
Nhận tin nhắn real-time

**Listen:**
```javascript
socket.on('new_message', (message) => {
    // message: {id, senderId, senderName, content, timestamp, isFromCustomer}
});
```

#### Event: `get_messages`
Lấy lịch sử tin nhắn

**Emit:**
```javascript
socket.emit('get_messages', {
    userId: 'customer1'
});
```

**Listen:**
```javascript
socket.on('messages_response', (response) => {
    // response: {success, messages[], error}
});
```

### 👥 3. Manager Events

#### Event: `get_customers`
Lấy danh sách khách hàng với trạng thái online

**Emit:**
```javascript
socket.emit('get_customers', {});
```

**Listen:**
```javascript
socket.on('customers_response', (response) => {
    // response: {success, customers[], error}
    // customers: [{id, username, role, isOnline}]
});
```

## 🧪 Test Socket.IO với Browser Console

### Test Connection:
```javascript
// Mở browser console và test
const socket = io('http://localhost:9092');

socket.on('connect', () => {
    console.log('🔗 Connected to Socket.IO server');
});
```

### Test Login:
```javascript
socket.emit('login', {
    username: 'customer1',
    role: 'CUSTOMER'
});

socket.on('login_response', (response) => {
    console.log('📤 Login response:', response);
});
```

### Test Send Message:
```javascript
socket.emit('send_message', {
    senderId: 'customer1',
    senderName: 'Customer 1',
    content: 'Hello Manager',
    recipientId: 'manager',
    role: 'CUSTOMER'
});

socket.on('message_response', (response) => {
    console.log('📤 Message response:', response);
});
```

### Test Real-time Messages:
```javascript
socket.on('new_message', (message) => {
    console.log('📥 New message:', message);
});
```

## 📱 Android App Integration

### Socket.IO Configuration cho Android:

1. **Socket.IO URL cho emulator:**
```kotlin
private const val SOCKET_URL = "http://10.0.2.2:9092"
```

2. **Dependencies trong build.gradle:**
```gradle
implementation 'io.socket:socket.io-client:2.0.0'
implementation 'org.json:json:20230227'
```

3. **Network Security Config** (`res/xml/network_security_config.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
    </domain-config>
</network-security-config>
```

4. **AndroidManifest.xml:**
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    android:usesCleartextTraffic="true">
```

### Hướng dẫn chi tiết:
📋 Xem file **ANDROID_SOCKETIO_IMPLEMENTATION_GUIDE.md** để có hướng dẫn đầy đủ implement Socket.IO Android app.

## 🔍 Troubleshooting

### Lỗi thường gặp:

1. **Server không khởi động được:**
```bash
# Kiểm tra port 8080 có bị chiếm không
netstat -an | findstr :8080

# Hoặc thay đổi port trong SimpleServer.java
new InetSocketAddress(8081)
```

2. **Android không kết nối được:**
```bash
# Đảm bảo sử dụng 10.0.2.2 cho emulator
# Đảm bảo server đang chạy trên localhost:8080
# Kiểm tra network security config
```

3. **Compilation errors:**
```bash
# Clean và compile lại
mvn clean compile
```

4. **CORS errors:**
```bash
# Server đã có CORS headers, kiểm tra network config
```

## 📊 Server Logs

Server sẽ hiển thị logs chi tiết:

```
📥 Login request: {"username":"customer1","role":"CUSTOMER"}
📤 Login response: {"token":"customer1:CUSTOMER:1765552631442",...}
📥 Message request: {"senderId":"customer1","content":"Hello Manager"}
💬 Message from Customer 1 to manager: Hello Manager
📤 Message response: {"status":"success","message":"Message sent successfully"}
📤 Messages response for customer1: [{"id":"1","senderId":"customer1",...}]
```

## 🚀 Socket.IO Tính năng đã hoàn thành

- ✅ **Socket.IO Server**: Chạy ổn định trên ws://localhost:9092
- ✅ **Real-time Authentication**: Login với Socket.IO events
- ✅ **Instant Messaging**: Tin nhắn được gửi và nhận ngay lập tức
- ✅ **Bidirectional Communication**: Full-duplex real-time communication
- ✅ **Message History**: Lấy lịch sử chat qua Socket.IO
- ✅ **Online Status Tracking**: Theo dõi users online/offline
- ✅ **Event-driven Architecture**: Sử dụng events thay vì HTTP
- ✅ **Real-time Notifications**: Push notifications cho tin nhắn mới
- ✅ **Connection Management**: Quản lý kết nối client
- ✅ **Error Handling**: Xử lý lỗi real-time

## 🎯 Sẵn sàng cho Android Socket.IO

Backend Socket.IO đã hoàn toàn sẵn sàng để kết nối với Android app:

1. **Socket.IO Events**: Tất cả events hoạt động ổn định
2. **Real-time Communication**: Bidirectional instant messaging
3. **JSON Data Format**: Tương thích với Android Socket.IO client
4. **Connection Status**: Real-time connection monitoring
5. **Event Documentation**: Có hướng dẫn chi tiết cho Android Socket.IO implementation
6. **Online Status**: Theo dõi trạng thái online của users
7. **Message Delivery**: Instant message delivery confirmation

