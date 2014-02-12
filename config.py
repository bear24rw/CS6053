import socket

class Config:
    monitor_dns = "helios.ececs.uc.edu"
    monitor_ip = socket.gethostbyname(monitor_dns)
    monitor_port = 8180
    ident = "mtest16"
    password = "12345"
    cookie = "C5K8GNM22KQ5XCFKVHF"
    server_ip = socket.gethostbyname(socket.getfqdn())
    server_port = 35201
    use_encryption = True
