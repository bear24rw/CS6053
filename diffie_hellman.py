from Crypto.Random.random import getrandbits

class DHE:

    def __init__(self, key=None):

        self.p = 0x96C99B60C4F823707B47A848472345230C5B25103DC37412A701833E8FF5C567A53A41D0B37B10F0060D50F4131C57CF1FD11B6A6CB958F36B1E7D878A4C4BC7
        self.g = 0x2C900DF142E2B839E521725585A92DC0C45D6702A48004A917F74B73DB26391F20AEAE4C6797DD5ABFF0BFCAECB29554248233B5E6682CE1C73DD2148DED76C3

        """
        Generate a new private key if it wasn't given
        """
        if (key):
            self.private_key = int(key, 32)
        else:
            self.private_key = getrandbits(512)

        """
        Public Key = g**x % p
        """
        self.public_key = self._baseN(pow(self.g, self.private_key, self.p), 32)


    def monitor_key(self, key):
        """
        Takes in the monitors public key in Base 32
        """
        self.secret = pow(int(key,32), self.private_key, self.p)

    def _baseN(self, num,b,numerals="0123456789abcdefghijklmnopqrstuvwxyz"):
        """
        Return 'num' in base 'b'
        http://stackoverflow.com/a/2267428
        """
        return ((num == 0) and numerals[0]) or (self._baseN(num // b, b, numerals).lstrip(numerals[0]) + numerals[num % b])
