import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 100 }, // Ramp-up
        { duration: '2m', target: 100 },  // Steady
        { duration: '30s', target: 0 },   // Ramp-down
    ],
};

const BASE_URL = 'http://host.docker.internal:8080/api/v1';


export default function() {
    const userId = Math.floor(Math.random() * 100) + 1;
    const amount = [1000, 5000, 10000][Math.floor(Math.random() * 3)];

    // 1. 잔액 조회
    const balanceResponse = http.get(`${BASE_URL}/balances/${userId}`);

    check(balanceResponse, {
        '조회 성공': (r) => r.status === 200,
    });

    sleep(0.2);

    // 2. 충전
    const chargeResponse = http.put(`${BASE_URL}/balances/${userId}`,
        JSON.stringify({ amount: amount }), {
            headers: { 'Content-Type': 'application/json' },
        });

    check(chargeResponse, {
        '충전 성공': (r) => r.status === 200,
        '2초 이내': (r) => r.timings.duration < 2000,
    });

    sleep(0.2);
}
