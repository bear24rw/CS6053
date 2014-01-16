import socket
import threading
from printer import Printer
from config import Config

class Client(threading.Thread):

    def __init__(self):
        threading.Thread.__init__(self)
        self.printer = Printer("client")

    def run(self):

        sock = socket.create_connection((Config.monitor_ip, Config.monitor_port))
        for line in sock.makefile():
            response = self.generate_response(line)
            self.printer.command(response)
            sock.send(response)

        sock.close()

    def generate_response(self, line):

        line = line.strip()
        directive, args = [x.strip() for x in line.split(':', 1)]

        self.printer.directive(line)

        if directive == "REQUIRE":
            if args == "IDENT":
                return "IDENT %s\n" % Config.ident
            elif args == "PASSWORD":
                return "PASSWORD %s\n" % Config.password
            elif args == "ALIVE":
                if Config.cookie == "":
                    self.printer.info("Alive request but we don't know the cookie!")
                else:
                    return "ALIVE %s\n" % Config.cookie
            elif args == "HOST_PORT":
                return "HOST_PORT %s %s\n" % (Config.host_ip, Config.host_port)
            else:
                self.printer.error("Unknown require: " + line)
        elif directive == "RESULT":
            args = args.split(' ', 1)
            if args[0] == "PASSWORD":
                Config.cookie = args[1]
                self.printer.info("Got cookie: " + Config.cookie)
            elif args[0] == "HOST_PORT":
                self.printer.info("Login successful! (%s)" % args[1])
            else:
                self.printer.error("Unknown result: " + line)
        elif directive == "WAITING":
            pass
        elif directive == "COMMENT":
            pass
        elif directive == "COMMAND_ERROR":
            if "unable to connect to host" in args:
                return ""
            else:
                self.printer.error(line)
        else:
            self.printer.error("Unknown directive: " + line)

        return ""

