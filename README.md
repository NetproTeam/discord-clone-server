# Discord Clone Project (Server)

### 버전 가이드

- jdk 17
- spring boot 3.2.5 

### 실행 가이드

- 빌드
  ```
    # Linux 또는 Mac 사용자 
    ./gradlew build

    # Window 사용자
      gradlew.bat build
    ```  
- 실행
  ```
  java -jar ./build/libs/discord-clone-server-0.0.1-SNAPSHOT.jar
  ```

## API 가이드
- swagger url
  - https://app.swaggerhub.com/apis/PKW3322_1/DiscordCloneServer/1.0.0
  - http://localhost:8080/swagger-ui/index.html#/
- Chatting Socket 접속 url
  - ws://127.0.0.1:8080/
- RTC Signal Server url
  - ws://127.0.0.1:8080/signal
