from .util import *
from .operators import *
from mab import UCB

class STO:

    def __init__(self, function, k, K, args):
        # save problem
        self.function = function
        # save parameter
        self.args = args
        self.k    = k
        self.K    = K
        # extract parameters
        N, D, T, sbxdi, pmdi, pswap, rmp = self.extract_parameters()
        # initialize
        self.population = np.random.rand(2 * N, D)
        self.fitness = np.full([2 * N], np.inf)
        # evaluate
        for i in range(2 * N):
            self.fitness[i] = self.function(self.population[i])
        # sort
        sort_index      = np.argsort(self.fitness)
        self.population = self.population[sort_index]
        self.fitness    = self.fitness[sort_index]
        # initialize ucb
        self.ucb = UCB(k, K)
        self.lb  = np.min(self.fitness)
        self.ub  = np.max(self.fitness)
        self.pair = np.zeros(K)

    def update_fitness_bound(self):
        self.lb  = np.min(self.fitness)
        self.ub  = np.max(self.fitness)

    def get_reward(self, y): # y: fitness of an individual
        if y < self.ub:
            return 1
        else:
            return 0

    def extract_parameters(self):
        # extract parameters
        args = self.args
        N = args.pop_size
        D = args.dimension
        T = args.num_iter
        sbxdi = args.sbxdi
        pmdi  = args.pmdi
        pswap = args.pswap
        rmp   = args.rmp
        return N, D, T, sbxdi, pmdi, pswap, rmp

    def step(self, stos):
        # extract parameters
        N, D, T, sbxdi, pmdi, pswap, rmp = self.extract_parameters()
        population, fitness = self.population, self.fitness


        # permute current population
        permutation_index = np.random.permutation(N)
        population[:N]    = population[:N][permutation_index]
        fitness[:N]       = fitness[:N][permutation_index]

        # update fitness bound
        self.update_fitness_bound()
        self.pair = np.zeros(self.K)

        # select pair to crossover
        for i in range(N):
            flag = False
            p1 = population[i]
            if np.random.rand() < rmp / 2:
                # choose p2
                sf2 = self.ucb.select_action()
                p2  = stos[sf2].choose_random_ind()
                c1, c2 = sbx_crossover(p1, p2, sbxdi)
                c1 = mutate(c1, pmdi)
                c2 = mutate(c2, pmdi)
                if np.random.rand() < 0.5:
                    c = c1
                else:
                    c = c2
                flag = True
                # record pair
                self.pair[sf2] += 1
            else:
                # choose p2
                p2 = self.choose_random_ind()
                c1, c2 = sbx_crossover(p1, p2, sbxdi)
                c1 = mutate(c1, pmdi)
                c2 = mutate(c2, pmdi)
                c1, c2 = variable_swap(c1, c2, pswap)
                if np.random.rand() < 0.5:
                    c = c1
                else:
                    c = c2
            # evaluate
            y = self.function(c)
            # store
            population[N + i, :] = c[:]
            fitness[N + i]         = y

            if flag:
                reward = self.get_reward(y)
                self.ucb.update_estimate(sf2, reward)

        # sort
        sort_index = np.argsort(fitness)
        population = population[sort_index]
        fitness    = fitness[sort_index]

        # commit population and fitness
        self.population = population
        self.fitness    = fitness

    def choose_random_ind(self):
        N, D, T, sbxdi, pmdi, pswap, rmp = self.extract_parameters()
        idx = np.random.choice(N)
        return self.population[idx, :]