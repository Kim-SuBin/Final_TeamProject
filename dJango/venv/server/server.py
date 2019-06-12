import os
from socket import *
import django
os.environ.setdefault("DJANGO_SETTINGS_MODULE", "mysite.settings")
django.setup()
from apartment.models import Apart
import  threading
raspberry={}

def echoserver(port):
    serverSock = socket(AF_INET, SOCK_STREAM)
    serverSock.bind(('', port))
    serverSock.listen(5)
    print('%d번 포트로 접속 대기중...'%port)
    while True:
        connectionSock,addrs = serverSock.accept()
        recvData=connectionSock.recv(1024).decode('utf-8')
        if recvData =="P2\n" or recvData=="P3\n" or recvData=='DOOR\n': #앱에서 페이지 정보 받으면 pass
            #print(recvData)
            pass
        else: #라즈베리파이 정보 받으면 라즈베리파이 connection정보 저장
            #print(recvData)
            raspberry[recvData]=connectionSock
        print('Connected by{}'.format(connectionSock))
        handler = threading.Thread(target=run, args=(connectionSock, addrs))
        handler.daemon = True
        handler.start()

def run(connectionSock,addrs):
    while True:
        try:
            recvData = connectionSock.recv(1024).decode('utf-8')
            print(recvData)
            if '$' in recvData:  #라즈베리파이 키패드에서 비밀번호 입력 받는 코드
                d = recvData.split("$")[0] # 비밀번호에 포함된 #을 삭제한다
                print(d)
                db_pass = Apart.objects.filter(door_password=int(d)) #db에서 동 비밀번호와 라즈베리파이 입력값을 비교하는 인증 코드
                if db_pass: # 만약 db 비밀번호가 일치하면
                    print("yes")
                    sendData = "yes1"
                    connectionSock.sendall(sendData.encode("utf-8"))
                else:
                    print("no")
                    sendData = "no1"
                    connectionSock.sendall(sendData.encode("utf-8"))
                connectionSock.sendall(sendData.encode("utf-8"))
            if '?' in recvData: #앱에서 동 비밀번호 입력시 서버 db와 비교해줌
                a = recvData.split("?")[1]
                print("a=" + a)
                d_pass = Apart.objects.filter(door_password=a) #서버db에서 동 비밀번호 인증
                if d_pass:
                    print("db_ok")
                    sendData = 'confirm' + '\n'
                    connectionSock.sendall(sendData.encode("utf-8"))
                    print("send")
                else:
                    print("db_np")
                    sendData = 'no' + '\n'
                    connectionSock.sendall(sendData.encode("utf-8"))
            if '/' in recvData: #앱에서 거주자 정보를 입력하여 서버 db와 비교해 인증한다/앱에서 자동 인증 절차
                b = recvData.split('/')
                ho1 = int(b[0])
                db = Apart.objects.filter(ho=ho1, name=b[1], phone=b[2], identification=b[3][:-2])#입력값은 호/이름/폰번호/주민번호 순으로 들어온다
                if '&' in b[3][:-1]:
                    print(raspberry)
                    con = raspberry['3333$\n'] #라즈베리파이에게 인증 확인을 보내줌
                    #print(type(con))
                    #print(con)
                    if db:
                        print("db ok1")
                        sendData = "yes1"
                        con.sendall(sendData.encode("utf-8"))
                        print("send ok")
                        sendData2 = "confirm\n"
                        connectionSock.sendall(sendData2.encode("utf-8"))
                    else:
                        print("db no1")
                        sendData2 = "nooooooo\n"
                        connectionSock.sendall(sendData2.encode("utf-8"))
                        #sendData ="no1"
                        # con.sendall(sendData.encode("utf-8"))
                elif '#' in b[3][:-1]:
                    if db:
                        print("db ok2")
                        p = Apart.objects.get(UUID__contains="@", name=b[1], identification=b[3][:-2]) #만약 인증이 성공하면 라즈베리파이 고유id인 UUID를 앱에 보내준다
                        p = p.UUID
                        sendData = 'confirm' + p + '\n'
                        print(sendData)
                        connectionSock.sendall(sendData.encode("utf-8"))
                    else:
                        print("db no2")
                        sendData = 'noooooo@1' + '\n'
                        sendData = 'noooooo@1' + '\n'
                        connectionSock.sendall(sendData.encode("utf-8"))
                # connectionSock.sendall(sendData.encode("utf-8"))
                        sendData = 'noooooo@1' + '\n'
                        connectionSock.sendall(sendData.encode("utf-8"))
                # connectionSock.sendall(sendData.encode("utf-8"))
        except socket.error as e:
            print('socket error:{}'.format(e))
            break
    connectionSock.close()


if __name__ == '__main__':
    echoserver(5001)

