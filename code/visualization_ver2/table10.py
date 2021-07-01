import os
import numpy as np
import pandas as pd

pattern = '{} &  &  &  & \\'
root = '../../result/0/'

algorithm_path = {
    'Ma2BGA': {'folder_name': 'MTO', 'rmp': 0.3},
    'Ma2BGA_random': {'folder_name': 'MTO_random', 'rmp': 0.3},
    'Ma2BGA_optimal': {'folder_name': 'MTO_optimal', 'rmp': 0.3},
    'Ma2BGA_ucb': {'folder_name': 'MTO_ucb', 'rmp': 0.3},
}

def load(algorithm):
    folder_name = algorithm_path[algorithm]['folder_name']
    rmp         = algorithm_path[algorithm]['rmp']
    folder      = os.path.join(root, '{}_{:0.1f}'.format(folder_name, rmp))
    results = []
    if 'Ma2BGA' in algorithm:
        for name in list(sorted(os.listdir(folder))):
            if 'fitness' in name:
                path = os.path.join(folder, name)
                results.append(np.load(path))
    return np.array(results)

def last_value(algorithm, results):
    mean = np.mean(results[:, -1, :], axis=0)
    return mean

def load_baseline():
    means = {
        'MFEA': [1.31e00,
                 1.27e00,
                 1.17e00,
                 3.37e00,
                 8.15e02,
                 1.99e01,
                 1.05e01,
                 2.34e03,
                 4.11e02,
                 1.54e01,],
        'MaTGA': [2.45e-04,
                  4.75e-04,
                  0,
                  1.00e-06,
                  2.16e-02,
                  3.61e-03,
                  5.57e-04,
                  3.40e-02,
                  4.52e-03,
                  1.57e+01,],
        'EBSGA': [5.58e-04,
                  6.10e-05,
                  2.00e-06,
                  1.60e-05,
                  3.29e-02,
                  8.59e-04,
                  1.60e-03,
                  4.73e-02,
                  4.58e-03,
                  3.41e+01,]
    }
    return means

def main():
    # load results
    # algorithms = ['Ma2BGA']
    algorithms = ['Ma2BGA', 'Ma2BGA_random', 'Ma2BGA_optimal']# , 'MFEA', 'MaTGA', 'SBS_GA']


    means = load_baseline()

    for algorithm in algorithms:
        results = load(algorithm)
        mean = last_value(algorithm, results)
        means[algorithm] = mean

    for i in range(10):
        # print('$T_{}$ & {:.2E} & {:.2E} & {:.2E} & {:.2E} \\\\'.format(
        #     i,
        #     means['Ma2BGA'][i],
        #     means['MFEA'][i],
        #     means['MaTGA'][i],
        #     means['EBSGA'][i]))
        print('$T_{}$ & {:.2E} & {:.2E} & {:.2E} \\\\'.format(
            i + 1,
            means['Ma2BGA'][i],
            means['Ma2BGA_random'][i],
            means['Ma2BGA_optimal'][i],
        ))

if __name__ == '__main__':
    main()
