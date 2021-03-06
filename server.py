import argparse
import sys
import socket
import SocketServer
import platform
import subprocess
from config import Config
from config import checksums
from printer import Printer
from diffie_hellman import DHE
from karn import Karn
from fiat import Verifier
import parse_log

class tcp_handler(SocketServer.StreamRequestHandler):

    def setup(self):

        self.dhe = DHE()
        self.karn = None
        self.verifier = Verifier()

        self.printer = Printer("server")

        self.exit = False

        SocketServer.StreamRequestHandler.setup(self)

    def finish(self):

        SocketServer.StreamRequestHandler.finish(self)

    def generate_response(self, line):

        line = line.strip()
        directive, args = [x.strip() for x in line.split(':', 1)]

        if directive == "REQUIRE":
            if args == "IDENT":
                return "IDENT %s %s" % (Config.ident, self.dhe.public_key)
            elif args == "ALIVE":
                return "ALIVE %s" % Config.cookie
            elif args == "ROUNDS":
                return "ROUNDS %s" % Config.num_rounds
            elif args == "SUBSET_A":
                return "SUBSET_A %s" % ' '.join(str(x) for x in self.verifier.subset_a)
            elif args == "TRANSFER_RESPONSE":
                if self.verifier.good():
                    return "TRANSFER_RESPONSE ACCEPT"
                else:
                    return "TRANSFER_RESPONSE DECLINE"
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
            elif args[0] == "SUBSET_K":
                self.verifier.subset_k = [int(x) for x in args[1].split()]
            elif args[0] == "SUBSET_J":
                self.verifier.subset_j = [int(x) for x in args[1].split()]
            elif args[0] == "PUBLIC_KEY":
                self.verifier.v = int(args[1].split()[0], 32)
                self.verifier.n = int(args[1].split()[1], 32)
            elif args[0] == "AUTHORIZE_SET":
                self.verifier.authorize_set = [int(x) for x in args[1].split()]
            else:
                self.printer.error("Unknown result")
        elif directive == "PARTICIPANT_PASSWORD_CHECKSUM":
            self.printer.info("Got checksum: %s" % args)
            if args not in checksums:
                self.printer.error("INVALID CHECKSUM")
                self.exit = True
                return ""
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
                self.printer.directive(line, encrypted=True)
            else:
                self.printer.directive(line)

            # generate the response for this line
            response = self.generate_response(line)

            # if there is no response go get another line
            if response is None: continue

            self.printer.command(response + '\n', encrypted=self.karn)

            # if we have a valid karn we can encrypt the response
            if self.karn:
                response = self.karn.encrypt(response)

            # add the newline and send the response
            response += '\n'
            self.wfile.write(response)

            if self.exit:
                self.printer.error("Exiting!")
                break

if __name__ == "__main__":

    parser = argparse.ArgumentParser()
    parser.add_argument('--ident', default="mtest16")
    parser.add_argument('--logfile', default="/home/httpd/html/final.log.8180")
    args = parser.parse_args()

    if args.ident not in Config.accounts:
        print "Invalid ident"
        sys.exit(1)

    Config.ident       = Config.accounts[args.ident].ident
    Config.server_port = Config.accounts[args.ident].port

    parse_log.parse(args.logfile)
    Config.cookie = parse_log.cookies[args.ident.lower()]
    Config.password = parse_log.passes[args.ident.lower()]
    print "Using cookie: '%s'" % Config.cookie

    print "Starting server on %s:%s" % (Config.server_ip, Config.server_port)
    server = SocketServer.ThreadingTCPServer((Config.server_ip, Config.server_port), tcp_handler)
    server.serve_forever()
