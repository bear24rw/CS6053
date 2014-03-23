def base32(num):
    return _baseN(num, 32)

def _baseN(num, b, numerals="0123456789abcdefghijklmnopqrstuvwxyz"):
    """
    Return 'num' in base 'b'
    http://stackoverflow.com/a/2267428
    """
    return ((num == 0) and numerals[0]) or (_baseN(num // b, b, numerals).lstrip(numerals[0]) + numerals[num % b])
