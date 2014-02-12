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

    def __init__(self, who="", monitor_username=None):
        self.who = who
        self.monitor_username = monitor_username

    def directive(self, string):
        label = "monitor -> %s" % self.who
        self._print(string, label, self.GREEN)

    def command(self, string):
        label = "%s -> monitor" % self.who
        self._print(string, label, self.BLUE)

    def error(self, string):
        string = string.strip()
        label = self.who.center(17)
        self._print(string, label, self.RED)

    def info(self, string):
        string = string.strip()
        label = self.who.center(17)
        self._print(string, label, self.YELLOW)

    def _print(self, string, label, color):
        string = string.strip()
        if string == "": return
        if self.monitor_username is None:
            print "[" + label + "] " + color + string + self.DEFAULT
        else:
            print "[" + label + "] [" + self.monitor_username + "] " + color + string + self.DEFAULT
