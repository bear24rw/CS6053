import socket
from printer import Printer
from config import Config
from diffie_hellman import DHE
from karn import Karn

printer = Printer("client")
dhe = DHE()
authenticated = False
karn = None

def generate_response(line):
    global karn
    global authenticated

    line = line.strip()
    directive, args = [x.strip() for x in line.split(':', 1)]

    printer.directive(line)

    if directive == "REQUIRE":
        if args == "IDENT":
            if Config.use_encryption:
                return "IDENT %s %s" % (Config.ident, dhe.public_key)
            else:
                return "IDENT %s" % Config.ident
        elif args == "PASSWORD":
            return "PASSWORD %s" % Config.password
        elif args == "ALIVE":
            if Config.cookie == "":
                printer.info("Alive request but we don't know the cookie!")
            else:
                return "ALIVE %s" % Config.cookie
        elif args == "HOST_PORT":
            return "HOST_PORT %s %s" % (Config.host_ip, Config.host_port)
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
        else:
            printer.error("Unknown result: %s (args: %r)" % (line, args))
    elif directive == "WAITING":
        if authenticated:
            return raw_input('Command: ')
    elif directive == "COMMENT":
        pass
    elif directive == "COMMAND_ERROR":
        if "unable to connect to host" in args:
            return None
        else:
            printer.error(line)
    else:
        printer.error("Unknown directive: " + line)

    return None

if __name__ == "__main__":

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
