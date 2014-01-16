import threading
import SocketServer
from config import Config
from printer import Printer

class tcp_handler(SocketServer.StreamRequestHandler):

    def setup(self):

        self.printer = Printer("server")
        SocketServer.StreamRequestHandler.setup(self)

    def generate_response(self, line):

        line = line.strip()
        directive, args = [x.strip() for x in line.split(':', 1)]

        self.printer.directive(line)

        if directive == "REQUIRE":
            if args == "IDENT":
                return "IDENT %s\n" % Config.ident
            elif args == "ALIVE":
                if Config.cookie == "":
                    self.printer.info("Alive request but we don't know the cookie!")
                else:
                    return "ALIVE %s\n" % Config.cookie
            elif args == "QUIT":
                return "QUIT\n"
            else:
                self.printer.error("Unknown require: " + line)
        elif directive == "RESULT":
            args = args.split(' ', 1)
            if args[0] == "ALIVE" and args[1] == "Identity has been verified.":
                self.printer.info("Alive verified")
            elif args[0] == "QUIT":
                self.printer.info("server has quit")
            else:
                self.printer.error("Unknown result")
        elif directive == "PARTICIPANT_PASSWORD_CHECKSUM":
            self.printer.info("Got checksum: %s" % args)
        elif directive == "WAITING":
            pass
        elif directive == "COMMENT":
            pass
        else:
            self.printer.error("Unknown directive: " + line)

        return ""

    def handle(self):

        for line in self.rfile:
            response = self.generate_response(line)
            self.printer.command(response)
            self.wfile.write(response)

class Server(threading.Thread):

    def __init__(self):
        threading.Thread.__init__(self)

    def run(self):
        server = SocketServer.ThreadingTCPServer((Config.host_ip, Config.host_port), tcp_handler)
        server.serve_forever()

