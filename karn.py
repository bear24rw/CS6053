from hashlib import sha1
from itertools import izip_longest
import struct
import base64

BLOCK_SIZE = 40
GUARD_BYTE = 42

class Karn:

    def __init__(self, key):

        # Convert the integer key into a hex string
        # If the length of it is odd we need to left pad a '0'
        key_hex = '%x' % key
        if len(key_hex) & 1:
            key_hex = '0' + key_hex

        # Convert the hex key string into byte array
        key = bytearray.fromhex(key_hex)

        # Split key into two halfs
        self.key_left  = key[:len(key)/2]
        self.key_right = key[len(key)/2:]

    def encrypt(self, message):

        # Start the output with the guard byte
        output = bytearray([GUARD_BYTE])

        # Break message into blocks and process each one
        for block in self._grouper(message, BLOCK_SIZE, '\0'):

            # Convert the block into an array of bytes
            block = bytearray(block)

            # Divide block into left and right half
            block_left  = block[:BLOCK_SIZE/2]
            block_right = block[BLOCK_SIZE/2:]

            # Hash the left plaintext plus the left key
            digest = sha1(block_left + self.key_left).digest()

            # XOR the digest with the right plaintext
            cipher_right = bytearray([ord(d)^b for d,b in zip(digest, block_right)])

            # Hash the right cipher plus the right key
            digest = sha1(cipher_right + self.key_right).digest()

            # XOR the digest with the left plaintext
            cipher_left = bytearray([ord(d)^b for d,b in zip(digest, block_left)])

            output += cipher_left
            output += cipher_right

        # Convert output to a hex string
        output = '0x'+''.join('%02X' % x for x in output)

        # Convert the hex string to an integer and then conver to base 32
        return self._baseN(int(output, 16), 32)

    def _grouper(self, iterable, n, fillvalue=None):
        """
        Collect data into fixed-length chunks or blocks
        grouper('ABCDEFG', 3, 'x') --> ABC DEF Gxx
        http://docs.python.org/2/library/itertools.html#recipes
        """
        args = [iter(iterable)] * n
        return izip_longest(fillvalue=fillvalue, *args)

    def _baseN(self, num,b,numerals="0123456789abcdefghijklmnopqrstuvwxyz"):
        """
        Return 'num' in base 'b'
        http://stackoverflow.com/a/2267428
        """
        return ((num == 0) and numerals[0]) or (self._baseN(num // b, b, numerals).lstrip(numerals[0]) + numerals[num % b])

if __name__ == "__main__":
    k = Karn(123456789)
    print k.encrypt("this is a test")
