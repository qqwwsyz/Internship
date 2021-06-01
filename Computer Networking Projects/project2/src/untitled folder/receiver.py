# ----- receiver.py -----

# !/usr/bin/env python
from socket import *
import sys
import select

host = "0.0.0.0"
port = int(sys.argv[1])
num = 0
s = socket(AF_INET, SOCK_DGRAM)
s.bind((host, port))
addr = (host, port)
buf = 1024 + 1  # 1 for rudp header

ACK = 0
try:
    while True:
        data, address = s.recvfrom(buf)
        data = data.decode()
        seq_num = int(data[0])

        if seq_num == 0:
            s.sendto('ACK=1'.encode(), address)
            ACK = 1
            sys.stdout.write(data[1:])
            seq_num = 1

        elif seq_num < ACK:
            pass

        elif seq_num == ACK:
            ACK += 1
            ACK %= 10
            s.sendto(("ACK=%d" % ACK).encode(), address)
            sys.stdout.write(data[1:])

        else:
            s.sendto(("ACK=%d" % ACK).encode(), address)

        s.settimeout(5)

except timeout:
    s.close()
    pass
# python3 ./receiver.py 8999 > RECEIVED_FILE
