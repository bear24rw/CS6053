from hashlib import sha1
from itertools import izip_longest
import struct
import base64
import string
from printer import Printer

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

        # java BigInteger.toByteArray() is signed so it pads if MSB is 1
        # http://stackoverflow.com/a/8544521/253650
        if key[0] & (1<<7):
            key = bytearray([0]) + key

        # Split key into two halfs
        # Monitor drops last byte if len is odd (monitor/Cipher.java:87)
        self.key_left  = key[:len(key)/2]
        self.key_right = key[len(key)/2:(len(key)/2)*2]

        self.printer = Printer("karn")

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

    def decrypt(self, message):

        # Convert the message from base 32 to hex
        message = '%x' % int(message, 32)
        if len(message) & 1:
            message = '0' + message

        # Convert the hex string to a byte array
        message = bytearray.fromhex(message)

        # If the first byte isn't the guard byte we are done
        if message[0] != GUARD_BYTE:
            self.printer.error("Did not find guard_byte!")
            return None

        # Remove the guard byte
        message = message[1:]

        output = bytearray()

        # Break message into blocks and process each one
        for block in self._grouper(message, BLOCK_SIZE, '\0'):

            # Convert the block into an array of bytes
            block = bytearray(block)

            # Divide block into left and right half
            block_left  = block[:BLOCK_SIZE/2]
            block_right = block[BLOCK_SIZE/2:]

            # Find digest of cipher right and key right
            digest = sha1(block_right + self.key_right).digest()

            # XOR the digest with cipher left
            text_left = bytearray([ord(d)^b for d,b in zip(digest, block_left)])

            # Find the digest of text left and key left
            digest = sha1(text_left + self.key_left).digest()

            # XOR the digest with cipher right
            text_right = bytearray([ord(d)^b for d,b in zip(digest, block_right)])

            output += text_left
            output += text_right

        # Remove the padding
        output = str(output).split('\0')[0]

        # Make sure the plaintext is normal printable text
        if not all(x in string.printable for x in output):
            self.printer.error("Unable to decrypt: %r" % output)
            return None

        return output

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

    text = "this is a test"
    key = 123456789
    enc =  "1avfbcuej96oo1m2vc04rnlin6rpurc2pu7ac8h42dhr8l13ahdfcbev3a5sj74o85"

    k = Karn(key)

    print "--- Testing Correct ---"
    e = k.encrypt(text)
    assert e == enc
    d = k.decrypt(e)
    assert d == text

    print "--- Testing No Guard Byte ---"
    e = '0' + enc[1:]
    d = k.decrypt(e)
    assert d == None

    print "--- Testing Corrupted Message ---"
    e = enc[:10] + "00000" + enc[15:]
    d = k.decrypt(e)
    assert d == None

    print "--- Done ---"
