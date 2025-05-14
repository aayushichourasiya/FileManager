# File Manager

A Spring Boot application that allows you to upload, download, list, and delete files from an AWS S3 bucket. 

## 🚀 Features

- Upload files to an S3 bucket
- Download files from S3
- List files in the S3 bucket
- Delete files from the S3 bucket
- Logs file operations in the database

---

## ⚙️ Technologies

- Java 17+
- Spring Boot
- AWS SDK v1 and v2
- S3
- JPA 
- H2

---

## 🛠️ Getting Started

### Prerequisites

- Java 17+
- Maven
- AWS credentials (access key, secret key)
- S3 bucket created

### Setup

1. Clone the repository:

```bash
git clone https://github.com/your-username/aws-bucket-file-transfer-service.git
cd aws-bucket-file-transfer-service

```

2. Update your application.properties with your AWS and DB configuration.
```
app.bucketName=your-bucket-name
app.region=your-region
app.accessKey=your-access-key
app.secretKey=your-secret-key

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
```

3. Run the application
```
./mvnw spring-boot:run
```


### 📫 API Endpoints

1. Upload a file - 
POST /api/s3/upload 
```
curl --location 'http://localhost:8080/s3/upload' 
--form 'file=@"/Users/java.txt"'
```

2. Download a file - 
GET /api/s3/download/{fileName}
```
curl --location 'http://localhost:8080/s3/download?fileName=java.txt'
```

3. List all files - 
GET /api/s3/list
```
curl --location --request GET 'http://localhost:8080/s3/files' 
```

4. Delete a file - 
DELETE /api/s3/delete/{fileName}
```
curl --location --request DELETE 'http://localhost:8080/s3/delete?fileName=1'
```



