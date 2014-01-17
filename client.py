import socket
from printer import Printer
from config import Config

def generate_response(line):

    line = line.strip()
    directive, args = [x.strip() for x in line.split(':', 1)]

    printer.directive(line)

    if directive == "REQUIRE":
        if args == "IDENT":
            return "IDENT %s\n" % Config.ident
        elif args == "PASSWORD":
            return "PASSWORD %s\n" % Config.password
        elif args == "ALIVE":
            if Config.cookie == "":
                printer.info("Alive request but we don't know the cookie!")
            else:
                return "ALIVE %s\n" % Config.cookie
        elif args == "HOST_PORT":
            return "HOST_PORT %s %s\n" % (Config.host_ip, Config.host_port)
        else:
            printer.error("Unknown require: " + line)
    elif directive == "RESULT":
        args = args.split(' ', 1)
        if args[0] == "PASSWORD":
            Config.cookie = args[1]
            printer.info("Got cookie: " + Config.cookie)
        elif args[0] == "HOST_PORT":
            printer.info("Login successful! (%s)" % args[1])
        if args[0] == "ALIVE" and args[1] == "Identity has been verified.":
            printer.info("Alive verified")
        else:
            printer.error("Unknown result: " + line)
    elif directive == "WAITING":
        pass
    elif directive == "COMMENT":
        pass
    elif directive == "COMMAND_ERROR":
        if "unable to connect to host" in args:
            return ""
        else:
            printer.error(line)
    else:
        printer.error("Unknown directive: " + line)

    return ""

if __name__ == "__main__":

    printer = Printer("client")

    sock = socket.create_connection((Config.monitor_ip, Config.monitor_port))

    for line in sock.makefile():
        response = generate_response(line)
        printer.command(response)
        sock.send(response)

    sock.close()
