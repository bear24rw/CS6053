import socket
import argparse
from printer import Printer
from config import Config
from diffie_hellman import DHE
from karn import Karn
from fiat import Prover

config = Config()
printer = Printer("client")
dhe = DHE()
prover = Prover()
authenticated = False
karn = None
transfer = None

def generate_response(line):
    global karn
    global authenticated
    global transfer

    line = line.strip()
    directive, args = [x.strip() for x in line.split(':', 1)]

    printer.directive(line)

    if directive == "REQUIRE":
        if args == "IDENT":             return "IDENT %s %s" % (Config.ident, dhe.public_key)
        elif args == "PASSWORD":        return "PASSWORD %s" % Config.password
        elif args == "ALIVE":           return "ALIVE %s" % Config.cookie
        elif args == "HOST_PORT":       return "HOST_PORT %s %s" % (Config.server_ip, Config.server_port)
        elif args == "PUBLIC_KEY":      return "PUBLIC_KEY %s %s" % (base32(prover.v), base32(prover.n))
        elif args == "AUTHORIZE_SET":   return "AUTHORIZE_SET %s" % prover.authorize_set()
        elif args == "SUBSET_J":        return "SUBSET_J %s" % prover.subset_j()
        elif args == "SUBSET_K":        return "SUBSET_K %s" % prover.subset_k()
        else:
            printer.error("Unknown require: " + line)
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
        elif args[0] == "ALIVE" and args[1] == "Identity has been verified.":
            printer.info("Alive verified")
            authenticated = True
        elif args[0] == "TRANSFER_REQUEST":
            printer.info("Transfer was %s" % args[1])
        elif args[0] == "ROUNDS":
            prover.rounds = int(args[1])
        elif args[0] == "SUBSET_A":
            prover.subset_a = [int(x) for x in args[1:]]
        else:
            printer.error("Unknown result: %s (args: %r)" % (line, args))
    elif directive == "WAITING":
        if not authenticated:
            printer.info("Waiting but not authenticated!")
            return None
        if transfer:
            rt = "TRANSFER_REQUEST %s %s FROM %s\n" % tuple(transfer)
            transfer = None
            return rt
        if manual_mode:
            return raw_input('Command: ')
    elif directive == "COMMENT":
        pass
    elif directive == "COMMAND_ERROR":
        if "unable to connect to host" in args:
            return None
        elif "TRANSFER_REQUEST REJECTED-Lack of points" in args:
            printer.info("Transfer was reject - lack of points")
        else:
            printer.error(line)
    else:
        printer.error("Unknown directive: " + line)

    return None

if __name__ == "__main__":

    parser = argparse.ArgumentParser()
    parser.add_argument('--ident', default=Config.ident)
    parser.add_argument('--transfer', nargs=3, metavar=('TO', 'AMOUNT', 'FROM'))
    parser.add_argument('--manual', action='store_true')
    args = parser.parse_args()

    manual_mode = args.manual
    transfer = args.transfer

    if args.ident:
        if args.ident not in Config.accounts:
            print "Invalid ident"
            sys.exit(1)
        Config.ident       = Config.accounts[args.ident].ident
        Config.password    = Config.accounts[args.ident].password
        Config.cookie      = Config.accounts[args.ident].cookie
        Config.server_port = Config.accounts[args.ident].port
    else:
        print "Using test ident"

    sock = socket.create_connection((Config.monitor_ip, Config.monitor_port))

    for line in sock.makefile():

        # check if this line is encrypted
        if line.startswith('1a'):
            line = karn.decrypt(line)
            if line is None: continue

        # generate the response for this line
        response = generate_response(line)

        # if there is no response go get another line
        if response is None: continue

        # if we have a valid karn we can encrypt the response
        if karn:
            response = karn.encrypt(response)

        # add the newline and send the response
        response += '\n'
        printer.command(response)
        sock.send(response)

    sock.close()
