from socket import *
import sys
import time

s = socket(AF_INET, SOCK_DGRAM)
host = sys.argv[1]
port = int(sys.argv[2])

buf = 1024
addr = (host, port)  # address send to

bytecounter = 0

begin = time.time()  # Setup start time

queue = list(range(0, 10)) * 2  # circular queue
left = 0
right = 10  # init the slide window, size=10

clock = {}
for q in queue:
    clock[q] = 4  # set timeout = 4


# divide and add seq_num
def rudp_packets(raw):
    arr = bytearray(raw.encode())
    pieces = [arr[i:i + buf] for i in range(0, len(arr), buf)]

    for index, item in enumerate(pieces):
        seq = bytearray(str(index % 10).encode())  # seq_num 0-19,twice as window size
        pieces[index] = seq + item  # add seq_num as header ahead
    return pieces


raw = sys.stdin.read()
packets = rudp_packets(raw)

i = 0  # index of packets
ACK = 0  # init ACK

while i < len(packets):
    data = packets[i]
    seq_num = int(data[:1].decode())  # current seq_num

    try:
        s.settimeout(4)

        if i == 0:  # initial send
            s.sendto(data, addr)
            bytecounter += len(data)
            # stop-and-wait receive ACK
            ack = s.recvfrom(buf)[0].decode()
            ACK = int(ack[-1])
            # increment index, send next packet
            i += 1

        elif ACK == seq_num:  # if get same ACK, move next
            print("ACK:{}, seq_num:{}".format(ACK, seq_num))
            s.sendto(data, addr)
            bytecounter += len(data)
            ack = s.recvfrom(buf)[0].decode()
            ACK = int(ack[-1])
            i += 1

        else:  # if get unexpected ACK, retransmit
            print("retransmit: ACK:{}, seq_num:{}".format(ACK, seq_num))
            s.sendto(packets[i - 1], addr)
            bytecounter += len(data)
            ack = s.recvfrom(buf)[0].decode()
            ACK = int(ack[-1])

    except timeout:
        # receive no ACK
        print("TIMEOUT retransmit: ACK:{}, seq_num:{}".format(ACK, seq_num))
        s.sendto(data, addr)
        ACK = (ACK + 1) % 10
        i += 1
    finally:
        for k, v in clock.items():  # timeout update
            clock[k] -= 1

s.close()
print("----- Finished -----")
end = time.time()  # Setup end time

# Timer
time = (end - begin)
speed = bytecounter / time / 1000
time = float("%0.3f" % (time))
speed = float("%0.3f" % (speed))
print("Sent " + str(bytecounter) + " bytes in " + str(time) + " seconds: " + str(speed) + " kB/s")
s.close()
print("File received, exiting.")

# cat HUGE_FILE | python3 ./sender.py 0.0.0.0 8999
# cat f.txt | python3 ./sender.py 0.0.0.0 8999
