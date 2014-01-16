from client import Client
from server import Server

server_thread = Server()
client_thread = Client()

server_thread.start()
client_thread.start()
