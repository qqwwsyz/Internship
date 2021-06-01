import sys
import socket
import time
from typing import List, Union

#############
# Utils
#############
FIN = 1
SYN = 2
ACK = 4


def int_to_bytes(num: int, length: int) -> bytes:
    return num.to_bytes(length=length, byteorder=BYTE_ORDER)


def bytes_to_int(byte_array: bytes) -> int:
    return int.from_bytes(byte_array, byteorder=BYTE_ORDER)


class Packet:
    """
    Attributes:
        seq_num: 32 bits integer, sequence number of the packet
        ack_num: 32 bits integer, acknowledge number of the packet
        conn_id: 16 bits integer, connection id
        data: data to be send in the packet
        syn: 1 bit, synchronize sign
        ack: 1 bit, acknowledgement sign
        fin: 1 bit, close connection sign
    """
    conn_id: int
    seq_num: int
    ack_num: int
    data: bytes
    syn: bool
    ack: bool
    fin: bool

    def __init__(self, conn_id: int, seq_num: int, ack_num: int,
                 data: Union[bytes, None] = None, syn: bool = False,
                 ack: bool = False, fin: bool = False):
        self.conn_id = conn_id
        self.seq_num = seq_num
        self.ack_num = ack_num
        self.data = data
        self.syn = syn
        self.ack = ack
        self.fin = fin
        self.timer = time.time()

    def pack(self) -> bytes:
        """Serialize the packet into a bytes array. The format of the packet is
        | sequence number: 4 bytes     |
        | acknowledge number: 4 bytes  |
        | connection id: 2 bytes       |
        | Not Used: 13 bits | A: 1 bit | S: 1 bit | F: 1 bit |
        | data                         |
        """
        b_seq_num = int_to_bytes(self.seq_num, 4)
        b_ack_num = int_to_bytes(self.ack_num, 4)
        b_conn_id = int_to_bytes(self.conn_id, 2)
        sign = 0
        if self.syn:
            sign += SYN
        if self.ack:
            sign += ACK
        if self.fin:
            sign += FIN
        b_sign = int_to_bytes(sign, 2)
        header = b_seq_num + b_ack_num + b_conn_id + b_sign
        if not self.data:
            return header
        return header + self.data

    def __lt__(self, other):
        return self.seq_num < other.seq_num

    def __le__(self, other):
        return self.seq_num <= other.seq_num

    def __gt__(self, other):
        return self.seq_num > other.seq_num

    def __ge__(self, other):
        return self.seq_num >= other.seq_num

    def __str__(self):
        str_pack = str(self.seq_num) + " "
        str_pack += str(self.ack_num) + " "
        str_pack += str(self.conn_id)
        if self.ack:
            str_pack += " ACK"
        if self.syn:
            str_pack += " SYN"
        if self.fin:
            str_pack += " FIN"
        return str_pack

    def __repr__(self):
        return self.__str__()

    def tick(self):
        self.timer = time.time()


def parse_packet(packet: bytes) -> Packet:
    """Parse the packet received into a instance of Packet.
    """
    seq_num = bytes_to_int(packet[0:4])
    ack_num = bytes_to_int(packet[4:8])
    conn_id = bytes_to_int(packet[8:10])
    sign = bytes_to_int(packet[10:12])
    is_fin = (sign & FIN == FIN)
    is_syn = (sign & SYN == SYN)
    is_ack = (sign & ACK == ACK)
    return Packet(conn_id, seq_num, ack_num, packet[12:],
                  is_syn, is_ack, is_fin)


def log(command: str, pa: Packet, dup: bool = False):
    print(f"{command} sequence number: {pa.seq_num}, acknowledge number: {pa.ack_num} "
          f"connection id: {pa.conn_id} "
          f"window: {cnwd} threshold: {threshold}"
          f"{'length: ' + str(len(pa.data)) if pa.data else ''}"
          f"{' ACK' if pa.ack else ''}"
          f"{' SYN' if pa.syn else ''}"
          f"{' FIN' if pa.fin else ''}"
          f"{' DUP' if dup else ''}")


#########################
# State of the connection
#########################
INIT = 0
SYN_SENT = 1
ESTABLISHED = 2
FIN_WAIT = 3
TIME_WAIT = 4

################
# Constants
################
BYTE_ORDER = 'big'
# The maximum sequence and acknowledgment number
MAX_SEQ_NUM = 204800
# The maximum window size
# (in case of confuse about the sequence number of packet)
MAX_WND_SIZE = MAX_SEQ_NUM - 1
# maximum size of data to be encapsulated in packet
MAX_DATA_SIZE = 512
# maximum size of each packet
MAX_PACKET_SIZE = MAX_DATA_SIZE + 12
# Initial and minimum congestion window size
INIT_CROWD_WND_SIZE = MAX_DATA_SIZE * 2
# Initial slow-start threshold
SS_THRESH = 15000
# Initial sequence number
INIT_SEQ_NUM = 42
# The maximum connection id
MAX_CONN_ID = (1 << 16) - 1
# Whenever client receives no packets from server for more than
# 10 seconds
MAX_WAIT_TIME_BEFORE_EXIT = 10
# retransmission timeout, 0.5 seconds
RETRANSMISSION_TIMEOUT = 0.5
# default connection id
DEFAULT_CONN_ID = 0

####################
# ERROR CODE
####################
EXCEED_MAX_SILENT_TIME = -1

