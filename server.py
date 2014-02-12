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


        """
        Figure out if this connection is from the real monitor or not
        """

        client_ip, client_port = self.client_address

        # make sure this connection is even coming from the monitor
        if client_ip == Config.monitor_ip:

            # get the list of all open file descriptors
            # find the one that has the client port open
            # get the name of the user who owns it
            cmd = "fstat | grep %s | cut -d' ' -f1" % client_port

            # if we're not running on the same box as monitor we need to ssh
            if Config.server_ip != Config.monitor_ip:
                cmd = "ssh -i id_rsa stackattack@helios.ececs.uc.edu " + cmd

            ps = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
            username = ps.communicate()[0].strip()

            if username == "franco":
                self.real_monitor = True
            else:
                self.real_monitor = False

            self.printer = Printer("server", monitor_username=username)

        else:

            self.printer = Printer("server", monitor_username="UNKNOWN")
            self.printer.error("CONNECTION NOT FROM HELIOS!")
            self.real_monitor = False

        if not self.real_monitor:
            # TODO: terminate connection / honeypot
            pass

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
