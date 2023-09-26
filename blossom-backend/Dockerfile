FROM eclipse-temurin:8-jre as builder
WORKDIR application
ARG JAR_FILE=backend/target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

###########################################################

FROM eclipse-temurin:8-jre
MAINTAINER li-guohao <git@liguohao.cn>
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

ENV JVM_OPTS="-Xmx256m -Xms256m" \
    BLOSSOM_WORK_DIR="/home/bl" \
    SPRING_CONFIG_LOCATION="optional:classpath:/;optional:classpath:/config/;optional:file:/home/bl/" \
    TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone

EXPOSE 9999

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} org.springframework.boot.loader.JarLauncher ${0} ${@}"]
