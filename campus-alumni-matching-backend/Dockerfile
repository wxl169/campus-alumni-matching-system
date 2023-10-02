# Docker 镜像构建
FROM openjdk:8
VOLUME /tmp
ADD campus-alumni-matching-backend-0.0.1-SNAPSHOT.jar springboot-docker.jar
RUN bash -c 'touch /springboot-docker.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/prod/./urandom","-jar","/springboot-docker.jar","--spring.profiles.active=prod","-c"]
EXPOSE 8888

