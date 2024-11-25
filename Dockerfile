# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
# Loại bỏ VOLUME vì Railway không hỗ trợ
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Railway yêu cầu ứng dụng phải lắng nghe cổng từ biến môi trường PORT
ENV PORT=8080

ENTRYPOINT ["java","-Dserver.port=${PORT}","-cp","app:app/lib/*","com.example.flowerstore.FlowerstoreApplication"]
