import { Injectable, NgZone } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { Subject } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ChatService {
    private client?: Client;
    private connectedUserId?: number;

    message$ = new Subject<any>();
    activePartnerId: number | null = null;

    constructor(private zone: NgZone) {}

    connect(userId: number) {
        if (this.connectedUserId === userId && this.client?.active) return;
        this.disconnect();
        this.connectedUserId = userId;

        this.client = new Client({
            brokerURL: `${environment.wsUrl}/ws?userId=${userId}`,
            reconnectDelay: 5000,
            onConnect: () => {
                this.client!.subscribe('/user/queue/messages', (msg: IMessage) => {
                    try {
                        const parsed = JSON.parse(msg.body);
                        this.zone.run(() => this.message$.next(parsed));
                    } catch { /* ignore parse errors */ }
                });
            },
            onStompError: (frame) => console.error('[ChatService] STOMP error', frame),
            onWebSocketError: (evt) => console.error('[ChatService] WebSocket error', evt),
            onDisconnect: () => console.warn('[ChatService] Disconnected')
        });

        this.client.activate();
    }

    disconnect() {
        this.client?.deactivate();
        this.connectedUserId = undefined;
    }
}
