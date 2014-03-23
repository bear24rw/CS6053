import socket
import SocketServer
import platform
import subprocess
from config import Config
from printer import Printer
from diffie_hellman import DHE
from karn import Karn

class tcp_handler(SocketServer.StreamRequestHandler):

    def setup(self):

        self.dhe = DHE()
        self.karn = None

        self.printer = Printer("server")

        SocketServer.StreamRequestHandler.setup(self)

    def finish(self):

        SocketServer.StreamRequestHandler.finish(self)

    def generate_response(self, line):

        line = line.strip()
        directive, args = [x.strip() for x in line.split(':', 1)]

        self.printer.directive(line)

        if directive == "REQUIRE":
            if args == "IDENT":
                if Config.use_encryption:
                    return "IDENT %s %s" % (Config.ident, self.dhe.public_key)
                else:
                    return "IDENT %s" % Config.ident
            elif args == "ALIVE":
                if Config.cookie == "":
                    self.printer.info("Alive request but we don't know the cookie!")
                else:
                    return "ALIVE %s" % Config.cookie
            elif args == "TRANSFER_RESPONSE":
                self.printer.info("Accepting transfer")
                return "TRANSFER_RESPONSE ACCEPT"
                #return "TRANSFER_RESPONSE DECLINE"
            elif args == "QUIT":
                return "QUIT"
            else:
                self.printer.error("Unknown require: " + line)
        elif directive == "RESULT":
            args = args.split(' ', 1)
            if args[0] == "IDENT":
                self.dhe.monitor_key(args[1])
                self.karn = Karn(self.dhe.secret)
                self.printer.info("Setup secret key")
            elif args[0] == "ALIVE" and args[1] == "Identity has been verified.":
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

        return None

    def handle(self):

        for line in self.rfile:

            # check if this line is encrypted
            if line.startswith('1a'):
                line = self.karn.decrypt(line)
                if line is None: continue

            # generate the response for this line
            response = self.generate_response(line)

            # if there is no response go get another line
            if response is None: continue

            # if we have a valid karn we can encrypt the response
            if self.karn:
                response = self.karn.encrypt(response)

            # add the newline and send the response
            response += '\n'
            self.printer.command(response)
            self.wfile.write(response)

if __name__ == "__main__":
    print "Starting server on %s:%s" % (Config.server_ip, Config.server_port)
    server = SocketServer.ThreadingTCPServer((Config.server_ip, Config.server_port), tcp_handler)
    server.serve_forever()
