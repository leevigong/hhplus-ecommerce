import http from 'k6/http';
import {sleep, check, group} from "k6";

const BASE = (__ENV.API_URL || 'http://host.docker.internal:8080').replace(/\/+$/, '');

export const options = {
    stages: [
        {duration: '10s', target: 10},
        {duration: '10s', target: 50},
        {duration: '10s', target: 100},
        {duration: '10s', target: 100},
        {duration: '10s', target: 50},
        {duration: '10s', target: 10},
        {duration: '10s', target: 0}
    ],
};

export default function main() {

    group("인기상품 캐싱 성능 테스트", function () {
        const url = `${BASE}/api/v1/products/popular?rankingScope=THREE_DAYS`;
        const res = http.get(url);

        check(res, {
            '응답 상태 200': (r) => r.status === 200
        });
    });

    sleep(1);
}