server_addr = (sys.argv[1], int(sys.argv[2]))
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

state = INIT
conn_id = 0
seq_number = INIT_SEQ_NUM
ack_number = 0

# list of  packet has been sent but not acknowledged
send_queue: List[Packet] = []
# initialize the crowd window size
cnwd = INIT_CROWD_WND_SIZE
threshold = SS_THRESH
# sum of data bytes in the queue wait to confirm
send_window = 0


def retransmit():
    global threshold, cnwd
    if len(send_queue) > 0:
        threshold, cnwd = cnwd // 2, INIT_CROWD_WND_SIZE
        count = 0
        for r_packet in send_queue:
            if count >= cnwd:
                break
            sock.sendto(r_packet.pack(), server_addr)
            log("SEND", r_packet, True)
            r_packet.tick()
            if r_packet.syn or r_packet.fin:
                count += 1
            else:
                count += len(data)


def send(p: Packet):
    send_queue.append(p)
    sock.sendto(p.pack(), server_addr)
    p.tick()


# send syn to server and transfer to SYN_SENT state
syn_packet = Packet(DEFAULT_CONN_ID, seq_number, ack_number,
                    syn=True)
send(syn_packet)
log("SEND", syn_packet)
# syn consume one logical byte
seq_number = (seq_number + 1) % MAX_SEQ_NUM
send_window += 1
# state transition
state = SYN_SENT

number_of_timeout = 0
sock.settimeout(RETRANSMISSION_TIMEOUT)

while True:
    try:
        packet, addr = sock.recvfrom(MAX_PACKET_SIZE)
        if addr != server_addr:
            continue
        packet = parse_packet(packet)
        log("RECV", packet)
        number_of_timeout = 0
    except socket.timeout:
        number_of_timeout += 1
        if state == TIME_WAIT:
            break
        elif number_of_timeout * RETRANSMISSION_TIMEOUT > MAX_WAIT_TIME_BEFORE_EXIT:
            sys.exit(EXCEED_MAX_SILENT_TIME)
        else:
            retransmit()
            continue
    if len(send_queue) > 0 \
            and send_queue[0].timer + RETRANSMISSION_TIMEOUT < time.time():
        retransmit()
    if state != SYN_SENT and packet.seq_num != ack_number:
        ack_packet = Packet(conn_id, seq_number, ack_number,
                            ack=True)
        sock.sendto(ack_packet.pack(), server_addr)
        log("SEND", ack_packet)

    # ignore the packet if the connection id is wrong
    if state != SYN_SENT and (packet.conn_id != conn_id
                              or packet.seq_num != ack_number):
        log("DROP", packet)
        continue

    # handle ack
    if packet.ack:
        if not packet.syn:
            if cnwd < threshold:
                cnwd += MAX_DATA_SIZE
            else:
                cnwd = min(MAX_WND_SIZE, cnwd + 512 * 512 // cnwd)
        while len(send_queue) > 0:
            cycle = False
            # sequence cycle exist
            if send_queue[0].seq_num > send_queue[-1].seq_num:
                cycle = True
            if not cycle:
                if packet.ack_num > send_queue[0].seq_num:
                    p = send_queue.pop(0)
                else:
                    break
            else:
                # all the packet in the window is acknowledged
                if send_queue[-1].seq_num < packet.ack_num < send_queue[0].seq_num:
                    p = send_queue.pop(0)
                elif send_queue[0].seq_num < packet.ack_num < MAX_SEQ_NUM:
                    p = send_queue.pop(0)
                elif len(send_queue) > 1 and send_queue[-1].seq_num >= packet.ack_num:
                    p = send_queue.pop(0)
                else:
                    break
            if p.seq_num < packet.ack_num:
                cycle = False
            if p.syn or p.fin:
                send_window -= 1
            elif p.data:
                send_window -= len(p.data)

    # establish the connection when the state is syn_sent and receives syn
    # and ack
    if state == SYN_SENT and packet.syn and packet.ack:
        ack_number = (packet.seq_num + 1) % MAX_SEQ_NUM
        conn_id = packet.conn_id
        state = ESTABLISHED

    if state == ESTABLISHED:
        finished = False
        while send_window < cnwd:
            data_size = min(MAX_DATA_SIZE, cnwd - send_window)
            data = sys.stdin.buffer.read(data_size)
            if len(data) == 0:
                finished = True
                break
            data_packet = Packet(conn_id, seq_number, ack_number, data,
                                 ack=True)
            send(data_packet)
            log("SEND", data_packet)
            seq_number = (seq_number + len(data)) % MAX_SEQ_NUM
            send_window += len(data)
        # send fin to close the connection
        if finished:
            fin_packet = Packet(conn_id, seq_number, ack_number, None,
                                fin=True)
            send(fin_packet)
            log("SEND", fin_packet)
            seq_number = (seq_number + 1) % MAX_SEQ_NUM
            send_window += 1
            state = FIN_WAIT
            continue
    if state == FIN_WAIT and len(send_queue) == 0:
        state = TIME_WAIT
        sock.settimeout(RETRANSMISSION_TIMEOUT * 4)
    if state == TIME_WAIT and packet.fin:
        ack_number = (packet.seq_num + 1) % MAX_SEQ_NUM
        ack_packet = Packet(conn_id, seq_number, ack_number, ack=True)
        sock.sendto(ack_packet.pack(), server_addr)
        log("SEND", ack_packet)
