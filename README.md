🎥 WebRTC 기반 화상채팅 서비스

WebRTC SFU 구조를 기반으로
실시간 화상채팅 + 메시지 + CI/CD를 구현한 개인 프로젝트

🔗 Demo

URL: (배포 후 추가)

Test Account: (선택)

📌 Project Overview
서비스 설명

다수의 사용자가 실시간 화상채팅과 메시지를 동시에 주고받을 수 있는 서비스

SFU 방식을 적용해 낮은 지연 시간과 비용 효율성 확보

시그널링과 미디어 경로를 분리해 확장성과 안정성을 고려한 설계

🧩 Features
화상채팅

1:1 및 다자간 화상채팅

WebRTC 기반 실시간 미디어 전송

SFU(mediasoup) 적용

메시지

WebRTC DataChannel 기반 메시지 송수신

전체 메시지 / 귓속말(Private Message)

네트워크 대응

STUN / TURN 서버 연동

NAT 환경에서도 안정적인 연결 지원

운영/배포

CI/CD 기반 자동 배포

무중단(rolling) 배포 지원

🏗️ Architecture
전체 구조
Browser
  │ WebSocket (Signaling)
  ▼
ALB
  ▼
Signaling Server (Node.js)
  │
  ├─ Redis (Room ↔ SFU Mapping)
  │
  ▼
SFU Server (mediasoup)
  │
  └─ WebRTC Media (UDP)
설계 핵심

Control Plane / Media Plane 분리

시그널링 서버는 Stateless → 수평 확장

SFU는 방 단위 고정 할당 → 연결 안정성 확보

Redis를 통해 다수 시그널링 서버 간 상태 공유

sticky session 없이 확장 가능

🛠 Tech Stack
Frontend

WebRTC API

WebSocket

HTML / JavaScript (or React)

Backend

Node.js

WebSocket

mediasoup (SFU)

Infrastructure

AWS EC2

Application Load Balancer

Redis

STUN / TURN (coturn)

DevOps

Docker

GitHub Actions (CI)

Rolling Deployment (CD)

🚀 Getting Started
Prerequisites

Node.js 18+

Docker

Redis

Local Run
# signaling server
cd signaling
npm install
npm run dev

# SFU server
cd sfu
npm install
npm run dev
🔄 CI / CD Pipeline
CI (GitHub Actions)

코드 푸시 시 자동 실행

Lint / Test

Docker 이미지 빌드

CD

EC2 기반 배포

시그널링 서버 Rolling 업데이트

WebSocket 연결 유지

SFU 서버는 신규 방부터 적용

⚠️ Failure Scenarios
상황	영향
시그널링 서버 다운	기존 통화 유지
Redis 장애	신규 방 생성 불가
SFU 재시작	해당 방만 종료

WebRTC 미디어는 브라우저 ↔ SFU 직접 연결로
시그널링 장애 시에도 기존 통화 유지

🧠 Key Design Decisions

왜 SFU인가?

MCU 대비 낮은 지연 / 비용 효율

왜 Redis인가?

다수 시그널링 서버 간 판단 기준 공유

왜 sticky session을 사용하지 않는가?

장애 대응과 수평 확장을 위해 Stateless 설계

왜 SFU는 EC2인가?

장시간 UDP 연결과 포트 제어 필요

📈 Scaling Strategy

시그널링 서버: ALB 기반 수평 확장

SFU 서버: 신규 방부터 새 인스턴스 할당

리전 기반 분산으로 지연 최소화

🧪 What I Learned

WebRTC 시그널링과 미디어 경로의 분리 설계

SFU 구조에서의 부하 분산 전략

Redis를 활용한 분산 환경 상태 관리

WebSocket 기반 실시간 서비스의 CI/CD 운영

실시간 서비스에서의 장애 허용 설계

📌 Future Improvements

인증/인가 (JWT)

EKS 기반 시그널링 서버 운영

SFU 자동 스케일링

모니터링 (Prometheus / Grafana)

🙋‍♂️ Author

Name: (본인 이름)

GitHub: (링크)

Email: (선택)
