import os
import yaml
import numpy as np
from .operators import get_best_individual
from scipy.optimize import OptimizeResult

def load_config(path='config.yaml'):
    with open(path) as fp:
        config = yaml.load(fp)
    return config


def get_optimization_results(
        t,
        population,
        factorial_cost,
        scalar_fitness,
        skill_factor,
        pairs=None,
        tasks=None):
    K = len(set(skill_factor))
    N = len(population) // 2
    results = []
    for k in range(K):
        result = OptimizeResult()
        x, fun = get_best_individual(
            population, factorial_cost, scalar_fitness, skill_factor, k)
        result.x = x
        result.fun = fun
        result.nit = t
        result.nfev = (t + 1) * N
        if pairs is not None:
            result.pair = pairs[k, :]
        else:
            result.pair = None
        if tasks is not None:
            result.ucb_value = tasks[k].ucb_solver.value
        else:
            result.ucb_value = None
        results.append(result)
    return results

ROOT = '../../result'

def create_result_folder(args):
    # folder for root
    if not os.path.exists(ROOT):
        os.mkdir(ROOT)
    # folder for benchmark
    folder = os.path.join(ROOT, '{}'.format(args.benchmark_id))
    if not os.path.exists(folder):
        os.mkdir(folder)
    # folder for algorithm
    folder = os.path.join(folder, '{}_{:0.1f}'.format(args.algorithm.__name__, args.rmp))
    if not os.path.exists(folder):
        os.mkdir(folder)
    return folder

def save(Results, args):
    folder = create_result_folder(args)
    path = os.path.join(folder, 'fitness-{}.npy'.format(args.seed))
    X = np.array([[res.fun for res in results] for results in Results])
    np.save(path, X)

    path = os.path.join(folder, 'pair-selection-{}.npy'.format(args.seed))
    X = np.array([[res.pair for res in results] for results in Results])
    np.save(path, X)

    path = os.path.join(folder, 'ucb-value-{}.npy'.format(args.seed))
    X = np.array([[res.ucb_value for res in results] for results in Results])
    np.save(path, X)
