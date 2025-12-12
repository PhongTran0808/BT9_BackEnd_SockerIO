# Customer Support Backend

Ứng dụng backend hỗ trợ khách hàng với tính năng chat messaging hoàn chỉnh giữa khách hàng và manager.

## 📋 Mô tả

Project này cung cấp **HTTP Server** với các API hoàn chỉnh để:
- ✅ **Đăng nhập**: Simple JWT authentication cho customer và manager
- ✅ **Gửi tin nhắn**: Customer gửi tin nhắn đến manager
- ✅ **Lấy tin nhắn**: Lấy lịch sử chat theo user ID
- ✅ **Quản lý khách hàng**: Manager xem danh sách customers
- ✅ **CORS Support**: Kết nối với Android app qua emulator
- ✅ **Real-time Logging**: Server logs mọi hoạt động

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

Project này sử dụng **Java thuần** không cần thư viện bên ngoài:
- **Java HTTP Server**: `com.sun.net.httpserver` (built-in)
- **Java Collections**: ConcurrentHashMap cho in-memory database
- **Maven**: Build tool và dependency management

### Dependencies trong pom.xml:
```xml
<dependencies>
    <!-- Không có external dependencies -->
    <!-- Chỉ sử dụng Java standard library -->
</dependencies>
```

## 🚀 Cách chạy chương trình

### Cách 1: Chạy HTTP Server (Khuyến nghị)

1. **Compile project:**
```bash
mvn compile -q
```

2. **Chạy HTTP Server:**
```bash
java -cp target/classes com.example.support.SimpleServer
```

**Server sẽ khởi động trên:** `http://localhost:8080`

**Output mong đợi:**
```
🚀 Customer Support Server started on http://localhost:8080
📱 Android app can now connect to:
   - Login: POST http://localhost:8080/api/auth/login
   - Send Message: POST http://localhost:8080/api/chat/send
   - Get Messages: GET http://localhost:8080/api/chat/messages?userId=customer1
   - Get Customers: GET http://localhost:8080/api/manager/customers
   - Health Check: GET http://localhost:8080/api/health
⏹️  Press Ctrl+C to stop server
```

### Cách 2: Sử dụng batch file (Windows)

1. **Chạy server batch:**
```bash
run-server.bat
```

## 📁 Cấu trúc project

```
src/main/java/com/example/support/
├── SimpleServer.java             # 🚀 HTTP Server chính
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

## 📖 API Documentation

### 🔐 1. Authentication API

#### POST /api/auth/login
Đăng nhập và tạo JWT token

**Request:**
```json
{
    "username": "customer1",
    "role": "CUSTOMER"
}
```

**Response:**
```json
{
    "token": "customer1:CUSTOMER:1765552631442",
    "role": "CUSTOMER",
    "userId": "customer1",
    "username": "customer1"
}
```

**Users có sẵn:**
- `customer1`, `customer2`, `customer3` (role: CUSTOMER)
- `manager` (role: MANAGER)

### 💬 2. Chat API

#### POST /api/chat/send
Gửi tin nhắn từ customer đến manager

**Request:**
```json
{
    "senderId": "customer1",
    "senderName": "Customer 1",
    "content": "Hello Manager",
    "recipientId": "manager",
    "role": "CUSTOMER"
}
```

**Response:**
```json
{
    "status": "success",
    "message": "Message sent successfully"
}
```

#### GET /api/chat/messages?userId=customer1
Lấy danh sách tin nhắn theo user ID

**Response:**
```json
[
    {
        "id": "1",
        "senderId": "customer1",
        "senderName": "Customer 1",
        "content": "Hello Manager",
        "timestamp": 1765552631442,
        "isFromCustomer": true
    }
]
```

### 👥 3. Manager API

#### GET /api/manager/customers
Lấy danh sách khách hàng

**Response:**
```json
[
    {
        "id": "customer1",
        "username": "customer1",
        "role": "CUSTOMER"
    },
    {
        "id": "customer2",
        "username": "customer2",
        "role": "CUSTOMER"
    }
]
```

### ❤️ 4. Health Check API

#### GET /api/health
Kiểm tra trạng thái server

**Response:**
```json
{
    "status": "OK",
    "message": "Customer Support Server is running",
    "timestamp": 1765552631442
}
```

## 🧪 Test APIs với curl/PowerShell

### Test Login:
```bash
# PowerShell
$body = @{username='customer1'; role='CUSTOMER'} | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -Body $body -ContentType 'application/json'
```

### Test Send Message:
```bash
# PowerShell
$body = @{senderId='customer1'; senderName='Customer 1'; content='Hello Manager'} | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/api/chat/send' -Method Post -Body $body -ContentType 'application/json'
```

### Test Get Messages:
```bash
# PowerShell
Invoke-RestMethod -Uri 'http://localhost:8080/api/chat/messages?userId=customer1' -Method Get
```

### Test Health Check:
```bash
# PowerShell
Invoke-RestMethod -Uri 'http://localhost:8080/api/health' -Method Get
```

## 📱 Android App Integration

### Network Configuration cho Android:

1. **Base URL cho emulator:**
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/api/"
```

2. **Network Security Config** (`res/xml/network_security_config.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
    </domain-config>
</network-security-config>
```

3. **AndroidManifest.xml:**
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    android:usesCleartextTraffic="true">
```

### Hướng dẫn chi tiết:
📋 Xem file **ANDROID_MESSAGE_IMPLEMENTATION_GUIDE.md** để có hướng dẫn đầy đủ implement Android app.

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

## 🚀 Tính năng đã hoàn thành

- ✅ **HTTP Server**: Chạy ổn định trên port 8080
- ✅ **Authentication**: Login với username + role
- ✅ **Message Sending**: Gửi tin nhắn thành công
- ✅ **Message Retrieval**: Lấy lịch sử chat
- ✅ **Customer Management**: Quản lý danh sách customers
- ✅ **CORS Support**: Hỗ trợ Android connectivity
- ✅ **Error Handling**: Xử lý lỗi và validation
- ✅ **Real-time Logging**: Logs chi tiết mọi request/response

## 🎯 Sẵn sàng cho Android

Backend đã hoàn toàn sẵn sàng để kết nối với Android app:

1. **Server APIs**: Tất cả endpoints hoạt động ổn định
2. **Data Format**: JSON responses tương thích với Android
3. **CORS**: Đã cấu hình cho cross-origin requests
4. **Error Handling**: Trả về error messages rõ ràng
5. **Documentation**: Có hướng dẫn chi tiết cho Android implementation

## 📞 Liên hệ

Nếu có vấn đề hoặc câu hỏi, vui lòng tạo issue trong repository này.

---

**🎉 Backend hoàn thành! Sẵn sàng cho Android integration!**