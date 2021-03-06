import sys
import socket
import argparse
from printer import Printer
from config import Config
from diffie_hellman import DHE
from karn import Karn
from fiat import Prover
from base32 import base32
import parse_log

config = Config()
printer = Printer("client")
dhe = DHE()
prover = Prover()
authenticated = False
karn = None
transfer = None
exit = False
only_do_alive = False

def generate_response(line):
    global karn
    global authenticated
    global transfer
    global exit

    line = line.strip()
    directive, args = [x.strip() for x in line.split(':', 1)]

    if directive == "REQUIRE":
        if args == "IDENT":             return "IDENT %s %s" % (Config.ident, dhe.public_key)
        elif args == "PASSWORD":        return "PASSWORD %s" % Config.password
        elif args == "ALIVE":           return "ALIVE %s" % Config.cookie
        elif args == "HOST_PORT":       return "HOST_PORT %s %s" % (Config.server_ip, Config.server_port)
        elif args == "PUBLIC_KEY":      return "PUBLIC_KEY %s %s" % (base32(prover.v), base32(prover.n))
        elif args == "AUTHORIZE_SET":   return "AUTHORIZE_SET %s" % prover.authorize_set()
        elif args == "SUBSET_J":        return "SUBSET_J %s" % prover.subset_j()
        elif args == "SUBSET_K":        return "SUBSET_K %s" % prover.subset_k()
        else:                           printer.error("Unknown require: " + line)
    elif directive == "RESULT":
        args = args.split(' ', 1)
        if args[0] == "IDENT":
            dhe.monitor_key(args[1])
            karn = Karn(dhe.secret)
            printer.info("Setup secret key")
        elif args[0] == "PASSWORD":
            Config.cookie = args[1]
            printer.info("Got cookie: " + Config.cookie)
        elif args[0] == "HOST_PORT":
            printer.info("Login successful! (%s)" % args[1])
            if transfer is None and not manual_mode:
                exit = True
                return ""
        elif args[0] == "ALIVE" and args[1] == "Identity has been verified.":
            printer.info("Alive verified")
            authenticated = True
            if only_do_alive:
                exit = True
                return ""
        elif args[0] == "TRANSFER_REQUEST":
            printer.info("Transfer was %s" % args[1])
        elif args[0] == "ROUNDS":
            prover.rounds = int(args[1])
        elif args[0] == "SUBSET_A":
            prover.subset_a = [int(x) for x in args[1].split()]
        elif args[0] == "TRANSFER_RESPONSE" and not manual_mode:
            exit = True
            return ""
        else:
            printer.error("Unknown result: %s (args: %r)" % (line, args))
    elif directive == "WAITING":
        if transfer and authenticated:
            rt = "TRANSFER_REQUEST %s %s FROM %s\n" % tuple(transfer)
            transfer = None
            return rt
        if manual_mode and authenticated:
            return raw_input('Command: ')
    elif directive == "COMMENT":
        pass
    elif directive == "COMMAND_ERROR":
        exit = True
        printer.error(line)
        return ""
    else:
        exit = True
        printer.error("Unknown directive: " + line)
        return ""

    return None

if __name__ == "__main__":

    parser = argparse.ArgumentParser()
    parser.add_argument('--ident', default="mtest16")
    parser.add_argument('--transfer', nargs=3, metavar=('TO', 'AMOUNT', 'FROM'))
    parser.add_argument('--manual', action='store_true')
    parser.add_argument('--alive', action='store_true')
    parser.add_argument('--logfile', default="/home/httpd/html/final.log.8180")
    args = parser.parse_args()

    manual_mode = args.manual
    transfer = args.transfer
    only_do_alive = args.alive

    if args.ident not in Config.accounts:
        print "Invalid ident"
        sys.exit(1)

    Config.ident = args.ident
    Config.server_port = Config.accounts[args.ident].port

    parse_log.parse(args.logfile)
    Config.cookie = parse_log.cookies[args.ident.lower()]
    Config.password = parse_log.passes[args.ident.lower()]

    print "Using cookie: '%s'" % Config.cookie

    sock = socket.create_connection((Config.monitor_ip, Config.monitor_port))

    for line in sock.makefile():

        # check if this line is encrypted
        if line.startswith('1a'):
            line = karn.decrypt(line)
            if line is None: continue
            printer.directive(line, encrypted=True)
        else:
            printer.directive(line)

        # generate the response for this line
        response = generate_response(line)

        # if there is no response go get another line
        if response is None: continue

        printer.command(response + '\n', encrypted=karn)

        # if we have a valid karn we can encrypt the response
        if karn:
            response = karn.encrypt(response)

        # add the newline and send the response
        response += '\n'
        sock.send(response)

        if exit: break

    sock.close()
