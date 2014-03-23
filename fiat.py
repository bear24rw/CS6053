import random
from config import Config

class Prover:

    '''
    http://pages.swcp.com/~mccurley/talks/msri2/node24.html
    http://friedo.szm.com/krypto/AC/ch21/21-01.html
    '''

    p = 18446744073709551253
    q = 18446744073709551557
    n = p * q

    def __init__(self):

        self.s = random.randrange(2<<64)
        self.v = pow(self.s, 2, self.n)
        self.subset_a = []
        self.r_set = []
        self.rounds = 10

    def authorize_set(self):
        # generate the random set
        self.r_set = [random.randrange(2<<64) for _ in range(self.rounds)]
        # calculate the authorize set
        auth_set = [pow(r, 2, self.n) for r in self.r_set]
        # return it as a string
        return ' '.join(str(x) for x in auth_set)

    def subset_k(self):
        k = [(self.s * self.r_set[i]) % self.n for i in self.subset_a]
        return ' '.join(str(x) for x in k)

    def subset_j(self):
        j = [self.r_set[i] % self.n for i in set(range(self.rounds)) - set(self.subset_a)]
        return ' '.join(str(x) for x in j)


class Verifier:

    def __init__(self):

        self.rounds = Config.num_rounds
        self.v = 0
        self.n = 0

        self.authorize_set = []
        self.subset_j = []
        self.subset_k = []

        self.subset_a = sorted(random.sample(range(self.rounds), self.rounds // 2))

    def good(self):

        if not self.authorize_set: print "auth set empty!!!!!!!!!"
        if not self.subset_j: print "subset j empty!!!!!!!"
        if not self.subset_k: print "subset k empty!!!!!!!"

        # check that the public key is our own
        if self.n != Prover.n:
            print "public keys dont match!"
            return False

        j = iter(self.subset_j)
        k = iter(self.subset_k)
        for i in range(self.rounds):
            if i in self.subset_a:
                if pow(next(k), 2, self.n) != (self.v * self.authorize_set[i]) % self.n:
                    return False
            elif pow(next(j), 2, self.n) != self.authorize_set[i]:
                return False

        return True

