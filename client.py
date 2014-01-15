import socket
from printer import *

class Config:
    monitor_ip = "helios.ececs.uc.edu"
    monitor_port = 8180
    ident = "mtest1"
    password = "12345"
    cookie = ""
    host_ip = ""
    host_port = 0

def generate_response(line):

    line = line.strip()
    directive, args = [x.strip() for x in line.split(':', 1)]

    print_directive(line)

    if directive == "REQUIRE":
        if args == "IDENT":
            return "IDENT %s\n" % Config.ident
        elif args == "PASSWORD":
            return "PASSWORD %s\n" % Config.password
        elif args == "ALIVE":
            if Config.cookie == "":
                print_info("Alive request but we don't know the cookie!")
                return ""
                return "HOST_PORT google.com 80"
                return "ALIVE 234\n"
                return "PASSWORD %s\n" % Config.password
                return "QUIT\n"
            else:
                return "ALIVE %s\n" % Config.cookie
        else:
            print_error("Unknown require: " + line)
    elif directive == "RESULT":
        args = args.split()
        if args[0] == "PASSWORD":
            Config.cookie = args[1]
            print_info("Got cookie: " + Config.cookie)
        else:
            print_error("Unknown result: " + line)
    elif directive == "WAITING":
        pass
    elif directive == "COMMENT":
        pass
    elif directive == "COMMAND_ERROR":
        print_error(line)
        return "QUIT\n"
    else:
        print_error("Unknown directive: " + line)

    return ""

if __name__ == "__main__":

    sock = socket.create_connection((Config.monitor_ip, Config.monitor_port))
    for line in sock.makefile():
        response = generate_response(line)
        print_command(response)
        sock.send(response)

    sock.close()
