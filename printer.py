from config import Config

class Printer:

    BOLD    = '\033[1m'
    BLACK   = '\033[30m'
    RED     = '\033[31m'
    GREEN   = '\033[32m'
    YELLOW  = '\033[33m'
    BLUE    = '\033[34m'
    PURPLE  = '\033[35m'
    CYAN    = '\033[36m'
    WHITE   = '\033[37m'
    DEFAULT = '\033[0m'

    def __init__(self, who=""):
        self.who = who

    def directive(self, string, encrypted=False):
        if encrypted:
            label = "<E<"
        else:
            label = "<<<"
        self._print(string, label, self.GREEN)

    def command(self, string, encrypted=False):
        if encrypted:
            label = ">E>"
        else:
            label = ">>>"
        self._print(string, label, self.BLUE)

    def error(self, string):
        string = string.strip()
        self._print(string, "---", self.RED)

    def info(self, string):
        string = string.strip()
        self._print(string, "---", self.YELLOW)

    def _print(self, string, label, color):
        string = string.strip()
        if string == "": return
        print "[" + Config.ident + "] " + label + "  " + color + string + self.DEFAULT
