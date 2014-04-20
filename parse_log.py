"""
Parses the monitor log and builds a dictionary mapping
player names to their most recent cookie value.
"""

import sys

passes = {}
cookies = {}

def parse(filename=None):
    global passes
    global cookies

    player = None
    possible_cookie = None

    f = open(filename)

    for line in f:

        if line.startswith("Player "):
            """
            if possible_cookie and player:
                cookies[player] = possible_cookie
                #print "Cookie for %s: %s" % (player, possible_cookie)
            """
            player = line.split()[1].lower()
            continue

        if player is None: continue

        if ">>>change_password" in line.lower():
            pw = line.split()[-1]
            passes[player] = pw
            #print "Password for %s: %s" % (player, pw)
            continue

        if ">>>alive" in line.lower():
            possible_cookie = line.split()[-1]
            continue

        if "invalid monitor password" in line.lower():
            possible_cookie = None
            continue

        if ">>>quit" in line.lower() and possible_cookie is not None:
            cookies[player] = possible_cookie
            #print "Cookie for %s: %s" % (player, possible_cookie)
            continue
