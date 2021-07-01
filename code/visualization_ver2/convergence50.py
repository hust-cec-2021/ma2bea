import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from pprint import pprint

root = '../../result/'
algorithm_path = {
    'Ma2BGA': {'folder_name': 'MTO', 'rmp': 0.3},
    'Ma2BGA': {'folder_name': 'MTO', 'rmp': 0.3},
    'MFEA': {'folder_name': 'mfea', 'rmp': 0.3},
}
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
        'sphere',
        'rosenbrock',
        'ackley',
        'rastrigin',
        'griewank',
        'weierstrass',
        'schwefel',
]
FMT = [
    '-r^',
    '-bo',
    '-g+',
    '-ks',
]

def load(benchmark_id, algorithm):
    if algorithm == 'Ma2BGA':
        folder_name = algorithm_path[algorithm]['folder_name']
        rmp         = algorithm_path[algorithm]['rmp']
        folder = os.path.join(root,
                              str(benchmark_id),
                              '{}_{:0.1f}'.format(folder_name, rmp))
        results = []
        for name in list(sorted(os.listdir(folder))):
            if 'fitness' in name:
                path = os.path.join(folder, name)
                results.append(np.load(path))
    elif algorithm == 'SBS_GA' or algorithm == 'MaTGA' or algorithm == 'MFEA':
        path = os.path.join(root,
                            algorithm,
                            str(benchmark_id),
                            'Result_{}.txt'.format(algorithm))
        results = np.array([line.split(',')[1:] for line in open(path).read().strip().split('\n')])
        results = results.reshape(1000, 30, 50)
        results = np.transpose(results, [1, 0, 2]).astype(np.float)
    return np.array(results)

def trend(results, algorithm):
    mean = np.mean(results, axis=0)
    if algorithm in ['MaTGA', 'SBS_GA']:
        idx = (np.arange(10) + 1) * 100 - 1
        mean = mean[idx, :]
    else:
        idx = (np.arange(10) + 1) * 10 - 1
        mean = mean[idx, :]
    return mean

def per_function(mean, benchmark_id, algorithm):
    # extract parameter
    functions = [FUNCTION[k] for k in BENCHMARK[benchmark_id - 1]]
    # initialize result
    results = {}
    for f in functions:
        results[f] = []
    # adding data
    for k in range(50):
        f = functions[k % len(functions)]
        results[f].append(mean[:, k])
    # stacking data
    for f in functions:
        results[f] = np.mean(np.array(results[f]), axis=0)
    return results

def plot(Results, benchmark_id):
    algorithms = [key for key in Results]
    functions  = [key for key in Results[algorithms[0]]]
    for i, f in enumerate(functions):
        fig, axes = plt.subplots(1, 1, sharex=True, figsize=(4, 3), tight_layout=True)
        for j, algorithm in enumerate(algorithms):
            x = (np.arange(10) + 1) * 100
            plt.plot(x, Results[algorithm][f], FMT[j], label=algorithm)
        plt.yscale('log')
        plt.ylabel('Fitness')
        plt.xlabel('Generation')
        # plt.savefig('../../paper/figure/experiment/convergence50/{}_{}.png'.format(benchmark_id, f), dpi=300)
        plt.savefig('../../paper/figure/experiment/convergence50/{}_{}.eps'.format(benchmark_id, f), dpi=300)
        plt.cla()

def main():
    algorithms = ['Ma2BGA', 'MFEA', 'MaTGA', 'SBS_GA']

    for benchmark_id in range(1, 11):
        print('[+] ploting', benchmark_id)
        Results = {}
        for algorithm in algorithms:
            results = load(benchmark_id, algorithm)
            mean = trend(results, algorithm)
            Results[algorithm] = per_function(mean, benchmark_id, algorithm)
        plot(Results, benchmark_id)

if __name__ == '__main__':
    main()
