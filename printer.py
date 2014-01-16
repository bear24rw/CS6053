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

    def directive(self, string):
        string = string.strip()
        if string == "": return
        label = "[monitor -> %s] " % self.who
        self._print(string, label, self.GREEN)

    def command(self, string):
        string = string.strip()
        if string == "": return
        label = "[%s -> monitor] " % self.who
        self._print(string, label, self.BLUE)

    def error(self, string):
        string = string.strip()
        label = "[%s] " % self.who.center(17)
        self._print(string, label, self.RED)

    def info(self, string):
        string = string.strip()
        label = "[%s] " % self.who.center(17)
        self._print(string, label, self.YELLOW)

    def _print(self, string, label, color):
        print "[" + label + "] " + color + string + self.DEFAULT
