# 构建阶段
FROM --platform=$BUILDPLATFORM docker.m.daocloud.io/library/maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 配置阿里云 Maven 镜像
RUN mkdir -p /root/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>\
<settings><mirrors><mirror>\
<id>aliyun</id>\
<mirrorOf>central</mirrorOf>\
<url>https://maven.aliyun.com/repository/central</url>\
</mirror><mirror>\
<id>aliyun-public</id>\
<mirrorOf>public</mirrorOf>\
<url>https://maven.aliyun.com/repository/public</url>\
</mirror></mirrors></settings>' > /root/.m2/settings.xml

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# 运行阶段
FROM docker.m.daocloud.io/library/eclipse-temurin:17-jre
WORKDIR /app
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
