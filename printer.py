class Color:
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

def print_directive(string):
    string = string.strip()
    if string == "": return
    print Color.GREEN + string + Color.DEFAULT

def print_command(string):
    string = string.strip()
    if string == "": return
    print Color.BLUE + string + Color.DEFAULT

def print_error(string):
    string = string.strip()
    print Color.RED + string + Color.DEFAULT

def print_info(string):
    string = string.strip()
    print Color.YELLOW + string + Color.DEFAULT

