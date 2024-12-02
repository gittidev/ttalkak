# 딸깍(Ttalkak)- 분산 컴퓨팅 호스팅 서비스

##### 프로젝트 기간

###### 2024.09. ~ 2024.10 (7주)

## 프로젝트 소개

### ① 도입배경

깃허브 프로젝트를 간편하게 배포할 수 있는 플랫폼

### ② 기획의도

배포에 익숙하지 않거나 서버를 사용하는 비용에 부담을 느끼는 주니어 개발자들을 위한 배포 서비스

웹 사용자와 앱 사용자가 상호작용하여 프로젝트를 배포하고 실행할 수 있는 환경을 제공

- 웹 : 프로젝트를 업로드하여 간단하게 배포할 수 있으며, 배포 상태 및 트래픽을 모니터링
- 앱 : 유휴 자원을 제공하여 웹 사용자의 프로젝트를 로컬 환경에서 실행하고, 수익을 창출

## 프로젝트의 주요 장점

- 유휴 컴퓨터 자원을 활용하여 경제적이고 지속 가능한 배포 환경 제공.
- 블록체인 기술을 통한 공정한 보상 체계.
- 도커 파일 자동화 및 플랫폼별 맞춤 설정으로 사용자 접근성 증대.
- 효율적인 MSA 구조와 데이터 일관성 보장을 위한 SAGA 패턴 활용.
- 로그 데이터 분석 및 AI 기반 서비스 개선으로 최적의 사용자 경험 제공.

---

#### 목차

##### 📊 I. 기술 스택

##### 💁 II. 구현 화면

##### 💻 III. 주요 기술 설명

##### 📑 IV. 프로젝트 설계

---

<br/>

##### 📊 기술 스택

| **Section**                  | **Details**                                                                                                                                                             |
| ---------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **FE Framework**             | Next.js, Electron                                                                                                                                                       |
| **Web Libraries**            | TypeScript, TanstackQuery, Zustand, react-hook-form, Tailwind, Chart.js, Cypress, Storybook, framer-motion, react-loading-skeleton, react-tostify, ethers, web3, lottie |
| **App Libraries**            | TypeScript, Axios, Zustand, Tailwind, Dockerode, Stomp.js                                                                                                               |
| **BE Framework**             | - Spring, FastAPI                                                                                                                                                       |
| **DB**                       | - PostgreSQL, Redis, Elastic Search                                                                                                                                     |
| **Library**                  | - pgrok, Spring Security                                                                                                                                                |
| **Protocol & Message Queue** | - OpenFeign, Kafka                                                                                                                                                      |
| **Server**                   | -                                                                                                                                                                       |

##### 💁 구현 화면

<summary> 메인페이지 </summary>
<div markdown="1">
<img alt='func1.1.1' src='./README_IMG/1.1 (2).PNG' height=300px style="padding-left: 20px;">
<img alt='func1.1.1' src='./README_IMG/1.2.PNG' height=300px style="padding-left: 20px;">
</div>

<br/>
<summary> Frontend, Backend 배포 </summary>
<div markdown="1">
<img alt='func1.1.1' src='./README_IMG/2.1.2.png' height=300px style="padding-left: 20px;">
<img alt='func1.1.1' src='./README_IMG/2.2.png' height=300px style="padding-left: 20px;">
</div>

<br/>
<div markdown="1">
<summary> database 배포</summary>
<img alt='func1.1.1' src='./README_IMG/2.3.png' height=300px style="padding-left: 20px;">
</div>

<br/>
<summary>Desktop 앱</summary>
<div markdown="1">
<img alt='func1.1.1' src='./README_IMG/3.1.jpg' height=300px style="padding-left: 20px;">
 </div>
<br/>

<div markdown="1">
<summary>배포 화면</summary>
<img alt='func1.1.1' src='./README_IMG/4.1.png' height=300px style="padding-left: 20px;">
</div>
 
<br/>
<summary>대시 보드</summary>

<div markdown="1">
<img alt='func1.1.1' src='./README_IMG/dashboard.png' height=300px style="padding-left: 20px;">
 </div>

## 💻 주요 기술 설명

이 프로젝트를 통해 전략 패턴을 적용하여 코드의 확장성과 유지보수성을 개선했으며, 로그 수집 및 시각화를 통해 고객에게 유용한 데이터를 제공할 수 있었습니다. AI를 활용한 인사이트 제공과 캐싱 전략을 통해 성능과 비용을 최적화하였으며, EHCache를 활용하여 대규모 데이터를 처리하는 페이징 성능도 크게 개선했습니다

