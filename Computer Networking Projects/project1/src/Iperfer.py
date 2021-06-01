import sys
import socket
import time as t
import datetime

sys=sys.argv
def client(sys):
    port=int(sys[2])
    host=sys[1]
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((host, port))
    print("Connect successfully on " + host)
    timeMs = float(sys[3])
    startTime = t.time()
    endTime = 0
    bytesLen = 0
    chunk = bytearray(1000)
    while True:
        sock.send(chunk)
        bytesLen = bytesLen + len(chunk)
        if t.time() > startTime+timeMs:
            endTime = t.time()
            break
    durationMS = endTime - startTime
    kbSent = format(round(bytesLen / 1000))
    sentRate = bytesLen /1000 / 1000 *8 / durationMS
    print("sent = " + str(kbSent) + " KB " +
          ", rate = %.3f Mbps" % sentRate)




def server(sys):

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    port=int(sys[2])
    sock.bind(('',port))
    sock.listen(1)
    print('listening at ',socket.gethostname(),':',port)
    client_sock, client_addr = sock.accept()
    with client_sock:
        
        starttime = datetime.datetime.now()
        print(starttime, end="")
        print(client_addr,'connected')

        count = 0
        while True:
            data = client_sock.recv(2048)
            if data:
                count += len(data)
                del data
                continue
            else:
                break
            #client_sock.close()

    endtime = datetime.datetime.now()
    print(endtime)
    print(client_addr,' disconnected')

    print('received=', format(round(count/1000)),'KB')
    time_takes = endtime - starttime
    time_takes = time_takes.seconds + time_takes.microseconds / 1000000.0
    print('rate=', round(count / 1000 / 1000 * 8 / time_takes,3),'Mbps')

    sock.close()

if(sys[1] == '-s'):
    if(len(sys) != 3):
        print('Error: missing or additional arguments')
        sys.exit(1)
    if(int(sys[2]) < 1024 or int(sys[2]) > 65535):
        print('Error: port number must be in the range 1024 to 65535')
        sys.exit(1)
    server(sys)
else:
    if(len(sys) != 4):
        print('Error: missing or additional arguments')
        sys.exit(1)
    if(int(sys[2]) < 1024 or int(sys[2]) > 65535):
        print('Error: port number must be in the range 1024 to 65535')
        sys.exit(1)
    client(sys)


