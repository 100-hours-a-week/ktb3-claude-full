# 📖 PC방 좌석 & 주문 관리 CLI 프로그램

## 📌 소개
이 프로젝트는 **PC방 좌석 관리와 주문(음료, 커피, 컵라면 등)** 기능을 지원하는 **CLI 프로그램**입니다.  
주요 기능은 다음과 같습니다:
- [X] 좌석 시작 / 종료
- [X] 좌석별 사용 시간 및 요금 계산
- [ ] 주문 추가 (음료, 커피, 컵라면 등)
- [ ] 모든 좌석 상태 확인 (사용 중/빈 좌석, 주문 내역)
 
`StringConstant`, `ConfigConstant`를 통해 상수를 관리합니다.

---

## 📌 프로젝트 구조
```
src
├── app
│ ├── PcCafeApp.java # 메인 실행 클래스
│ ├── SeatHandler.java # 좌석 및 주문 관리 로직
├── constants
│ ├── StringConstant.java
│ └── ConfigConstant.java
├── domain
│ ├── Seat.java # 추상 클래스 (좌석 공통 기능)
│ ├── CustomerSeat.java  
│ ├── OrderSeat.java
│ └── Product.java
```

--------

---

## 📌 UML 다이어그램

### 📂 Domain 구조
![Domain UML](docs/domain.png)

### 📂 Application 구조
![App UML](docs/app.png)

---

## 📌 주요 클래스 설명

- **Seat (추상 클래스)**
    - 좌석 번호, 사용 여부, 사용 시작/종료, 요금 계산 메서드 정의

- **CustomerSeat (좌석)**
    - 시간제 요금제 적용 (1분당 100원)
    - 기본적으로 주문 불가(수리 중 좌석 및 관리자 로그인으로 추가 기능 제공 가능)

- **OrderSeat (좌석 + 주문 가능)**
    - `Product` 리스트를 보유하여 주문 가능
    - 사용 요금 + 주문 금액 합산

- **Product (상품)**
    - 상품명, 가격, 수량을 관리
    - `toString()` 오버라이딩으로 상품 명 및 전체 금액 보기 쉽게 출력

- **SeatHandler (입력 처리)**
    - 좌석 시작 / 종료 / 주문 / 전체 상태 확인
---
