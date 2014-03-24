import hashlib
import socket

checksums = []

class Account:
    def __init__(self, ident, password, cookie, port):
        global checksums
        self.ident = ident
        self.password = password
        self.cookie = cookie
        self.port = port
        checksums.append(hashlib.sha1(password).hexdigest())

class Config:
    monitor_dns = "gauss.ececs.uc.edu"
    monitor_ip = socket.gethostbyname(monitor_dns)
    monitor_port = 8180
    server_ip = socket.gethostbyname(socket.getfqdn())
    server_port = 20167
    accounts = {
            "EDSNOWDEN":    Account("EDSNOWDEN", "8Y6]%D[:T%2!.P^$6AGDEHMCZICN;OOX-]UOJU=^G-", "NOZHR7CN56SZTQW8O90", 20168),
            "GROUND_WATER": Account("GROUND_WATER", "Y9JUY8A`L?+;RK9O$;SWRLO_M'I-[9MP)IK=*]4-E!", "EVSKI5L7EFQOZUYMM6R", 20169),
            "CORNHOLIO":    Account("CORNHOLIO", "-S_:9J7R9X*/&7UDL'SX-'2?R=FOW2!T'I8.Q7F?PA", "RQ4ZRZ67IXIZN5MIW89", 20120),
            "mtest16":      Account("mtest16", "12345", "C5K8GNM22KQ5XCFKVHF", 11121),
            "mtest17":      Account("mtest17", "12345", "J98Q82H1C458X6YAKAI", 11122),
            "mtest18":      Account("mtest18", "12345", "MGYKSTOKL4T106N0175", 11123),
    }
    """
    ALTIUS BA5WKLFGG6XA1GFCI0
    IDENT R6LA4UC2RNBZRJH7HJK
    PASSWORD HZEXPZ9035G0PO0YCO7
    ALIVE Q3Z2RZANB1CEJDFBS3
    """

    ident = ""
    password = ""
    cookie = ""

    num_rounds = 5
