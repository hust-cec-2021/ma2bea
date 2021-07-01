import os
import numpy as np
import pandas as pd
from scipy.stats import wilcoxon

root = '../../result/'
algorithm_path = {
    'Ma2BGA': {'folder_name': 'MTO', 'rmp': 0.3},
    'Ma2BGA_random': {'folder_name': 'MTO_random', 'rmp': 0.3},
    'Ma2BGA_ucb': {'folder_name': 'MTO_ucb', 'rmp': 0.3},
    'MFEA': {'folder_name': 'mfea', 'rmp': 0.3},
}

def load(benchmark_id, algorithm):
    if 'Ma2BGA' in algorithm:
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

def last_mean(results):
    mean = np.mean(results[:, -1, :])
    return mean

def last_mean_v2(results, k):
    mean = np.mean(results[:, -1, k])
    return mean

def last_value(results, k):
    last = results[:, -1, k]
    return last

def count_best(Results):
    count = {}
    for algorithm in Results:
        count[algorithm] = 0
    for k in range(50):
        algorithms = []
        means = []
        lasts = []
        for algorithm in Results:
            algorithms.append(algorithm)
            means.append(last_mean_v2(Results[algorithm], k))
            lasts.append(last_value(Results[algorithm], k))
        alg_1st, alg_2nd = np.argsort(means)[:2]
        x = lasts[alg_1st]
        y = lasts[alg_2nd]
        max_length = max(len(x), len(y))
        x = [x[i % len(x)] for i in range(max_length)]
        y = [y[i % len(y)] for i in range(max_length)]
        statistic, pvalue = wilcoxon(x=x, y=y)
        if pvalue < 0.05:
            count[algorithms[alg_1st]] += 1
    return count

def count_best_v2(Results):
    count = {}
    for algorithm in Results:
        count[algorithm] = 0
    for k in range(50):
        algorithms = []
        means = []
        lasts = []
        for algorithm in Results:
            algorithms.append(algorithm)
            means.append(last_mean_v2(Results[algorithm], k))
            lasts.append(last_value(Results[algorithm], k))
        alg_1st, alg_2nd = np.argsort(means)[:2]
        x = lasts[alg_1st]
        y = lasts[alg_2nd]
        max_length = max(len(x), len(y))
        x = [x[i % len(x)] for i in range(max_length)]
        y = [y[i % len(y)] for i in range(max_length)]
        statistic, pvalue = wilcoxon(x=x, y=y)
        count[algorithms[alg_1st]] += 1
    return count

def main():
    benchmark_id = 1
    algorithms = ['Ma2BGA_ucb', 'Ma2BGA_random']# , 'MFEA', 'MaTGA', 'SBS_GA']

    for benchmark_id in range(1, 11):
        # load results
        means = {}
        Results = {}
        for algorithm in algorithms:
            results = load(benchmark_id, algorithm)
            mean = last_mean(results)
            means[algorithm] = mean
            Results[algorithm] = results
        # count best
        count = count_best(Results)
        count_v2 = count_best_v2(Results)
        # # print results
        # print('$B_{}$ & {} ({}) & {} ({}) & {} ({}) & {} ({}) & {} ({}) \\\\'.format(
        #     benchmark_id,
        #     count_v2['Ma2BGA'], count['Ma2BGA'],
        #     count_v2['Ma2BGA_random'], count['Ma2BGA_random'],
        #     count_v2['MFEA'], count['MFEA'],
        #     count_v2['MaTGA'], count['MaTGA'],
        #     count_v2['SBS_GA'], count['SBS_GA'],
        # ))

        # print results
        print('$B_{}$ & {} ({}) & {} ({}) \\\\'.format(
            benchmark_id,
            count_v2['Ma2BGA_ucb'], count['Ma2BGA_ucb'],
            count_v2['Ma2BGA_random'], count['Ma2BGA_random'],
        ))


if __name__ == '__main__':
    main()
