# 무신사 과제(포인트 시스템)

## 개발환경

- java 21, springboot 3, H2, redis(Testcontainer 환경)

## 실행방법
- docker desktop 설치
- spring boot 실행

## 테스트 및 데이터 확인 방법

- 스웨거 : http://localhost:8080/swagger-ui.html
- DB 확인 : http://localhost:8080/h2-console/
    - JDBC URL : jdbc:h2:mem:testdb
    - User Name : sa
