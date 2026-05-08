// Testa WebSocket em tempo real: conecta como user 2 (Julia), manda mensagem como user 1 (Mariana)
import { Client } from './frontend/node_modules/@stomp/stompjs/esm6/index.js';
import { WebSocket } from 'ws';

// @stomp/stompjs no Node precisa do WebSocket global
globalThis.WebSocket = WebSocket;

let received = false;

console.log('Conectando como usuário 2 (Julia) ao WebSocket...');
const client = new Client({
    brokerURL: 'ws://localhost:8080/ws?userId=2',
    reconnectDelay: 0,
    onConnect: async () => {
        console.log('✓ Conectado ao STOMP como userId=2');

        client.subscribe('/user/queue/messages', (msg) => {
            received = true;
            console.log('✓ MENSAGEM RECEBIDA em tempo real:', msg.body);
            client.deactivate();
        });

        console.log('Aguardando 500ms para subscription estabilizar...');
        await new Promise(r => setTimeout(r, 500));

        console.log('Enviando mensagem de userId=1 para userId=2 via REST...');
        const res = await fetch('http://localhost:8080/api/messages', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ senderId: 1, receiverId: 2, content: 'Teste WS real-time ' + Date.now() })
        });
        const saved = await res.json();
        console.log('Mensagem salva no banco, id=' + saved.id + '. Aguardando push WebSocket...');

        await new Promise(r => setTimeout(r, 2000));
        if (!received) {
            console.error('✗ FALHOU: mensagem não chegou via WebSocket em 2s');
            client.deactivate();
            process.exit(1);
        }
    },
    onStompError: (f) => { console.error('STOMP error:', f.headers?.message); process.exit(1); },
    onWebSocketError: (e) => { console.error('WS error:', e.message); process.exit(1); },
    onDisconnect: () => { if (!received) { console.error('✗ Desconectado sem receber'); process.exit(1); } else { console.log('Desconectado OK'); process.exit(0); } }
});

client.activate();

setTimeout(() => {
    if (!received) {
        console.error('✗ TIMEOUT: nenhuma mensagem recebida em 5s');
        process.exit(1);
    }
}, 5000);
