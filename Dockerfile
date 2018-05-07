FROM nmapdiscover
ADD target/discover-mysql.jar discover-mysql.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "discover-mysql.jar"]