# 트러블 슈팅 기록

## 1. 문제 인식 및 정의
Spring Boot 애플리케이션 실행 중 JWT 비밀키 미설정 문제로 인해 마크다운 `JwtUtil` 클래스에서 환경 변수를 찾지 못해 예외 발생
`TodoRepository` 에서 `findByIdWithUser` 메서드에서 잘못된 프로퍼티명을 사용하고 있었음

## 2. 해결 방안
- 의사결정 과정
  - 터미널로 비밀키를 발급 받은 후 영구 저장
  - `findByIdWithUser` 메서드에서 올바른 프로퍼티명으로 수정
- 해결 과정
  - 터미널에 환경 변수 설정
    - ```export JWT_SECRET_KEY="my key~"```
  - MacOS에 영구 저장
    - ```echo 'export JWT_SECRET_KEY="my key~"' >> ~/.zshrcsource ~/.zshrc```
  - TodoRepository 메서드 수정
    - findByIdWithUser에서 withUser는 올바른 JPA 프로퍼티명이 아님
    - ```
      @Query("SELECT t FROM Todo t JOIN FETCH t.user WHERE t.id = :todoId") 
      Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
      ```
## 3. 해결 완료
- 회고
  - 환경 변수 문제 해결 : 애플리케이션 실행 시 JWT_SECRET_KEY를 찾지 못하는 오류 해결
  - JPA Query 수정 : fingByIdWithUser가 올바르게 작동하도록 수정
- 전후 데이터 비교

   | 수정 전 오류                                                          | 수정 후 결과         |
    |------------------------------------------------------------------|-----------------|
    | java.lang.IllegalStateException: 환경 변수 'JWT_SECRET_KEY'가 설정되지 않음 | 정상적으로 JWT 토큰 생성 가능 |
    | No property 'withUser' found for type 'Long'                     | JPA Query 정상 실행 |
- 최종 결과
  - ```INFO  --- Started ExpertApplication in 3.362 seconds```
 
## 4. 테스트 커버리지
- Line Coverage
  <img width="1334" alt="스크린샷 2025-02-26 오전 11 46 04" src="https://github.com/user-attachments/assets/23b9e767-d9e3-4d86-8c20-dd2fd832a850" />
- Condition Coverage
  <img width="1334" alt="스크린샷 2025-02-26 오전 11 46 59" src="https://github.com/user-attachments/assets/931110eb-e1b3-478c-9221-3357489346df" />
