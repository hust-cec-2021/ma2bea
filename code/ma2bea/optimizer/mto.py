from .util import *
from .operators import *
from .sto import STO
from tqdm import trange
from scipy.optimize import OptimizeResult

class MTO:

    def __init__(self, functions, args):
        # extract parameters
        K = len(functions)

        # initialize STOs
        self.stos = [STO(functions[k], k, K, args) for k in range(K)]
        self.K = K
        self.T = args.num_iter
        self.args = args

    def optimize(self, callback=None):
        T, K, rmp = self.args.num_iter, self.K, self.args.rmp
        iterator = trange(T)
        for t in iterator:
            order = np.random.permutation(K)
            for k in order:
                self.stos[k].step(self.stos)
            y = self.get_min_fitness()

            desc = 'gen:{} rmp:{} mean:{} std:{} min:{} max:{}'.format(
                t, rmp, np.mean(y), np.std(y), np.min(y), np.max(y))
            iterator.set_description(desc)
            results = self.get_results(t)
            if callback and t % self.args.log_iter == 0:
                callback(results)

    def get_min_fitness(self):
        # extract parameters
        K = self.K
        y = []
        for k in range(K):
            y.append(np.min(self.stos[k].fitness))
        return y

    def get_results(self, t):
        K = self.K
        N = self.args.pop_size
        results = []
        for k in range(K):
            result = OptimizeResult()
            x, fun = self.stos[k].population[0], self.stos[k].fitness[0]
            result.x = x
            result.fun = fun
            result.nit = t
            result.nfev = (t + 1) * N
            result.pair = self.stos[k].pair
            result.ucb_value = self.stos[k].ucb.value
            results.append(result)
        return results