1. 전략 패턴을 통한 코드 구조 개선
   프로젝트는 빌드 시스템과 패키지 시스템의 다양한 구성을 다루기 위해 전략 패턴을 적용했습니다. 공통 로직을 인터페이스로 추출하고, 구체적인 구현은 별도의 클래스에서 처리하도록 하여 코드의 중복을 줄이고 복잡성을 최소화했습니다. 이를 통해 새로운 빌드나 패키지 시스템이 추가되어도 코드의 수정 없이 쉽게 확장할 수 있는 구조를 만들었습니다.

2. 로그 수집 및 시각화
   고객들이 어떤 기능을 가장 많이 사용하는지 파악하기 위해 리버스 터널링을 통한 API 로그를 수집하고, 이를 로그스태시로 정리한 후, 키바나를 사용하여 시각화했습니다. 이를 통해 딸깍 서비스의 로그를 실시간으로 모니터링할 수 있게 되었으며, 생성형 AI를 통해 고객들이 배포한 서비스의 로그를 분석하여 요약 및 인사이트를 제공하고 있습니다.

3. AI를 활용한 로그 인사이트 제공
   생성형 AI를 도입하여 로그 데이터를 분석하고 인사이트를 제공하는 서비스는 유용하지만, AI 사용에 따른 비용 문제가 있었습니다. 이를 해결하기 위해 레디스 캐싱을 활용하여 기간을 제한하고 API 호출 갱신 여부에 따라 로그 모니터링 생성을 최소화하였습니다. 또한 프롬프트 디자인을 통해 AI의 토큰 사용량을 줄임으로써 비용을 절감했습니다.

4. 페이징 처리 성능 개선
   페이징 처리에 있어서도 캐싱 전략을 활용하여 성능을 개선했습니다. EHCache를 사용하여 로컬 캐싱을 적용했고, 5000개의 프로젝트가 노출되는 상황에서 성능 테스트를 진행한 결과, 시스템 성능이 크게 향상되었습니다. 평균 TPS는 351.0에서 2,395.2로 증가했고, 최고 TPS는 372.0에서 2,774.5로 상승했습니다. 평균 테스트 시간은 142.67초에서 20.56초로 줄어들었으며, 실행된 테스트 수도 19,762에서 134,808로 증가했습니다.

### 1. 사용자 도커 인스턴스 관리

- **데스크탑 어플리케이션 기능**
  - 도커 이미지 다운로드 및 빌드 처리.
  - 웹소켓 기반 명령 전달 및 수행 처리.
  - 현재 사용 중인 포트 관리.
  - 도커 컨테이너 생성, 수정, 삭제 처리.

### 2. 사용한 프로젝트에 맞는 도커 파일 자동 생성

- **자동화된 도커 파일 생성 기능**
  - 배포한 서비스의 플랫폼에 맞춘 자동 도커 파일 생성.
  - 현재 지원 플랫폼: CRA, Vite, Next.js, Gradle, Maven.
  - 깃허브에 직접 추가하지 않고, 할당된 컴퓨터에서 파일 생성.
  - 전략 패턴을 사용한 효율적이고 유연한 파일 생성.

### 3. 블록체인 기반의 자동 결제 시스템

- **자동 결제 처리**
  - `signTransaction` 및 `sendSignedTransaction`을 통한 결제 요청.
  - 특정 시간마다 자동 결제 요청 및 출금 처리.
  - 결제 잔액 부족 시 인스턴스 자동 종료 처리.

### 4. 리버스 터널링 및 소켓 통신

- **안전하고 효율적인 서버 제어**
  - 리버스 터널링을 통해 원격 서버에 안전하고 효율적인 접근 제공.
  - 실시간 양방향 통신으로 즉각적인 서버 관리 및 모니터링 구현.

### 5. 로그 데이터 분석

- **생성형 인공지능을 활용한 로그 데이터 분석**
  - 서비스 로그를 기반으로 인공지능을 활용한 데이터 분석 및 인사이트 제공.

### 6. AI 기반 서비스 분석

- **자동화된 데이터 분석**
  - 사용자의 서비스 사용 패턴을 학습하고 개선점을 제안. 리버스 터널링을 통한 API 로그를 수집하고, 이를 로그스태시로 정리한 후, 키바나를 사용하여 시각화

## 아키텍처 설계

1. 아키텍쳐
   <img alt='func1.1.1' src='./README_IMG/infra.png' height=300px style="padding-left: 20px;">

   ### MSA(Microservices Architecture)

   - **이점**
     - 서비스 간 독립성 증가로 유지보수와 확장성 향상.
     - 각 서비스 경량화로 배포 및 업데이트 과정 간소화.

   ### Kafka와 SAGA 패턴

   - **데이터 일관성 보장**
     - 분산 시스템에서 트랜잭션 관리 개선.
     - 시스템 장애 발생 시에도 데이터 정합성 유지.
