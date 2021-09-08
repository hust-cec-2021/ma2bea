import os
import argparse
import numpy as np

ROOT = '../../_/result-format-2/result/'

algorithm = 'MaTGA'
benchmark_id = 1

def get_args():
    # create argument parser
    parser = argparse.ArgumentParser()
    # parameter for problem
    parser.add_argument('--algorithm', type=str, default='MaTGA')
    parser.add_argument('--benchmark_id', type=int, default=1)
    # parse args
    args = parser.parse_args()
    return args

def load_last_value(args):
    if args.benchmark_id == 0:
        path = os.path.join(ROOT,
                            '{}_Results_benchmark_10'.format(args.algorithm),
                            'Benchmark_{}'.format(args.benchmark_id),
                            'Result_{}.txt'.format(args.algorithm))
    else:
        path = os.path.join(ROOT,
                            '{}_Results_benchmark_50'.format(args.algorithm),
                            'Benchmark_{}'.format(args.benchmark_id),
                            'Result_{}.txt'.format(args.algorithm))
    line = open(path).read().strip().split('\n')[-1]
    fitness = np.array([float(_) for _ in line.strip().split(',')[1:]])
    if args.benchmark_id == 0:
        fitness = fitness.reshape(30, 10)
    else:
        fitness = fitness.reshape(30, 50)
    return fitness

def main():
    # get args
    args = get_args()

    for benchmark_id in range(0, 11):
        args.benchmark_id = benchmark_id
        fitness = load_last_value(args)
        mean = np.mean(fitness, axis=0)
        print('{} {} {} {} {:0.2f} {:0.2f}'.format(
            benchmark_id, algorithm,
            np.mean(mean), np.std(mean), np.min(mean), np.max(mean)))

if __name__ == '__main__':
    main()
