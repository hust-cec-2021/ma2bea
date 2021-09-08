import os
import numpy as np

from .functions import *


def read_bias(path):
    return np.array([float(_) for _ in open(path).read().strip().split()])

def read_matrix(path):
    matrix = []
    for line in open(path).read().strip().split('\n'):
        row = [float(_) for _ in line.strip().split()]
        matrix.append(row)
    return np.array(matrix)


DATA_FOLDER = '../../data/WCCI-SO-Manytask-Benchmarks/Tasks'

BENCHMARK = [
        (0,),
        (1,),
        (3,),
        (0, 1, 2,),
        (3, 4, 5,),
        (1, 4, 6,),
        (2, 3, 5,),
        (1, 2, 3, 4, 5),
        (1, 2, 3, 4, 5, 6),
        (2, 3, 4, 5, 6),
]

FUNCTION = [
        sphere,
        rosenbrock,
        ackley,
        rastrigin,
        griewank,
        weierstrass,
        schwefel,
]

K = 50
D = 50

def get_functions(args):
    # data path
    folder = os.path.join(DATA_FOLDER, 'benchmark_{}'.format(args.benchmark_id))

    # get list of functions
    choice_functions = [FUNCTION[i] for i in BENCHMARK[args.benchmark_id - 1]]

    # replicate to 50 functions
    functions = []
    for k in range(K):
        f = choice_functions[k % len(choice_functions)]
        functions.append(f)

    # shift+lb+ub+rotation
    for i, f in enumerate(functions):
        print(f.__name__)
        if f.__name__ == 'sphere':
            ub = 100 * np.ones(D)
            lb = -100 * np.ones(D)
            path = os.path.join(folder, 'matrix_{}'.format(i + 1))
            M  = read_matrix(path)
            path = os.path.join(folder, 'bias_{}'.format(i + 1))
            O  = read_bias(path)
            functions[i] = lambda x: f(M @ (x * (ub - lb) + lb - O))

        elif f.__name__ == 'rosenbrock':
            ub = 50 * np.ones(D)
            lb = -50 * np.ones(D)
            path = os.path.join(folder, 'matrix_{}'.format(i + 1))
            M  = read_matrix(path)
            path = os.path.join(folder, 'bias_{}'.format(i + 1))
            O  = read_bias(path)
            functions[i] = lambda x: f(M @ (x * (ub - lb) + lb - O + 1))

        elif f.__name__ == 'ackley':
            ub = 50 * np.ones(D)
            lb = -50 * np.ones(D)
            path = os.path.join(folder, 'matrix_{}'.format(i + 1))
            M  = read_matrix(path)
            path = os.path.join(folder, 'bias_{}'.format(i + 1))
            O  = read_bias(path)
            functions[i] = lambda x: f(M @ (x * (ub - lb) + lb - O))

        elif f.__name__ == 'rastrigin':
            ub = 50 * np.ones(D)
            lb = -50 * np.ones(D)
            path = os.path.join(folder, 'matrix_{}'.format(i + 1))
            M  = read_matrix(path)
            path = os.path.join(folder, 'bias_{}'.format(i + 1))
            O  = read_bias(path)
            functions[i] = lambda x: f(M @ (x * (ub - lb) + lb - O))

        elif f.__name__ == 'griewank':
            ub = 100 * np.ones(D)
            lb = -100 * np.ones(D)
            path = os.path.join(folder, 'matrix_{}'.format(i + 1))
            M  = read_matrix(path)
            path = os.path.join(folder, 'bias_{}'.format(i + 1))
            O  = read_bias(path)
            functions[i] = lambda x: f(M @ (x * (ub - lb) + lb - O))

        elif f.__name__ == 'weierstrass':
            ub = 0.5 * np.ones(D)
            lb = -0.5 * np.ones(D)
            path = os.path.join(folder, 'matrix_{}'.format(i + 1))
            M  = read_matrix(path)
            path = os.path.join(folder, 'bias_{}'.format(i + 1))
            O  = read_bias(path)
            functions[i] = lambda x: f(M @ (x * (ub - lb) + lb - O))

        elif f.__name__ == 'schwefel':
            ub = 500 * np.ones(D)
            lb = -500 * np.ones(D)
            path = os.path.join(folder, 'matrix_{}'.format(i + 1))
            M  = read_matrix(path)
            path = os.path.join(folder, 'bias_{}'.format(i + 1))
            O  = read_bias(path)
            functions[i] = lambda x: f(M @ (x * (ub - lb) + lb - O))

    return functions

