# 1. 사용할 JDK 환경 설정 (경량화된 OpenJDK)
FROM eclipse-temurin:17-jdk-jammy

# 2. 앱 실행 경로 생성 및 이동
WORKDIR /app

# 3. 로컬 jar 파일을 컨테이너 안으로 복사
COPY build/libs/AIVideoApp-0.0.1-SNAPSHOT.jar app.jar

# 4. jar 파일 실행 명령어
CMD ["java", "-jar", "app.jar"]