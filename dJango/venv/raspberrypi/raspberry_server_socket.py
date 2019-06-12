import socket, time
import serial

def test():
    input_pwd = input("password: ")
    return input_pwd

def pwd_ok():
    print("pwd_ok")
    s = serial.Serial('/dev/ttyACM0', 9600)
    s.flushInput()
    ok = "ok"
    s.write(ok.encode())
    read_pwd = s.readline()
    return read_pwd[:-2]

def run_client(host='18.220.63.77', port=5001):
    with socket.socket() as sock:
        sock.connect((host, port))
        data = "3333" + "$\n"
        print("data")
        sock.sendall(data.encode('utf-8'))
        print("send")
        while True:
            if input()=="*":
               data = test() + "$"
               print(data)
               sock.sendall(data.encode('utf-8'))
               print(data)
               res = sock.recv(1024)
               pwd = res.decode('utf-8')
               print(pwd)
               if (pwd == 'yes1'):
                  print("password ok")
                  pwd_ok()
               elif (pwd == 'no1'):
                  print("FAIL!!!")

if __name__ == '__main__':
    run_client()

