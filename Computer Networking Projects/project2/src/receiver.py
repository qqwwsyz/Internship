import sys
import socket
import random
import time
from typing import List, Tuple, Union

#################
# Utils
################
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
    # print(f"{command} sequence number: {pa.seq_num}, acknowledge number: {pa.ack_num} "
    #       f"connection id: {pa.conn_id} "
    #       f"window: {cnwd} threshold: {threshold}"
    #       f"{'length: ' + str(len(pa.data)) if pa.data else ''}"
    #       f"{' ACK' if pa.ack else ''}"
    #       f"{' SYN' if pa.syn else ''}"
    #       f"{' FIN' if pa.fin else ''}"
    #       f"{' DUP' if dup else ''}")
    pass


#########################
# State of the connection
#########################
SYN_RECEIVED = 1
ESTABLISHED = 2
LAST_ACK = 3
CLOSED = 4

################
# Constants
################
BYTE_ORDER = 'big'
# The maximum sequence and acknowledgment number
MAX_SEQ_NUM = 204800
# Initial slow-start threshold
SS_THRESH = 15000
# The maximum window size
# (in case of confuse about the sequence number of packet)
MAX_WND_SIZE = MAX_SEQ_NUM - 1
# maximum size of data to be encapsulated in packet
MAX_DATA_SIZE = 512
# maximum size of each packet
MAX_PACKET_SIZE = MAX_DATA_SIZE + 12
# Initial and minimum congestion window size
INIT_CROWD_WND_SIZE = MAX_DATA_SIZE * 2
# retransmission timeout, 0.5 seconds
RETRANSMISSION_TIMEOUT = 0.5
# Initial sequence number
INIT_SEQ_NUM = 42
# The maximum connection id
MAX_CONN_ID = (1 << 16) - 1

####################
# variables
####################
port = int(sys.argv[1])
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(('0.0.0.0', port))

state = None
address: Tuple[str, int] = None
conn_id = 0

seq_number = INIT_SEQ_NUM
ack_number = 0
# list of  packet has been sent but not acknowledged
send_queue: List[Packet] = []
# initialize the crowd window size
# since the server just
cnwd = INIT_CROWD_WND_SIZE
threshold = SS_THRESH


def send(p: Packet):
    send_queue.append(p)
    sock.sendto(p.pack(), address)


sock.settimeout(RETRANSMISSION_TIMEOUT)
while state is None or state != CLOSED:
    try:
        packet, addr = sock.recvfrom(MAX_PACKET_SIZE)
        packet = parse_packet(packet)
        if random.random() > 0.95:
            continue
        log("RECV", packet)

        # ignore the packet if the packet is out of order
        if not packet.syn and packet.seq_num != ack_number:
            ack_packet = Packet(conn_id, seq_number,
                                ack_number, ack=True)
            sock.sendto(ack_packet.pack(), address)
            log("SEND", ack_packet)
        if not packet.syn and (packet.seq_num != ack_number
                               or packet.conn_id != conn_id
                               or addr != address):
            log("DROP", packet)
            continue

        # remove the packet has been acknowledged and restart the timer
        # if the send queue is not empty
        if packet.ack:
            while len(send_queue) > 0 \
                    and packet.ack_num > send_queue[0].seq_num:
                send_queue.pop(0)

        # handle syn packet
        if packet.syn:
            ack_number = (packet.seq_num + 1) % MAX_SEQ_NUM
            conn_id = random.randint(1, MAX_CONN_ID)
            address = addr
            # ack_number will be packet.seq_num since syn take logically
            # one byte of data stream, syn bit and ack bit should be set
            syn_ack = Packet(conn_id, seq_number, ack_number,
                             syn=True, ack=True)
            # syn consume one logically one byte
            seq_number = (INIT_SEQ_NUM + 1) % MAX_SEQ_NUM
            # send the packet and start the timer
            send(syn_ack)
            log("SEND", syn_ack)
            # state transition
            state = SYN_RECEIVED
        elif state == SYN_RECEIVED and packet.ack:
            # for each valid packet of the connection, the server responds
            # with an ACK packet, except only ack flag and empty payload
            if packet.data:
                # update the expected sequence number
                ack_number = ((packet.seq_num + len(packet.data))
                              % MAX_SEQ_NUM)
                sys.stdout.buffer.write(packet.data)
                # f.write(packet.data)
                ack_packet = Packet(conn_id, seq_number,
                                    ack_number, ack=True)
                sock.sendto(ack_packet.pack(), address)
                log("SEND", ack_packet)
            # if data is empty, i.e. it's an ack packet with empty payload,
            # the expected sequence of data will keep the same since ack
            # doesn't consume logical byte
            state = ESTABLISHED
        elif state == ESTABLISHED and packet.fin:
            ack_number = (packet.seq_num + 1) % MAX_SEQ_NUM
            fin_ack = Packet(conn_id, seq_number,
                             ack_number, ack=True, fin=True)
            send(fin_ack)
            log("SEND", fin_ack)
            # like syn, fin take one logical byte
            seq_number = (seq_number + 1) % MAX_SEQ_NUM
            # since server has no data to send to client, we omit the
            # CLOSE_WAIT state
            state = LAST_ACK
        elif state == LAST_ACK and packet.ack:
            state = CLOSED
        elif state == ESTABLISHED and packet.data:
            # received data from client
            sys.stdout.buffer.write(packet.data)
            # f.write(packet.data)
            ack_number = ((packet.seq_num + len(packet.data))
                          % MAX_SEQ_NUM)
            ack_packet = Packet(conn_id, seq_number,
                                ack_number, ack=True)
            sock.sendto(ack_packet.pack(), address)
            log("SEND", ack_packet)
    except socket.timeout:
        for p in send_queue:
            sock.sendto(p.pack(), address)
            log("SEND", p, True)
            p.tick()
    if (len(send_queue) > 0
            and send_queue[0].timer + RETRANSMISSION_TIMEOUT < time.time()):
        for p in send_queue:
            sock.sendto(p.pack(), address)
            log("SEND", p, True)
            # restart the timer
            p.tick()
